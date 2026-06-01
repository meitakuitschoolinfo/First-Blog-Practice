package blog.ex.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import blog.ex.model.entity.BlogEntity;
import blog.ex.model.entity.UserEntity;
import blog.ex.service.BlogService;



@SpringBootTest
@AutoConfigureMockMvc
public class BlogControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BlogService blogService;

	private MockHttpSession session;

	/**
	 * このメソッドは、テストの前にデータを準備するために使用されます。
	 * 
	 * メソッドの内容を解説すると以下の通りです：
	 * 
	 * UserEntity クラスのインスタンス user を作成します。 user オブジェクトの属性に値を設定します。setUserId(1L)
	 * でユーザーのIDを1に設定し、setUserName("John") でユーザー名を"John"に設定します。 BlogEntity
	 * クラスのインスタンスのリスト blogList を作成します。 blogList に新しい BlogEntity オブジェクトを2つ追加します。
	 * MockHttpSession クラスのインスタンス session を作成します。 session の属性 "user" に先ほど作成した user
	 * オブジェクトを設定します。 blogService の findAllBlogPost(1L) メソッドが呼び出された場合、事前に作成した
	 * blogList を返すように設定します。
	 */
	@BeforeEach
	public void prepareData() {
		UserEntity user = new UserEntity();
		user.setUserId(1L);
		user.setUserName("John");

		List<BlogEntity> blogList = new ArrayList<>();
		blogList.add(new BlogEntity());
		blogList.add(new BlogEntity());
		
		session = new MockHttpSession();
		session.setAttribute("user", user);

		when(blogService.findAllBlogPost(1L)).thenReturn(blogList);
		
	}

	/**
	 * このメソッドは、/user/blog/list パスへのGETリクエストをテストします。
	 * 
	 * メソッドの内容を解説すると以下の通りです：
	 * 
	 * MockMvcRequestBuilders.get("/user/blog/list")
	 * メソッドを使用して、GETリクエストを作成します。リクエストのパスは /user/blog/list です。 session
	 * オブジェクトをリクエストに設定します。これにより、リクエストはセッション情報を持つことができます。 mockMvc.perform(request)
	 * メソッドを呼び出して、モックMvcを使用してリクエストを実行します。 .andExpect(status().isOk())
	 * を呼び出して、レスポンスのHTTPステータスが200 (OK) であることを検証します。
	 * .andExpect(view().name("blog-list.html")) を呼び出して、レスポンスのビュー名が "blog-list.html"
	 * であることを検証します。 .andExpect(model().attributeExists("userName", "blogList"))
	 * を呼び出して、レスポンスのモデルに "userName" と "blogList" という属性が存在することを検証します。
	 */
	@Test
	public void testGetBlogListPage() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/user/blog/list")
				.session(session);

		mockMvc.perform(request)
		.andExpect(status().isOk())
		.andExpect(view().name("blog-list.html"))
		.andExpect(model().attributeExists("userName", "blogList"))
		.andExpect(model().attribute("userName", "John"));
	}

	/**
	 * このメソッドは、/user/blog/register パスへのGETリクエストをテストします。
	 * 
	 * メソッドの内容を解説すると以下の通りです：
	 * 
	 * UserEntity クラスのインスタンス user を作成します。 user オブジェクトの属性に値を設定します。setUserId(1L)
	 * でユーザーのIDを1に設定し、setUserName("John") でユーザー名を"John"に設定します。 session の属性 "user"
	 * に先ほど作成した user オブジェクトを設定します。
	 * mockMvc.perform(get("/user/blog/register").session(session))
	 * メソッドを呼び出して、モックMvcを使用してGETリクエストを実行します。リクエストのパスは /user/blog/register
	 * で、セッション情報もリクエストに含まれます。 .andExpect(status().isOk()) を呼び出して、レスポンスのHTTPステータスが200
	 * (OK) であることを検証します。 .andExpect(view().name("blog-register.html"))
	 * を呼び出して、レスポンスのビュー名が "blog-register.html" であることを検証します。
	 * .andExpect(model().attributeExists("userName", "registerMessage"))
	 * を呼び出して、レスポンスのモデルに "userName" と "registerMessage" という属性が存在することを検証します。
	 */
	@Test
	public void testGetBlogRegisterPage() throws Exception {
		UserEntity user = new UserEntity();
		user.setUserId(1L);
		user.setUserName("John");

		session.setAttribute("user", user);

		mockMvc.perform(get("/user/blog/register").session(session))
		.andExpect(status().isOk())
		.andExpect(view().name("blog-register.html"))
		.andExpect(model().attributeExists("userName", "registerMessage"))
		.andExpect(model().attribute("userName", "John"));

		// 他のアサーションも追加できます
		//.andExpect(model().attribute("userName", "John"))
		//.andExpect(model().attribute("registerMessage", "新規記事追加"));
	}
	
	/**
	 * このメソッドは、ブログ記事の登録が成功する場合のテストを行います。
	 * 
	 * メソッドの内容を解説すると以下の通りです：
	 * 
	 * when(blogService.createBlogPost(anyString(), any(LocalDate.class),
	 * anyString(), anyString(), anyString(), anyLong())) を呼び出して、blogService の
	 * createBlogPost メソッドのモックの動作を設定します。anyString() や any(LocalDate.class)
	 * などの引数は、任意の値が受け入れられることを意味します。また、.thenReturn(true) でメソッドの戻り値を true に設定します。
	 * MockMultipartFile を使用して blogImage
	 * という名前のファイルを作成します。ここではダミーデータとして空のバイト配列を使用しています。 mockMvc.perform()
	 * メソッドを呼び出して、モックMvcを使用してリクエストを実行します。.multipart("/user/blog/register/process")
	 * でマルチパートリクエストを指定し、.file(blogImage) でファイルをリクエストに添付します。.param()
	 * メソッドを使用して、他のフォームパラメーターを指定します。また、session オブジェクトもリクエストに含まれます。
	 * .andExpect(status().isOk()) を呼び出して、レスポンスのHTTPステータスが200 (OK) であることを検証します。
	 * .andExpect(view().name("blog-register-fix.html")) を呼び出して、レスポンスのビュー名が
	 * "blog-register-fix.html" であることを検証します。 verify(blogService,
	 * times(1)).createBlogPost(...) を呼び出して、モックの createBlogPost
	 * メソッドが指定された引数で正しく呼び出されたことを検証します。引数の値には .param() で指定した値が使用されます。
	 */
	
	@Test
	public void testBlogRegister_Succeed() throws Exception {
		// モックの動作設定
		when(blogService.createBlogPost(anyString(), any(LocalDate.class), anyString(), anyString(), anyString(), anyLong()))
		.thenReturn(true);

		String filImage = "test-image.jpg";
		String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-").format(new Date()) + filImage;
		MockMultipartFile blogImage = new MockMultipartFile("blogImage", filImage, "image/jpeg", new byte[0]);

		// テスト実行
		mockMvc.perform(MockMvcRequestBuilders.multipart("/user/blog/register/process")
				.file(blogImage)
				.param("blogTitle", "Test Blog")
				.param("registerDate", "2023-06-01")
				.param("category", "Test Category")
				.param("blogDetail", "Test Blog Detail")
				.session(session))
		.andExpect(status().isOk())
		.andExpect(view().name("blog-register-fix.html"));

		// モックのメソッドが正しく呼ばれたことを検証
		verify(blogService, times(1)).createBlogPost(eq("Test Blog"), eq(LocalDate.parse("2023-06-01")), eq(fileName), eq("Test Blog Detail"), eq("Test Category"), eq(1L));
	}
	
	/**
	 * このメソッドは、ブログ記事の登録が失敗する場合のテストを行います。
	 * 
	 * メソッドの内容を解説すると以下の通りです：
	 * 
	 * when(blogService.createBlogPost(anyString(), any(LocalDate.class),
	 * anyString(), anyString(), anyString(), anyLong())) を呼び出して、
	 * blogService のcreateBlogPost メソッドのモックの動作を設定します。anyString() や any(LocalDate.class)
	 * などの引数は、任意の値が受け入れられることを意味します。また、.thenReturn(false) でメソッドの戻り値を false に設定します。
	 * MockMultipartFile を使用して blogImage
	 * という名前のファイルを作成します。ここではダミーデータとして空のバイト配列を使用しています。 mockMvc.perform()
	 * メソッドを呼び出して、モックMvcを使用してリクエストを実行します。.multipart("/user/blog/register/process")
	 * でマルチパートリクエストを指定し、.file(blogImage) でファイルをリクエストに添付します。.param()
	 * メソッドを使用して、他のフォームパラメーターを指定します。また、session オブジェクトもリクエストに含まれます。
	 * .andExpect(status().isOk()) を呼び出して、レスポンスのHTTPステータスが200 (OK) であることを検証します。
	 * .andExpect(view().name("blog-register.html")) を呼び出して、レスポンスのビュー名が
	 * "blog-register.html" であることを検証します。
	 * .andExpect(model().attributeExists("registerMessage")) を呼び出して、レスポンスのモデルに
	 * "registerMessage" という属性が存在することを検証します。
	 */
	
	@Test
	public void testBlogRegister_Failure() throws Exception {
		// モックの動作設定
		when(blogService.createBlogPost(anyString(), any(LocalDate.class), anyString(), anyString(), anyString(), anyLong()))
		.thenReturn(false);

		String filImage = "test-image.jpg";
		String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-").format(new Date()) + filImage;
		MockMultipartFile blogImage = new MockMultipartFile("blogImage", filImage, "image/jpeg", new byte[0]);

		// テスト実行
		mockMvc.perform(MockMvcRequestBuilders.multipart("/user/blog/register/process")
				.file(blogImage)
				.param("blogTitle", "Test Blog")
				.param("registerDate", "2023-06-01")
				.param("category", "Test Category")
				.param("blogDetail", "Test Blog Detail")
				.session(session))
		.andExpect(status().isOk())
		.andExpect(view().name("blog-register.html"))
		.andExpect(model().attributeExists("registerMessage"));

		// モックのメソッドが正しく呼ばれたことを検証
		verify(blogService, times(1)).createBlogPost(eq("Test Blog"), eq(LocalDate.parse("2023-06-01")), eq(fileName), eq("Test Blog Detail"), eq("Test Category"), eq(1L));
	}
	
	/**
	 * このメソッドは、ブログ記事の編集ページにアクセスする場合のテストを行います。
	 * 
	 * メソッドの内容を解説すると以下の通りです：
	 * 
	 * UserEntity オブジェクトを作成し、userId を1L、userName を "John" に設定します。
	 * session.setAttribute("user", user) を呼び出して、session オブジェクトにユーザーオブジェクトをセットします。
	 * BlogEntity オブジェクトを作成し、blogId を1L、blogTitle を "Test Blog"、registerDate を
	 * LocalDate.parse("2023-06-01") に設定します。
	 * when(blogService.getBlogPost(1L)).thenReturn(blog) を呼び出して、blogService の
	 * getBlogPost メソッドのモックの動作を設定します。引数の 1L に対して blog オブジェクトを返すように設定されています。
	 * mockMvc.perform()
	 * メソッドを呼び出して、モックMvcを使用してリクエストを実行します。.get("/user/blog/edit/{blogId}", 1L)
	 * でGETリクエストを指定し、{blogId} を1Lに置き換えます。また、.session(session) でセッション情報をリクエストに含めます。
	 * .andExpect(status().isOk()) を呼び出して、レスポンスのHTTPステータスが200 (OK) であることを検証します。
	 * .andExpect(view().name("blog-edit.html")) を呼び出して、レスポンスのビュー名が "blog-edit.html"
	 * であることを検証します。 .andExpect(model().attributeExists("userName", "blogList",
	 * "editMessage")) を呼び出して、レスポンスのモデルに "userName"、"blogList"、"editMessage"
	 * の属性が存在することを検証します。 .andExpect(model().attribute("userName", "John"))
	 * を呼び出して、レスポンスのモデルの "userName" 属性が "John" であることを検証します。
	 * .andExpect(model().attribute("editMessage", "記事編集")) を呼び出して、レスポンスのモデルの
	 * "editMessage" 属性が "記事編集" であることを検証します。
	 * .andExpect(model().attribute("blogList", blog)) を呼び出して、レスポンスのモデルの "blogList"
	 * 属性が blog オブジェクトであることを検証します。
	 */

	@Test
	public void testGetBlogEditPage() throws Exception {
		UserEntity user = new UserEntity();
		user.setUserId(1L);
		user.setUserName("John");

		session.setAttribute("user", user);

		BlogEntity blog = new BlogEntity();
		blog.setBlogId(1L);
		blog.setBlogTitle("Test Blog");
		blog.setRegisterDate(LocalDate.parse("2023-06-01"));

		// モックの動作設定
		when(blogService.getBlogPost(1L)).thenReturn(blog);

		// テスト実行
		mockMvc.perform(get("/user/blog/edit/{blogId}", 1L).session(session))
		.andExpect(status().isOk())
		.andExpect(view().name("blog-edit.html"))
		.andExpect(model().attributeExists("userName", "blogList", "editMessage"))
		.andExpect(model().attribute("userName", "John"))
		.andExpect(model().attribute("editMessage", "記事編集"))
		.andExpect(model().attribute("blogList", blog));

		// モックのメソッドが正しく呼ばれたことを検証
		verify(blogService, times(1)).getBlogPost(1L);
	}

	/**
	 * このメソッドは、ブログ記事の編集ページにアクセスした際に、指定された blogId のブログ記事が存在しない場合のテストを行います。
	 * 
	 * メソッドの内容を解説すると以下の通りです：
	 * 
	 * UserEntity オブジェクトを作成し、userId を1L、userName を "John" に設定します。
	 * session.setAttribute("user", user) を呼び出して、session オブジェクトにユーザーオブジェクトをセットします。
	 * when(blogService.getBlogPost(1L)).thenReturn(null) を呼び出して、blogService の
	 * getBlogPost メソッドのモックの動作を設定します。引数の 1L に対して null を返すように設定されています。
	 * mockMvc.perform()
	 * メソッドを呼び出して、モックMvcを使用してリクエストを実行します。.get("/user/blog/edit/{blogId}", 1L)
	 * でGETリクエストを指定し、{blogId} を1Lに置き換えます。また、.session(session) でセッション情報をリクエストに含めます。
	 * .andExpect(status().is3xxRedirection())
	 * を呼び出して、レスポンスのHTTPステータスが3xxのリダイレクションであることを検証します。
	 * .andExpect(redirectedUrl("/user/blog/list")) を呼び出して、リダイレクト先のURLが
	 * "/user/blog/list" であることを検証します。 verify(blogService, times(1)).getBlogPost(1L)
	 * を呼び出して、blogService の getBlogPost メソッドが引数1Lで1回呼び出されたことを検証します。
	 */
