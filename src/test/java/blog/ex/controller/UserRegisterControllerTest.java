package blog.ex.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import blog.ex.service.UserService;

// @SpringBootTest：Spring Boot アプリケーションのコンテキストを起動して単体テストを実行する
@SpringBootTest
// @AutoConfigureMockMvc：MockMvc を自動構成し、サーバを起動せずに HTTP リクエストを擬似実行できるようにする
@AutoConfigureMockMvc
public class UserRegisterControllerTest {

	// MockMvc：コントローラへ擬似的にリクエストを送るためのテスト用クライアントを注入する
	@Autowired
	private MockMvc mockMvc;

	// @MockBean：UserService をモック化し、DI コンテナの本物の Bean と差し替える
	@MockBean
	private UserService userService;

	// @BeforeEach：各テストメソッドの実行前に共通の前準備（モック設定）を行う
	@BeforeEach
	public void prepareData() {
		// 新規ユーザーの引数では createAccount が true（登録成功）を返すようモック設定する
		when(userService.createAccount(eq("John"), eq("john@test.com"), eq("password"))).thenReturn(true);
		// 既存ユーザーの引数では createAccount が false（重複）を返すようモック設定する
		when(userService.createAccount(eq("John"), eq("john@test.com"), eq("existingPassword"))).thenReturn(false);
	}

	// No1：登録画面表示テスト（GET /user/register でビュー名 register.html が返ることを検証）
	@Test
	public void testGetUserRegisterPage() throws Exception {
		// /user/register への GET リクエストを組み立てる
		RequestBuilder request = MockMvcRequestBuilders.get("/user/register");
		// リクエストを実行し、返却ビュー名が "register.html" であることを検証する
		mockMvc.perform(request).andExpect(view().name("register.html"));
	}

	// No2：正常系（新規ユーザー登録が成功し /user/login へリダイレクトすることを検証）
	@Test
	public void testRegister_Successful() throws Exception {
		// 新規ユーザー情報（userName, email, password）を付与して /user/register/process への POST リクエストを組み立てる
		RequestBuilder request = MockMvcRequestBuilders.post("/user/register/process")
				.param("userName", "John")
				.param("email", "john@test.com")
				.param("password", "password");
		// リクエストを実行し、リダイレクト先が "/user/login" であることを検証する
		mockMvc.perform(request).andExpect(redirectedUrl("/user/login"));
		// createAccount が指定引数でちょうど1回呼び出されたことを検証する
		verify(userService, times(1)).createAccount(eq("John"), eq("john@test.com"), eq("password"));
	}

	// No3：異常系（既存ユーザー＝重複登録でも Controller は /user/login へリダイレクトすることを検証）
	@Test
	public void testRegister_ExistingUser_Unsuccessful() throws Exception {
		// 既存ユーザー情報（password=existingPassword）を付与して /user/register/process への POST リクエストを組み立てる
		RequestBuilder request = MockMvcRequestBuilders.post("/user/register/process")
				.param("userName", "John")
				.param("email", "john@test.com")
				.param("password", "existingPassword");
		// リクエストを実行し、戻り値が false でもリダイレクト先が "/user/login" であることを検証する
		mockMvc.perform(request).andExpect(redirectedUrl("/user/login"));
		// createAccount が既存ユーザーの引数でちょうど1回呼び出されたことを検証する
		verify(userService, times(1)).createAccount(eq("John"), eq("john@test.com"), eq("existingPassword"));
	}
}
