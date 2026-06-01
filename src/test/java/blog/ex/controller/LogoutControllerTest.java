package blog.ex.controller;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import blog.ex.model.entity.UserEntity;
import blog.ex.service.UserService;
import jakarta.servlet.http.HttpSession;

// @SpringBootTest：Spring Boot アプリケーションのコンテキストを起動して単体テストを実行する
@SpringBootTest
// @AutoConfigureMockMvc：MockMvc を自動構成し、サーバを起動せずに HTTP リクエストを擬似実行できるようにする
@AutoConfigureMockMvc
public class LogoutControllerTest {

	// MockMvc：コントローラへ擬似的にリクエストを送るためのテスト用クライアントを注入する
	@Autowired
	private MockMvc mockMvc;

	// @MockBean：UserService をモック化する（LogoutController は直接使わないが、コンテキスト構成のため差し替える）
	@MockBean
	private UserService userService;

	// No1：正常系（GET /user/blog/logout で 3xx リダイレクトかつ /user/login へ遷移することを検証）
	@Test
	public void testLogout_Redirect() throws Exception {
		// /user/blog/logout への GET リクエストを組み立てる
		RequestBuilder request = MockMvcRequestBuilders.get("/user/blog/logout");
		// リクエストを実行し、HTTP ステータスが 3xx リダイレクトであることと、リダイレクト先が "/user/login" であることを検証する
		mockMvc.perform(request).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/user/login"));
	}

	// No2：正常系（ログイン済みセッションでログアウトするとセッションが無効化され user 属性が取得できないことを検証）
	@Test
	public void testLogout_InvalidatesSession() throws Exception {
		// ログイン済み状態を再現するため、user 属性を格納したテスト用セッションを生成する
		MockHttpSession loginSession = new MockHttpSession();
		// セッションへログイン中ユーザーを表す UserEntity を user 属性として設定する
		loginSession.setAttribute("user", new UserEntity(1L, "Akemi", "ake@test.com", "1234abcd", LocalDateTime.now()));
		// 用意したログイン済みセッションを付与して /user/blog/logout への GET リクエストを組み立てる
		RequestBuilder request = MockMvcRequestBuilders.get("/user/blog/logout").session(loginSession);
		// リクエストを実行し、リダイレクト先が "/user/login" であることを検証し、結果を取得する
		MvcResult result = mockMvc.perform(request).andExpect(redirectedUrl("/user/login")).andReturn();
		// 実行後のリクエストから（既存セッションを再生成せず）現在のセッションを取得する
		HttpSession session = result.getRequest().getSession(false);
		// session.invalidate() によりセッションが無効化されているため、user 属性が取得できず null であることを検証する
		assertNull(session == null ? null : session.getAttribute("user"));
	}
}