//	@Test
//	public void testGetBlogEditPage_NullBlog() throws Exception {
//		UserEntity user = new UserEntity();
//		user.setUserId(1L);
//		user.setUserName("John");
//
//		session.setAttribute("user", user);
//
//		// モックの動作設定
//		when(blogService.getBlogPost(1L)).thenReturn(null);
//
//		// テスト実行
//		mockMvc.perform(get("/user/blog/edit/{blogId}", 1L).session(session))
//		.andExpect(status().is3xxRedirection())
//		.andExpect(redirectedUrl("/user/blog/list"));
//
//		// モックのメソッドが正しく呼ばれたことを検証
//		verify(blogService, times(1)).getBlogPost(1L);
//	}
	
	/**
	 * このメソッドは、ブログ記事の更新が成功した場合のテストを行います。
	 * 
	 * メソッドの内容を解説すると以下の通りです：
	 * 
	 * when(blogService.editBlogPost(anyString(), any(LocalDate.class), anyString(),
	 * anyString(), anyLong(), anyLong())) を呼び出して、blogService の editBlogPost
	 * メソッドのモックの動作を設定します。任意の文字列と日付、文字列、文字列、Long型の引数に対して true を返すように設定されています。
	 * mockMvc.perform()
	 * メソッドを呼び出して、モックMvcを使用してリクエストを実行します。.post("/user/blog/update")
	 * でPOSTリクエストを指定します。 .param() を呼び出して、リクエストのパラメータを設定します。この場合、"blogTitle",
	 * "registerDate", "category", "blogDetail", "blogId" の5つのパラメータを指定しています。
	 * .session(session) でセッション情報をリクエストに含めます。 .andExpect(status().isOk())
	 * を呼び出して、レスポンスのHTTPステータスが200 OKであることを検証します。
	 * .andExpect(view().name("blog-edit-fix.html")) を呼び出して、レスポンスのビュー名が
	 * "blog-edit-fix.html" であることを検証します。 verify(blogService,
	 * times(1)).editBlogPost(eq("Updated Blog"), eq(LocalDate.parse("2023-06-02")),
	 * eq("Updated Blog Detail"), eq("Updated Category"), eq(1L), eq(1L))
	 * を呼び出して、blogService の editBlogPost メソッドが指定された引数で1回呼び出されたことを検証します。
	 */
	@Test
	public void testBlogUpdate_Succeed() throws Exception {
		// モックの動作設定
		when(blogService.editBlogPost(anyString(), any(LocalDate.class), anyString(), anyString(), anyLong(), anyLong()))
		.thenReturn(true);

		// テスト実行
		mockMvc.perform(MockMvcRequestBuilders.post("/user/blog/update")
				.param("blogTitle", "Updated Blog")
				.param("registerDate", "2023-06-02")
				.param("category", "Updated Category")
				.param("blogDetail", "Updated Blog Detail")
				.param("blogId", "1")
				.session(session))
		.andExpect(status().isOk())
		.andExpect(view().name("blog-edit-fix.html"));

		// モックのメソッドが正しく呼ばれたことを検証
		verify(blogService, times(1)).editBlogPost(eq("Updated Blog"), eq(LocalDate.parse("2023-06-02")), eq("Updated Blog Detail"), eq("Updated Category"), eq(1L), eq(1L));
	}

	/**
	 * このメソッドは、ブログ記事の更新が失敗した場合のテストを行います。
	 * 
	 * メソッドの内容を解説すると以下の通りです：
	 * 
	 * when(blogService.editBlogPost(anyString(), any(LocalDate.class), anyString(),
	 * anyString(), anyLong(), anyLong())) を呼び出して、blogService の editBlogPost
	 * メソッドのモックの動作を設定します。任意の文字列と日付、文字列、文字列、Long型の引数に対して false を返すように設定されています。
	 * mockMvc.perform()
	 * メソッドを呼び出して、モックMvcを使用してリクエストを実行します。.post("/user/blog/update")
	 * でPOSTリクエストを指定します。 .param() を呼び出して、リクエストのパラメータを設定します。この場合、"blogTitle",
	 * "registerDate", "category", "blogDetail", "blogId" の5つのパラメータを指定しています。
	 * .session(session) でセッション情報をリクエストに含めます。 .andExpect(status().isOk())
	 * を呼び出して、レスポンスのHTTPステータスが200 OKであることを検証します。
	 * .andExpect(view().name("blog-edit.html")) を呼び出して、レスポンスのビュー名が "blog-edit.html"
	 * であることを検証します。 .andExpect(model().attributeExists("registerMessage"))
	 * を呼び出して、モデルに "registerMessage" 属性が存在することを検証します。 verify(blogService,
	 * times(1)).editBlogPost(eq("Updated Blog"), eq(LocalDate.parse("2023-06-02")),
	 * eq("Updated Blog Detail"), eq("Updated Category"), eq(1L), eq(1L))
	 * を呼び出して、blogService の editBlogPost メソッドが指定された引数で1回呼び出されたことを検証します。
	 */
