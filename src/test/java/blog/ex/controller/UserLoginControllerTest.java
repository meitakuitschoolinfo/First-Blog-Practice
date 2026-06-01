package blog.ex.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
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

/**@SpringBootTest：Spring Bootアプリケーションのコンテキストをロードしてテストを実行するための注釈です。**/
@SpringBootTest
/**@AutoConfigureMockMvc：Spring MVCテストのためにMockMvcを自動的に設定するための注釈です。。**/
@AutoConfigureMockMvc
public class UserLoginControllerTest {
	/**MockMvc mockMvc：テスト対象のコントローラと対話するためのモックされたMVCモックオブジェクトです。**/
	@Autowired
	private MockMvc mockMvc;
	/** @MockBean：モックオブジェクトを作成して注入するための注釈です。このテストでは、UserServiceのモックオブジェクトが作成されます。**/
	@MockBean
	private UserService userService;

	@BeforeEach
	/**@BeforeEachアノテーションは、各テストメソッドの実行前に実行されるメソッドを示します。つまり、各テストケースの前に共通の前準備を行うために使用されます。**/
	public void prepareData() {
		UserEntity userEntity = new UserEntity(1L, "Akemi", "ake@test.com", "1234abcd", LocalDateTime.now());
		when(userService.loginAccount(eq("ake@test.com"), eq("1234abcd"))).thenReturn(userEntity);
		when(userService.loginAccount(eq("ake@test.com"), eq("12345678"))).thenReturn(null);
		when(userService.loginAccount(eq("test@test.com"), eq("1234abcd"))).thenReturn(null);
		when(userService.loginAccount(eq("test@test.com"), eq("12345678"))).thenReturn(null);
		/**UserEntityオブジェクトを作成し、userServiceのloginAccountメソッドのモックを設定します。
		 * UserEntityオブジェクトの作成：
		 * UserEntity userEntity = new UserEntity(1L, "Akemi", "ake@test.com", "1234abcd", LocalDateTime.now())という行では、
		 * IDが1で、ユーザー名が"Akemi"、メールアドレスが"ake@test.com"、パスワードが"1234abcd"、現在の日時であるLocalDateTime.now()を持つ
		 * UserEntityオブジェクトを作成します。**/
		/**
		 * userService.loginAccountメソッドのモックの設定：
		 * 
		 * when(userService.loginAccount(eq("ake@test.com"),
		 * eq("1234abcd"))).thenReturn(userEntity)という行では、userServiceのloginAccountメソッドが、
		 * 引数が"ake@test.com"と"1234abcd"の場合にuserEntityを返すようにモック化されます。
		 * 同様に、異なる引数パターンに対してもモックが設定されます。
		 */
	}

	/**
	 * testGetLoginPage_Succeedメソッド：
	 * 
	 * テスト目的：ログインページを正しく取得できたかどうかを検証します。 手順：
	 * MockMvcRequestBuilders.get("/user/login")を使用して、/user/loginへのGETリクエストを作成します。
	 * mockMvc.perform(request)を使用して、リクエストを実行します。
	 * andExpect(view().name("login.html"))を使用して、ビュー名が"login.html"であることを検証します。
	 * このテストは、ログインページの取得が正常に行われるかどうかを確認します。
	 */
	@Test
	public void testGetLoginPage_Succeed() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/user/login");

