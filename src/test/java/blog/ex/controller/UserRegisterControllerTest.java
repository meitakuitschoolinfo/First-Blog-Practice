package blog.ex.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import blog.ex.model.entity.UserEntity;
import blog.ex.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
public class UserRegisterControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@BeforeEach
	public void prepareData() {
		when(userService.createAccount(eq("John"), eq("john@test.com"), eq("password"))).thenReturn(true);
		when(userService.createAccount(eq("John"), eq("john@test.com"), eq("existingPassword"))).thenReturn(false);
	}

	/**
	 * testGetUserRegisterPage()メソッドでは、/user/registerへのGETリクエストを作成しています。
	 * MockMvcRequestBuilders.get("/user/register")は、/user/
	 * registerへのGETリクエストを作成するためのビルダーメソッドです。 mockMvc.perform(request)を使用してリクエストを実行し、
	 * その結果として返されるビュー名が"register.html"であることを検証しています。
	 * つまり、このテストは、ユーザー登録ページを正常に取得できるかどうかを検証しています。
	 */
	@Test
	public void testGetUserRegisterPage() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/user/register");

		mockMvc.perform(request).andExpect(view().name("register.html"));
	}

	/**
	 * testRegister_Successful()メソッドでは、/user/register/processへのPOSTリクエストを作成しています。
	 * MockMvcRequestBuilders.post("/user/register/process")は、/user/register/
	 * processへのPOSTリクエストを作成するためのビルダーメソッドです。 
	 * .param("userName","John")、.param("email", "john@test.com")、.param("password","password")は、
	 * リクエストパラメータを設定しています。 mockMvc.perform(request)を使用してリクエストを実行し、
	 * その結果として返されるリダイレクト先のURLが"/user/login"であることを検証しています。 
	 * verify(userService,
	 * times(1)).createAccount(eq("John"), eq("john@test.com"),
	 * eq("password"))を使用して、
	 * userServiceのcreateAccountメソッドが指定された引数で1回呼び出されたことを検証しています。
	 * つまり、このテストは、正常なユーザー登録が行われた場合に正しいリダイレクトが行われるかどうかを検証しています。
	 */
	@Test
	public void testRegister_Successful() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.post("/user/register/process")
				.param("userName", "John")
				.param("email", "john@test.com")
				.param("password", "password");
		mockMvc.perform(request).andExpect(redirectedUrl("/user/login"));
		verify(userService, times(1)).createAccount(eq("John"), eq("john@test.com"), eq("password"));
	}
	/**
	 * testRegister_ExistingUser_Unsuccessful()メソッドでは、
	 * 既に存在するユーザーの情報を使用して/user/register/processへのPOSTリクエストを作成しています。
	 * MockMvcRequestBuilders.post("/user/register/process")は、
	 * /user/register/processへのPOSTリクエストを作成するためのビルダーメソッドです。 
	 * .param("userName","John")、.param("email", "john@test.com")、
	 * .param("password","existingPassword")は、リクエストパラメータを設定しています。
	 * mockMvc.perform(request)を使用してリクエストを実行し、
	 * その結果として返されるビュー名が"register.html"であることを検証しています。 
	 * verify(userService,times(1)).createAccount(eq("John"), eq("john@test.com"),eq("existingPassword"))を使用して、
	 * userServiceのcreateAccountメソッドが指定された引数で1回呼び出されたことを検証しています。
	 * つまり、このテストは、既に存在するユーザー情報での登録が行われた場合に適切なビューが表示されるかどうかを検証しています。
	 */

}