//	@Test
//	public void testBlogUpdate_Failure() throws Exception {
//		// モックの動作設定
//		when(blogService.editBlogPost(anyString(), any(LocalDate.class), anyString(), anyString(), anyLong(), anyLong()))
//		.thenReturn(false);
//		
//		// テスト実行
//		mockMvc.perform(MockMvcRequestBuilders.post("/user/blog/update")
//				.param("blogTitle", "Updated Blog")
//				.param("registerDate", "2023-06-02")
//				.param("category", "Updated Category")
//				.param("blogDetail", "Updated Blog Detail")
//				.param("blogId", "1")
//				.session(session))
//		.andExpect(status().isOk())
//		.andExpect(view().name("blog-edit.html"))
//		.andExpect(model().attributeExists("registerMessage"));
//
//		// モックのメソッドが正しく呼ばれたことを検証
//		verify(blogService, times(1)).editBlogPost(eq("Updated Blog"), eq(LocalDate.parse("2023-06-02")), eq("Updated Blog Detail"), eq("Updated Category"), eq(1L), eq(1L));
//	}

	/**
	 * このメソッドは、ブログ記事の画像編集ページの表示が正常に行われるかをテストします。
	 * 
	 * メソッドの内容を解説すると以下の通りです：
	 * 
	 * UserEntity オブジェクトとして user を作成し、userId に 1L を設定し、userName に "John" を設定します。
	 * session.setAttribute("user", user) を呼び出して、セッションに user オブジェクトを設定します。
	 * BlogEntity オブジェクトとして blog を作成し、blogId に 1L を設定し、blogTitle に "Test Blog"
	 * を設定します。 when(blogService.getBlogPost(1L)).thenReturn(blog) を呼び出して、blogService
	 * の getBlogPost メソッドのモックの動作を設定します。引数に 1L を渡すと、先ほど作成した blog
	 * オブジェクトを返すように設定されています。 mockMvc.perform()
	 * メソッドを呼び出して、モックMvcを使用してリクエストを実行します。.get("/user/blog/image/edit/{blogId}", 1L)
	 * でGETリクエストを指定します。 .session(session) でセッション情報をリクエストに含めます。
	 * .andExpect(status().isOk()) を呼び出して、レスポンスのHTTPステータスが200 OKであることを検証します。
	 * .andExpect(view().name("blog-img-edit.html")) を呼び出して、レスポンスのビュー名が
	 * "blog-img-edit.html" であることを検証します。
	 * .andExpect(model().attributeExists("userName", "blogList",
	 * "editImageMessage")) を呼び出して、モデルに "userName", "blogList", "editImageMessage"
	 * の属性が存在することを検証します。 .andExpect(model().attribute("userName", "John"))
	 * を呼び出して、モデルの "userName" 属性が "John" であることを検証します。
	 * .andExpect(model().attribute("editImageMessage", "画像編集")) を呼び出して、モデルの
	 * "editImageMessage" 属性が "画像編集" であることを検証します。
	 * .andExpect(model().attribute("blogList", blog)) を呼び出して、モデルの "blogList"
	 * 属性が先ほど作成した blog オブジェクトと同じであることを検証します。 verify(blogService,
	 * times(1)).getBlogPost(1L) を呼び出して、blogService の getBlogPost メソッドが引数に 1L
	 * を渡して1回呼び出されたことを検証します。
	 */
	@Test
	public void testGetBlogEditImagePage() throws Exception {
		UserEntity user = new UserEntity();
		user.setUserId(1L);
		user.setUserName("John");

		session.setAttribute("user", user);

		BlogEntity blog = new BlogEntity();
		blog.setBlogId(1L);
		blog.setBlogTitle("Test Blog");

		// モックの動作設定
		when(blogService.getBlogPost(1L)).thenReturn(blog);

		// テスト実行
		mockMvc.perform(get("/user/blog/image/edit/{blogId}", 1L).session(session))
		.andExpect(status().isOk())
		.andExpect(view().name("blog-img-edit.html"))
		.andExpect(model().attributeExists("userName", "blogList", "editImageMessage"))
		.andExpect(model().attribute("userName", "John"))
		.andExpect(model().attribute("editImageMessage", "画像編集"))
		.andExpect(model().attribute("blogList", blog));

		// モックのメソッドが正しく呼ばれたことを検証
		verify(blogService, times(1)).getBlogPost(1L);
	}
	/**
	 * このメソッドは、ブログ記事の画像編集ページの表示が失敗した場合に正しくリダイレクトされるかをテストします。
	 * 
	 * メソッドの内容を解説すると以下の通りです：
	 * 
	 * UserEntity オブジェクトとして user を作成し、userId に 1L を設定し、userName に "John" を設定します。
	 * session.setAttribute("user", user) を呼び出して、セッションに user オブジェクトを設定します。
	 * BlogEntity オブジェクトとして blog を作成し、blogId に 1L を設定し、blogTitle に "Test Blog"
	 * を設定します。 when(blogService.getBlogPost(1L)).thenReturn(null) を呼び出して、blogService
	 * の getBlogPost メソッドのモックの動作を設定します。引数に 1L を渡すと、null を返すように設定されています。
	 * mockMvc.perform()
	 * メソッドを呼び出して、モックMvcを使用してリクエストを実行します。.get("/user/blog/image/edit/{blogId}", 1L)
	 * でGETリクエストを指定します。 .session(session) でセッション情報をリクエストに含めます。
	 * .andExpect(status().is3xxRedirection())
	 * を呼び出して、リダイレクトが行われた場合のHTTPステータスが3xx系列であることを検証します。
	 * .andExpect(redirectedUrl("/user/blog/list")) を呼び出して、リダイレクト先のURLが
	 * "/user/blog/list" であることを検証します。 verify(blogService, times(1)).getBlogPost(1L)
	 * を呼び出して、blogService の getBlogPost メソッドが引数に 1L を渡して1回呼び出されたことを検証します。
	 */