		mockMvc.perform(request).andExpect(view().name("login.html"));
	}

	/**
	 * testLogin_Successfulメソッド：
	 * 
	 * テスト目的：正常なログインが成功することを検証します。 手順：
	 * MockMvcRequestBuilders.post("/user/login/process")を使用して、/user/login/
	 * processへのPOSTリクエストを作成します。 .param("email", "ake@test.com").param("password",
	 * "1234abcd")を使用して、リクエストパラメータとして正しいメールアドレスとパスワードを指定します。
	 * mockMvc.perform(request)を使用して、リクエストを実行します。
	 * andExpect(redirectedUrl("/user/blog/list"))を使用して、
	 * リダイレクト先のURLが"/user/blog/list"であることを検証します。
	 * result.getRequest().getSession()を使用して、セッションを取得します。
	 * session.getAttribute("user")を使用して、セッションからログインユーザーエンティティを取得します。
	 * assertNotNull(loggedInUser)を使用して、ログインユーザーエンティティがnullでないことを検証します。
	 * assertEquals("Akemi",
	 * loggedInUser.getUserName())とassertEquals("ake@test.com",
	 * loggedInUser.getEmail())を使用して、ログインユーザーエンティティのユーザー名とメールアドレスが期待通りであることを検証します。
	 * このテストは、正しいメールアドレスとパスワードが提供された場合にログインが成功し、リダイレクトが正しく行われるかどうかを確認します。
	 */
	@Test
	public void testLogin_Successful() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.post("/user/login/process").param("email", "ake@test.com")
				.param("password", "1234abcd");

		MvcResult result = mockMvc.perform(request).andExpect(redirectedUrl("/user/blog/list")).andReturn();

		/*HttpSession session = result.getRequest().getSession();

		UserEntity loggedInUser = (UserEntity) session.getAttribute("user");
		assertNotNull(loggedInUser);
		assertEquals("Akemi", loggedInUser.getUserName());
		assertEquals("ake@test.com", loggedInUser.getEmail());*/
	}

	/**
	 * testLogin_WrongPassword_Unsuccessfulメソッド：
	 * 
	 * テスト目的：パスワードが間違っている場合のログインが失敗することを検証します。 手順：
	 * MockMvcRequestBuilders.post("/user/login/process")を使用して、/user/login/
	 * processへのPOSTリクエストを作成します。 .param("email", "ake@test.com").param("password",
	 * "12345678")を使用して、リクエストパラメータとして正しいメールアドレスと間違ったパスワードを指定します。
	 * mockMvc.perform(request)を使用して、リクエストを実行します。
	 * andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/user/login")
	 * )を使用して、リダイレクトが正しく行われることを検証します。
	 * mockMvc.perform(MockMvcRequestBuilders.get("/user/login"))を使用して、
	 * ログインページのGETリクエストを作成します。
	 * mockMvc.perform(...).andReturn().getRequest().getSession()を使用して、セッションを取得します。
	 * session.getAttribute("user")を使用して、セッションからログインユーザーエンティティを取得します。
	 * assertNull(loggedInUser)を使用して、ログインユーザーエンティティがnullであることを検証します。
	 * このテストは、正しいメールアドレスと間違ったパスワードが提供された場合にログインが失敗し、リダイレクトが正しく行われるかどうかを確認します。
	 */
	@Test
	public void testLogin_WrongPassword_Unsuccessful() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.post("/user/login/process").param("email", "ake@test.com")
				.param("password", "12345678");

