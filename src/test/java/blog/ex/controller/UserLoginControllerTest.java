package blog.ex.controller;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import blog.ex.model.entity.UserEntity;
import blog.ex.service.UserService;
import jakarta.servlet.http.HttpSession;

// @SpringBootTest：Spring Boot アプリケーションのコンテキストを起動して結合的に単体テストを実行する
@SpringBootTest
// @AutoConfigureMockMvc：MockMvc を自動構成し、サーバを起動せずに HTTP リクエストを擬似実行できるようにする
@AutoConfigureMockMvc
public class UserLoginControllerTest {

	// MockMvc：コントローラへ擬似的にリクエストを送るためのテスト用クライアントを注入する
	@Autowired
	private MockMvc mockMvc;

	// @MockBean：UserService をモック化し、DI コンテナの本物の Bean と差し替える
	@MockBean
	private UserService userService;

	// @BeforeEach：各テストメソッドの実行前に共通の前準備（モック設定）を行う
	@BeforeEach
	public void prepareData() {
		// テストで返却させるログイン成功用の UserEntity を生成する（id, 名前, email, password, 登録日時）
		UserEntity userEntity = new UserEntity(1L, "Akemi", "ake@test.com", "1234abcd", LocalDateTime.now());
		// 正しい email と password の組み合わせでは UserEntity を返すようモック設定する（ログイン成功）
		when(userService.loginAccount(eq("ake@test.com"), eq("1234abcd"))).thenReturn(userEntity);
		// 正しい email・誤った password では null を返すようモック設定する（ログイン失敗）
		when(userService.loginAccount(eq("ake@test.com"), eq("12345678"))).thenReturn(null);
		// 誤った email・正しい password では null を返すようモック設定する（ログイン失敗）
		when(userService.loginAccount(eq("test@test.com"), eq("1234abcd"))).thenReturn(null);
		// 誤った email・誤った password では null を返すようモック設定する（ログイン失敗）
		when(userService.loginAccount(eq("test@test.com"), eq("12345678"))).thenReturn(null);
	}

	// No1：ログイン画面表示テスト（GET /user/login でビュー名 login.html が返ることを検証）
	@Test
	public void testGetLoginPage_Succeed() throws Exception {
		// /user/login への GET リクエストを組み立てる
		RequestBuilder request = MockMvcRequestBuilders.get("/user/login");
		// リクエストを実行し、返却ビュー名が "login.html" であることを検証する
		mockMvc.perform(request).andExpect(view().name("login.html"));
	}

	// No2：正常系（正しい email+password でログイン成功し /user/blog/list へリダイレクトすることを検証）
	@Test
	public void testLogin_Successful() throws Exception {
		// 正しい email と password を付与して /user/login/process への POST リクエストを組み立てる
		RequestBuilder request = MockMvcRequestBuilders.post("/user/login/process").param("email", "ake@test.com")
				.param("password", "1234abcd");
		// リクエストを実行し、リダイレクト先が "/user/blog/list" であることを検証し、結果を取得する
		MvcResult result = mockMvc.perform(request).andExpect(redirectedUrl("/user/blog/list")).andReturn();
		// 実行結果からセッションを取得する
		HttpSession session = result.getRequest().getSession();
		// ログイン成功後のセッションに格納されている user 属性を取り出す（参照のみ）
		session.getAttribute("user");
	}

	// No3：異常系（誤った email・正しい password でログイン失敗し /user/login へリダイレクトすることを検証）
	@Test
	public void testLogin_WrongEmail_Unsuccessful() throws Exception {
		// 誤った email と正しい password を付与して /user/login/process への POST リクエストを組み立てる
		RequestBuilder request = MockMvcRequestBuilders.post("/user/login/process").param("email", "test@test.com")
				.param("password", "1234abcd");
		// リクエストを実行し、3xx リダイレクトかつリダイレクト先が "/user/login" であることを検証する
		mockMvc.perform(request).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/user/login"));
		// ログイン画面を GET し直してセッションを取得する
		HttpSession session = mockMvc.perform(MockMvcRequestBuilders.get("/user/login")).andReturn().getRequest()
				.getSession();
		// セッションから user 属性を取得する
		UserEntity loggedInUser = (UserEntity) session.getAttribute("user");
		// ログイン失敗のため user 属性が null であることを検証する
		assertNull(loggedInUser);
	}

	// No4：異常系（正しい email・誤った password でログイン失敗し /user/login へリダイレクトすることを検証）
	@Test
	public void testLogin_WrongPassword_Unsuccessful() throws Exception {
		// 正しい email と誤った password を付与して /user/login/process への POST リクエストを組み立てる
		RequestBuilder request = MockMvcRequestBuilders.post("/user/login/process").param("email", "ake@test.com")
				.param("password", "12345678");
		// リクエストを実行し、リダイレクト先が "/user/login" であることを検証し、結果を取得する
		MvcResult result = mockMvc.perform(request).andExpect(redirectedUrl("/user/login")).andReturn();
		// 実行結果からセッションを取得する
		HttpSession session = result.getRequest().getSession();
		// セッションから user 属性を取得する
		UserEntity loggedInUser = (UserEntity) session.getAttribute("user");
		// ログイン失敗のため user 属性が null であることを検証する
		assertNull(loggedInUser);
	}

	// No5：異常系（email・password 両方誤りでログイン失敗し /user/login へリダイレクトすることを検証）
	@Test
	public void testLogin_WrongEmailAndWrongPassword_Unsuccessful() throws Exception {
		// 誤った email と誤った password を付与して /user/login/process への POST リクエストを組み立てる
		RequestBuilder request = MockMvcRequestBuilders.post("/user/login/process").param("email", "test@test.com")
				.param("password", "12345678");
		// リクエストを実行し、3xx リダイレクトかつリダイレクト先が "/user/login" であることを検証する
		mockMvc.perform(request).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/user/login"));
		// ログイン画面を GET し直してセッションを取得する
		HttpSession session = mockMvc.perform(MockMvcRequestBuilders.get("/user/login")).andReturn().getRequest()
				.getSession();
		// セッションから user 属性を取得する
		UserEntity loggedInUser = (UserEntity) session.getAttribute("user");
		// ログイン失敗のため user 属性が null であることを検証する
		assertNull(loggedInUser);
	}

	// No6：表示テスト（初期表示の /user/login ではセッションに user 属性が無い＝入力欄が空白状態であることを検証）
	@Test
	public void testGetLoginPage_InitialSessionEmpty() throws Exception {
		// 何も操作していない状態で /user/login を GET し、セッションを取得する
		HttpSession session = mockMvc.perform(MockMvcRequestBuilders.get("/user/login")).andReturn().getRequest()
				.getSession();
		// セッションから user 属性を取得する
		UserEntity loggedInUser = (UserEntity) session.getAttribute("user");
		// 初期表示のためログインユーザーが格納されておらず null であることを検証する
		assertNull(loggedInUser);
	}
}