//	@Test
//	public void testGetBlogEditImagePage_fail() throws Exception {
//		UserEntity user = new UserEntity();
//		user.setUserId(1L);
//		user.setUserName("John");
//
//		session.setAttribute("user", user);
//
//		BlogEntity blog = new BlogEntity();
//		blog.setBlogId(1L);
//		blog.setBlogTitle("Test Blog");
//
//		// モックの動作設定
//		when(blogService.getBlogPost(1L)).thenReturn(null);
//
//		// テスト実行
//		mockMvc.perform(get("/user/blog/image/edit/{blogId}", 1L).session(session))
//		.andExpect(status().is3xxRedirection())
//		.andExpect(redirectedUrl("/user/blog/list"));
//
//		// モックのメソッドが正しく呼ばれたことを検証
//		verify(blogService, times(1)).getBlogPost(1L);
//	}
	
	
	
	/**
	 * このメソッドは、ブログ記事の画像の更新が成功した場合に正しく処理されるかをテストします。
	 * 
	 * メソッドの内容を解説すると以下の通りです：
	 * 
	 * MockMultipartFile オブジェクトとして blogImage を作成します。ファイル名は "test-image.jpg"
	 * で、コンテンツタイプは "image/jpeg" で、内容は空のバイト配列です。これはテストデータとして使用されます。
	 * when(blogService.editBlogImage(anyLong(), anyString(),
	 * anyLong())).thenReturn(true) を呼び出して、blogService の editBlogImage
	 * メソッドのモックの動作を設定します。任意の長整数、任意の文字列、および任意の長整数を引数に受け取る場合に、true を返すように設定されています。
	 * mockMvc.perform()
	 * メソッドを呼び出して、モックMvcを使用してリクエストを実行します。.multipart("/user/blog/image/update")
	 * でマルチパートのPOSTリクエストを指定します。 .file(blogImage) でファイルデータをリクエストに含めます。
	 * .param("blogId", "1") でリクエストパラメータ "blogId" を設定します。 .session(session)
	 * でセッション情報をリクエストに含めます。 .andExpect(status().isOk()) を呼び出して、HTTPステータスが200 (OK)
	 * であることを検証します。 .andExpect(view().name("blog-edit-fix.html")) を呼び出して、ビューの名前が
	 * "blog-edit-fix.html" であることを検証します。 verify(blogService,
	 * times(1)).editBlogImage(eq(1L), eq("test-image.jpg"), eq(1L))
	 * を呼び出して、blogService の editBlogImage メソッドが引数に 1L、"test-image.jpg"、1L
	 * を渡して1回呼び出されたことを検証します。
	 */
	
	@Test
	public void testBlogImageUpdate_Succeed() throws Exception {
		// テストデータの準備
		String filImage = "test-image.jpg";
		String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-").format(new Date()) + filImage;
		MockMultipartFile blogImage = new MockMultipartFile("blogImage", filImage, "image/jpeg", new byte[0]);

		// モックの動作設定
		when(blogService.editBlogImage(anyLong(), anyString(), anyLong())).thenReturn(true);

		// テスト実行
		mockMvc.perform(MockMvcRequestBuilders.multipart("/user/blog/image/update")
				.file(blogImage)
				.param("blogId", "1")
				.session(session))
		.andExpect(status().isOk())
		.andExpect(view().name("blog-edit-fix.html"));

		// モックのメソッドが正しく呼ばれたことを検証
		verify(blogService, times(1)).editBlogImage(eq(1L), eq(fileName), eq(1L));
	}
	/**
	 * このメソッドは、ブログ記事の画像の更新が失敗した場合に正しく処理されるかをテストします。
	 * 
	 * メソッドの内容を解説すると以下の通りです：
	 * public void testBlogImageUpdate_Failure() throws Exception:
	 * テストメソッドの定義です。testBlogImageUpdate_Failureという名前のメソッドで、Exceptionをスローする可能性があります。
	 * 
	 * MockMultipartFile blogImage = new MockMultipartFile("blogImage",
	 * "test-image.jpg", "image/jpeg", new byte[0]):
	 * MockMultipartFileを使用して、テストデータとしてblogImageという名前のファイルを作成しています。ファイル名はtest-image.
	 * jpg、コンテンツタイプはimage/jpeg、中身は空のバイト配列です。
	 * 
	 * when(blogService.editBlogImage(anyLong(), anyString(),
	 * anyLong())).thenReturn(false):
	 * blogService.editBlogImage()メソッドが呼び出された場合にfalseを返すように、
	 * blogServiceモックの動作が設定されています。このモックは、後でメソッドが正しく呼び出されたかどうかを検証するために使用されます。
	 * 
	 * mockMvc.perform(MockMvcRequestBuilders.multipart("/user/blog/image/update")..
	 * .):
	 * mockMvcを使用して、HTTPリクエストをシミュレートします。multipart("/user/blog/image/update")は、/user/
	 * blog/image/updateエンドポイントに対するマルチパートフォームのPOSTリクエストを作成しています。.file(blogImage)は、
	 * リクエストにblogImageファイルを添付しています。.param("blogId",
	 * "1")は、リクエストパラメーターとしてblogIdを設定しています。.session(session)は、セッションをリクエストに追加しています。
	 * 
	 * .andExpect(status().isOk()): レスポンスのステータスコードが200（成功）であることを検証しています。
	 * 
	 * .andExpect(view().name("blog-img-edit.html")):
	 * レスポンスのビュー名がblog-img-edit.htmlであることを検証しています。
	 * 
	 * .andExpect(model().attributeExists("editImageMessage")):
	 * レスポンスのモデル属性にeditImageMessageという属性が存在することを検証しています。
	 * 
	 * verify(blogService, times(1)).editBlogImage(eq(1L), eq("test-image.jpg"),
	 * eq(1L)):
	 * blogService.editBlogImage()メソッドが引数1L、"test-image.jpg"、1Lで正しく呼び出されたことを検証しています。
	 * times(1)は、メソッドの呼び出し回数が1回であることを指定しています
	 */