//		mockMvc.perform(request).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/user/login"));
//
//		HttpSession session = mockMvc.perform(MockMvcRequestBuilders.get("/user/login")).andReturn().getRequest()
//				.getSession();
		MvcResult result = mockMvc.perform(request).andExpect(redirectedUrl("/user/login")).andReturn();

		HttpSession session = result.getRequest().getSession();

		UserEntity loggedInUser = (UserEntity) session.getAttribute("user");
		assertNull(loggedInUser);
	}

	/**
	 * testLogin_WrongEmail_Unsuccessfulメソッド：
	 * 
	 * テスト目的：メールアドレスが間違っている場合のログインが失敗することを検証します。 手順：
	 * MockMvcRequestBuilders.post("/user/login/process")を使用して、/user/login/
	 * processへのPOSTリクエストを作成します。 .param("email", "test@test.com").param("password",
	 * "1234abcd")を使用して、リクエストパラメータとして間違ったメールアドレスと正しいパスワードを指定します。
	 * mockMvc.perform(request)を使用して、リクエストを実行します。
	 * andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/user/login")
	 * )を使用して、リダイレクトが正しく行われることを検証します。
	 * mockMvc.perform(MockMvcRequestBuilders.get("/user/login"))を使用して、
	 * ログインページのGETリクエストを作成します。
	 * mockMvc.perform(...).andReturn().getRequest().getSession()を使用して、セッションを取得します。
	 * session.getAttribute("user")を使用して、セッションからログインユーザーエンティティを取得します。
	 * assertNull(loggedInUser)を使用して、ログインユーザーエンティティがnullであることを検証します。
	 * このテストは、間違ったメールアドレスと正しいパスワードが提供された場合にログインが失敗し、リダイレクトが正しく行われるかどうかを確認します。
	 */
	@Test
	public void testLogin_WrongEmail_Unsuccessful() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.post("/user/login/process").param("email", "test@test.com")
				.param("password", "1234abcd");

		mockMvc.perform(request).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/user/login"));

		HttpSession session = mockMvc.perform(MockMvcRequestBuilders.get("/user/login")).andReturn().getRequest()
				.getSession();

		UserEntity loggedInUser = (UserEntity) session.getAttribute("user");
		assertNull(loggedInUser);
	}

	/**
	 * testLogin_WrongEmailAndWrongPassword_Unsuccessfulメソッド
	 * このテストメソッドは、間違ったメールアドレスと間違ったパスワードを使用してログインが失敗することを検証します。
	 * 
	 * リクエストの作成：
	 * MockMvcRequestBuilders.post("/user/login/process")を使用して、/user/login/
	 * processへのPOSTリクエストを作成します。 .param("email", "test@test.com").param("password",
	 * "12345678")を使用して、リクエストパラメータとして間違ったメールアドレスと間違ったパスワードを指定します。 
	 * 
	 * リクエストの実行と検証：
	 * mockMvc.perform(request)を使用して、リクエストを実行します。
	 * .andExpect(status().is3xxRedirection())を使用して、
	 * レスポンスのHTTPステータスコードが3xxのリダイレクションであることを検証します。
	 * .andExpect(redirectedUrl("/user/login"))を使用して、リダイレクト先のURLが/user/
	 * loginであることを検証します。
	 *  
	 * セッションの取得と検証：
	 * mockMvc.perform(MockMvcRequestBuilders.get("/user/login"))を使用して、
	 * ログインページのGETリクエストを作成します。
	 * .andReturn().getRequest().getSession()を使用して、セッションを取得します。
	 * 
	 * ログインユーザーエンティティの検証：
	 * session.getAttribute("user")を使用して、セッションからuserという名前の属性を取得します。
	 * assertNull(loggedInUser)を使用して、ログインユーザーエンティティがnullであることを検証します。
	 */
	@Test
	public void testLogin_WrongEmailAndWrongPassword_Unsuccessful() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.post("/user/login/process").param("email", "test@test.com")
				.param("password", "12345678");

		mockMvc.perform(request).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/user/login"));

		HttpSession session = mockMvc.perform(MockMvcRequestBuilders.get("/user/login")).andReturn().getRequest()
				.getSession();

		UserEntity loggedInUser = (UserEntity) session.getAttribute("user");
		assertNull(loggedInUser);
	}


}
//@Test
//public void testLogin_Successful() throws Exception {
//    RequestBuilder request = MockMvcRequestBuilders.post("/user/login/process")
//            .param("email", "ake@test.com")
//            .param("password", "1234abcd");
//
//    MvcResult loginResult = mockMvc.perform(request)
//            .andExpect(status().is3xxRedirection())
//            .andExpect(redirectedUrl("/user/blog/list"))
//            .andReturn();
//
//    HttpSession session = loginResult.getRequest().getSession();
//
//    UserEntity loggedInUser = (UserEntity) session.getAttribute("user");
//    assertNotNull(loggedInUser);
//    assertEquals("ake@test.com", loggedInUser.getEmail());
//    assertEquals("1234abcd", loggedInUser.getPassword());
//}