//	@Test
//	public void testBlogImageUpdate_Failure() throws Exception {
//		// テストデータの準備
//		MockMultipartFile blogImage = new MockMultipartFile("blogImage", "test-image.jpg", "image/jpeg", new byte[0]);
//
//		// モックの動作設定
//		when(blogService.editBlogImage(anyLong(), anyString(), anyLong())).thenReturn(false);
//
//		// テスト実行
//		mockMvc.perform(MockMvcRequestBuilders.multipart("/user/blog/image/update")
//				.file(blogImage)
//				.param("blogId", "1")
//				.session(session))
//		.andExpect(status().isOk())
//		.andExpect(view().name("blog-img-edit.html"))
//		.andExpect(model().attributeExists("editImageMessage"));
//
//		// モックのメソッドが正しく呼ばれたことを検証
//		verify(blogService, times(1)).editBlogImage(eq(1L), eq("test-image.jpg"), eq(1L));
//	}
	
	/**
	 * このメソッドは、ブログ削除一覧記事のページが正しく処理されるかをテストします。
	 * 
	 * メソッドの内容を解説すると以下の通りです：
	 * public void testGetBlogDeleteListPage() throws Exception:
	 * テストメソッドの定義です。testGetBlogDeleteListPageという名前のメソッドで、Exceptionをスローする可能性があります。
	 * 
	 * UserEntity user = new UserEntity(): UserEntityオブジェクトの作成です。
	 * 
	 * user.setUserId(1L): UserEntityオブジェクトのuserIdフィールドに1Lを設定しています。
	 * 
	 * user.setUserName("John"): UserEntityオブジェクトのuserNameフィールドに"John"を設定しています。
	 * 
	 * session.setAttribute("user", user):
	 * sessionオブジェクトに"user"という名前でuserオブジェクトを設定しています。
	 * 
	 * List<BlogEntity> blogList = new ArrayList<>():
	 * BlogEntityオブジェクトのリストであるblogListを作成しています。
	 * 
	 * blogList.add(new BlogEntity())とblogList.add(new BlogEntity()):
	 * blogListに2つのBlogEntityオブジェクトを追加しています。
	 * 
	 * when(blogService.findAllBlogPost(1L)).thenReturn(blogList):
	 * blogService.findAllBlogPost()メソッドが引数1Lで呼び出された場合に、
	 * blogListを返すようにモックの動作が設定されています。
	 * 
	 * mockMvc.perform(get("/user/blog/delete/list").session(session)):
	 * mockMvcを使用して、HTTP
	 * GETリクエストをシミュレートします。get("/user/blog/delete/list")は、/user/blog/delete/
	 * listエンドポイントに対するGETリクエストを作成しています。.session(session)は、リクエストにセッションを追加しています。
	 * 
	 * .andExpect(status().isOk()): レスポンスのステータスコードが200（成功）であることを検証しています。
	 * 
	 * .andExpect(view().name("blog-delete.html")):
	 * レスポンスのビュー名がblog-delete.htmlであることを検証しています。
	 * 
	 * .andExpect(model().attributeExists("userName", "blogList", "deleteMessage")):
	 * レスポンスのモデルにuserName、blogList、deleteMessageという属性が存在することを検証しています。
	 * 
	 * .andExpect(model().attribute("userName", "John")):
	 * レスポンスのモデルのuserName属性が"John"であることを検証しています。
	 * 
	 * .andExpect(model().attribute("deleteMessage", "削除一覧")):
	 * レスポンスのモデルのdeleteMessage属性が"削除一覧"であることを検証しています。
	 * 
	 * .andExpect(model().attribute("blogList", blogList)):
	 * レスポンスのモデルのblogList属性がblogListと同じであることを検証しています。
	 * 
	 * verify(blogService, times(1)).findAllBlogPost(1L):
	 * blogService.findAllBlogPost()メソッドが引数1Lで正しく呼び出されたことを検証しています。times(1)は、
	 * メソッドの呼び出し回数が1回であることを指定しています。
	 */
	@Test
	public void testGetBlogDeleteListPage() throws Exception {
		UserEntity user = new UserEntity();
		user.setUserId(1L);
		user.setUserName("John");

		session.setAttribute("user", user);

		List<BlogEntity> blogList = new ArrayList<>();
		blogList.add(new BlogEntity());
		blogList.add(new BlogEntity());

		when(blogService.findAllBlogPost(1L)).thenReturn(blogList);

		mockMvc.perform(get("/user/blog/delete/list").session(session))
		.andExpect(status().isOk())
		.andExpect(view().name("blog-delete.html"))
		.andExpect(model().attributeExists("userName", "blogList", "deleteMessage"))
		.andExpect(model().attribute("userName", "John"))
		.andExpect(model().attribute("deleteMessage", "削除一覧"))
		.andExpect(model().attribute("blogList", blogList));

		verify(blogService, times(1)).findAllBlogPost(1L);
	}

	/**
	 * このテストは、/user/blog/delete/detail/{blogId}
	 * に対するGETリクエストが正常に処理されるかどうかを検証しています 
	 * public void
	 * testGetBlogDeleteDetailPage() throws Exception:
	 * テストメソッドの定義です。testGetBlogDeleteDetailPageという名前のメソッドで、Exceptionをスローする可能性があります。
	 * 
	 * UserEntity user = new UserEntity(): UserEntityオブジェクトの作成です。
	 * 
	 * user.setUserId(1L): UserEntityオブジェクトのuserIdフィールドに1Lを設定しています。
	 * 
	 * user.setUserName("John"): UserEntityオブジェクトのuserNameフィールドに"John"を設定しています。
	 * 
	 * session.setAttribute("user", user):
	 * sessionオブジェクトに"user"という名前でuserオブジェクトを設定しています。
	 * 
	 * BlogEntity blog = new BlogEntity(): BlogEntityオブジェクトの作成です。
	 * 
	 * blog.setBlogId(1L): BlogEntityオブジェクトのblogIdフィールドに1Lを設定しています。
	 * 
	 * blog.setBlogTitle("Test Blog"):
	 * BlogEntityオブジェクトのblogTitleフィールドに"Test Blog"を設定しています。
	 * 
	 * when(blogService.getBlogPost(1L)).thenReturn(blog):
	 * blogService.getBlogPost()メソッドが引数1Lで呼び出された場合に、blogを返すようにモックの動作が設定されています。
	 * 
	 * mockMvc.perform(get("/user/blog/delete/detail/{blogId}",
	 * 1L).session(session)): mockMvcを使用して、HTTP
	 * GETリクエストをシミュレートします。get("/user/blog/delete/detail/{blogId}",
	 * 1L)は、/user/blog/delete/detail/1というパスに対するGETリクエストを作成しています。
	 * 
	 * .andExpect(status().isOk()): レスポンスのステータスコードが200（成功）であることを検証しています。
	 * 
	 * .andExpect(view().name("blog-delete-detail.html")):
	 * レスポンスのビュー名がblog-delete-detail.htmlであることを検証しています。
	 * 
	 * .andExpect(model().attributeExists("userName", "blogList")):
	 * レスポンスのモデルにuserNameとblogListという属性が存在することを検証しています。
	 * 
	 * .andExpect(model().attribute("userName", "John")):
	 * レスポンスのモデルのuserName属性が"John"であることを検証しています。
	 * 
	 * .andExpect(model().attribute("blogList", blog)):
	 * レスポンスのモデルのblogList属性がblogと同じであることを検証しています。
	 * 
	 * verify(blogService, times(1)).getBlogPost(1L):
	 * blogService.getBlogPost()メソッドが引数1Lで正しく呼び出されたことを検証しています。times(1)は、
	 * メソッドの呼び出し回数が1回であることを指定しています。
	 */
	@Test
	public void testGetBlogDeleteDetailPage() throws Exception {
	    UserEntity user = new UserEntity();
	    user.setUserId(1L);
	    user.setUserName("John");

	    session.setAttribute("user", user);

	    BlogEntity blog = new BlogEntity();
	    blog.setBlogId(1L);
	    blog.setBlogTitle("Test Blog");

	    // モックの動作設定
	    when(blogService.getBlogPost(1L)).thenReturn(blog);

	    // テスト実行
	    mockMvc.perform(get("/user/blog/delete/detail/{blogId}", 1L).session(session))
	            .andExpect(status().isOk())
	            .andExpect(view().name("blog-delete-detail.html"))
	            .andExpect(model().attributeExists("userName", "blogList"))
	            .andExpect(model().attribute("userName", "John"))
	            .andExpect(model().attribute("blogList", blog));

	    // モックのメソッドが正しく呼ばれたことを検証
	    verify(blogService, times(1)).getBlogPost(1L);
	}

	/**
	 * このテストは、/user/blog/delete/detail/{blogId}エンドポイントに対するGETリクエストがblogService.
	 * getBlogPost()メソッドがnullを返す場合、リダイレクトが正しく行われるかどうかを検証しています。また、
	 * テストが呼び出された場合にblogService.getBlogPost()メソッドが期待どおりに呼び出されるかどうかも検証しています。 public
	 * void testGetBlogDeleteDetailPage_NullBlog() throws Exception:
	 * テストメソッドの定義です。testGetBlogDeleteDetailPage_NullBlogという名前のメソッドで、
	 * Exceptionをスローする可能性があります。
	 * 
	 * UserEntity user = new UserEntity(): UserEntityオブジェクトの作成です。
	 * 
	 * user.setUserId(1L): UserEntityオブジェクトのuserIdフィールドに1Lを設定しています。
	 * 
	 * user.setUserName("John"): UserEntityオブジェクトのuserNameフィールドに"John"を設定しています。
	 * 
	 * session.setAttribute("user", user):
	 * sessionオブジェクトに"user"という名前でuserオブジェクトを設定しています。
	 * 
	 * when(blogService.getBlogPost(1L)).thenReturn(null):
	 * blogService.getBlogPost()メソッドが引数1Lで呼び出された場合に、nullを返すようにモックの動作が設定されています。
	 * 
	 * mockMvc.perform(get("/user/blog/delete/detail/{blogId}",
	 * 1L).session(session)): mockMvcを使用して、HTTP
	 * GETリクエストをシミュレートします。get("/user/blog/delete/detail/{blogId}",
	 * 1L)は、/user/blog/delete/detail/1というパスに対するGETリクエストを作成しています。
	 * 
	 * .andExpect(status().is3xxRedirection()):
	 * レスポンスのステータスコードが3xxリダイレクトであることを検証しています。
	 * 
	 * .andExpect(redirectedUrl("/user/blog/list")):
	 * レスポンスが/user/blog/listにリダイレクトされることを検証しています。
	 * 
	 * verify(blogService, times(1)).getBlogPost(1L):
	 * blogService.getBlogPost()メソッドが引数1Lで正しく呼び出されたことを検証しています。times(1)は、
	 * メソッドの呼び出し回数が1回であることを指定しています。
	 */
//	@Test
//	public void testGetBlogDeleteDetailPage_NullBlog() throws Exception {
//	    UserEntity user = new UserEntity();
//	    user.setUserId(1L);
//	    user.setUserName("John");
//
//	    session.setAttribute("user", user);
//
//	    when(blogService.getBlogPost(1L)).thenReturn(null);
//
//	    mockMvc.perform(get("/user/blog/delete/detail/{blogId}", 1L)
//	            .session(session))
//	            .andExpect(status().is3xxRedirection())
//	            .andExpect(redirectedUrl("/user/blog/list"));
//
//	    verify(blogService, times(1)).getBlogPost(1L);
//	}
	@Test
	public void testDeleteBlog_Succeed() throws Exception {
		// テストデータの準備
		long blogId = 1L;

		// モックの動作設定
		when(blogService.deleteBlog(blogId)).thenReturn(true);

		// テスト実行
		mockMvc.perform(post("/user/blog/delete")
				.param("blogId", String.valueOf(blogId))
				.session(session))
			.andExpect(status().isOk())
			.andExpect(view().name("blog-delete-fix.html"));

		// モックのメソッドが正しく呼ばれたことを検証
		verify(blogService, times(1)).deleteBlog(blogId);
	}

	/**
	 * このテストは、/user/blog/deleteエンドポイントに対するPOSTリクエストがblogService.deleteBlog()
	 * メソッドがtrueを返す場合、正常に実行されることを検証しています public void testDeleteBlog_Succeed() throws
	 * Exception:
	 * テストメソッドの定義です。testDeleteBlog_Succeedという名前のメソッドで、Exceptionをスローする可能性があります。
	 * 
	 * long blogId = 1L: テストデータとしてのblogIdの準備です。blogIdには1Lが設定されています。
	 * 
	 * when(blogService.deleteBlog(blogId)).thenReturn(true):
	 * blogService.deleteBlog()メソッドが引数blogIdで呼び出された場合に、trueを返すようにモックの動作が設定されています。
	 * 
	 * mockMvc.perform(post("/user/blog/delete") .param("blogId",
	 * String.valueOf(blogId)) .session(session)): mockMvcを使用して、HTTP
	 * POSTリクエストをシミュレートします。post("/user/blog/delete")は、/user/blog/
	 * deleteというパスに対するPOSTリクエストを作成しています。
	 * 
	 * .param("blogId", String.valueOf(blogId)): リクエストのパラメータとしてblogIdを設定しています。
	 * 
	 * .session(session): リクエストにセッションを追加しています。
	 * 
	 * .andExpect(status().isOk()): レスポンスのステータスコードが200（成功）であることを検証しています。
	 * 
	 * .andExpect(view().name("blog-delete-fix.html")):
	 * レスポンスのビュー名がblog-delete-fix.htmlであることを検証しています。
	 * 
	 * verify(blogService, times(1)).deleteBlog(blogId):
	 * blogService.deleteBlog()メソッドが引数blogIdで正しく呼び出されたことを検証しています。times(1)は、
	 * メソッドの呼び出し回数が1回であることを指定しています。
	 */
	@Test
	public void testDeleteBlog_Failure() throws Exception {
		// テストデータの準備
		long blogId = 1L;

		// モックの動作設定
		when(blogService.deleteBlog(blogId)).thenReturn(false);

		// テスト実行
		mockMvc.perform(post("/user/blog/delete")
				.param("blogId", String.valueOf(blogId))
				.session(session))
			.andExpect(status().isOk())
			.andExpect(view().name("blog-delete.html"))
			.andExpect(model().attributeExists("DeleteDetailMessage"));

		// モックのメソッドが正しく呼ばれたことを検証
		verify(blogService, times(1)).deleteBlog(blogId);
	}
	
	/**
	 * 以下は、ログアウトが正常に行われているかを行うテストになります。
	 * public void testLogout() throws Exception:
	 * テストメソッドの定義です。testLogoutという名前のメソッドで、Exceptionをスローする可能性があります。
	 * 
	 * session = new MockHttpSession(): MockHttpSessionを使用して、新しいセッションオブジェクトを作成します。
	 * 
	 * mockMvc.perform(get("/user/blog/logout").session(session)): mockMvcを使用して、HTTP
	 * GETリクエストをシミュレートします。get("/user/blog/logout")は、/user/blog/
	 * logoutというパスに対するGETリクエストを作成しています。
	 * 
	 * .andExpect(status().is3xxRedirection()):
	 * レスポンスのステータスコードが3xxリダイレクトであることを検証しています。
	 * 
	 * .andExpect(redirectedUrl("/user/login")):
	 * レスポンスが/user/loginにリダイレクトされることを検証しています。
	 */
	@Test
	public void testLogout() throws Exception {
	    // セッションを再作成する
	    session = new MockHttpSession();
	    mockMvc.perform(get("/user/blog/logout").session(session))
	            .andExpect(status().is3xxRedirection())
	            .andExpect(redirectedUrl("/user/login"));
	}
	/**
	 * -----------------------------補足事項--------------------------------
	 * .andExpect(status().is3xxRedirection())の部分を詳しく解説します。
	 * 
	 * .andExpect(status().is3xxRedirection())は、
	 * mockMvcを使用して行われたHTTPリクエストのレスポンスステータスコードを検証しています。具体的には、
	 * 3xxリダイレクトのステータスコードであることを検証しています。
	 * 
	 * HTTPステータスコードは、クライアントがサーバから受け取るレスポンスの状態を示す3桁の数字です。3xx系列のステータスコードはリダイレクトを示し、
	 * リクエストを完了させるためにクライアントが別のリソースに転送されることを示します。
	 * 
	 * .andExpect(status().is3xxRedirection())は、テストの期待結果として、
	 * レスポンスのステータスコードが3xxリダイレクションであることを指定しています。このアサーションが成功すると、テストは合格となります。
	 * 
	 * 例えば、ステータスコードが302（Found）や303（See
	 * Other）など、3xx系列のリダイレクトステータスコードが返された場合、このアサーションは成功します。逆に、
	 * ステータスコードが3xxリダイレクトではない場合、このアサーションは失敗します。
	 */

	
}