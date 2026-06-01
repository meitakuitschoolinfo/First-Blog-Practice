package blog.ex.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import blog.ex.model.entity.BlogEntity;
import blog.ex.model.entity.UserEntity;
import blog.ex.service.BlogService;

// Spring Boot のテストコンテキストを起動する（DI コンテナを使用可能にする）
@SpringBootTest
// MockMvc を自動構成し、HTTP リクエストを擬似的に実行できるようにする
@AutoConfigureMockMvc
public class BlogEditControllerTest {

	// HTTP リクエストを擬似実行するための MockMvc を注入する
	@Autowired
	private MockMvc mockMvc;

	// BlogService をモック化してコンテナへ登録する（実際の DB アクセスを行わない）
	@MockBean
	private BlogService blogService;

	// ログインユーザー情報を保持するための擬似セッション
	private MockHttpSession session;

	// 各テストの実行前に共通のテストデータを準備する
	@BeforeEach
	public void prepareData() {
		// ログインユーザーを表す UserEntity を生成する
		UserEntity user = new UserEntity();
		// ユーザー ID に 1L を設定する
		user.setUserId(1L);
		// ユーザー名に "John" を設定する
		user.setUserName("John");
		// 擬似セッションを新規生成する
		session = new MockHttpSession();
		// セッションの "user" 属性にログインユーザーを設定する
		session.setAttribute("user", user);
	}

	// No.1 記事編集画面の表示（記事あり）をテストする
	@Test
	public void testGetBlogEditPage() throws Exception {
		// 編集対象の記事を表す BlogEntity を生成する
		BlogEntity blog = new BlogEntity();
		// ブログ ID に 1L を設定する
		blog.setBlogId(1L);
		// ブログタイトルに "Test Blog" を設定する
		blog.setBlogTitle("Test Blog");
		// 登録日に 2023-06-01 を設定する
		blog.setRegisterDate(LocalDate.parse("2023-06-01"));
		// getBlogPost(1L) が記事を返すようモックを設定する
		when(blogService.getBlogPost(1L)).thenReturn(blog);

		// GET /user/blog/edit/1 をセッション付きで実行し、結果を検証する
		mockMvc.perform(get("/user/blog/edit/{blogId}", 1L).session(session))
				// HTTP ステータスが 200 OK であることを検証する
				.andExpect(status().isOk())
				// ビュー名が blog-edit.html であることを検証する
				.andExpect(view().name("blog-edit.html"))
				// model に userName・blogList・editMessage が存在することを検証する
				.andExpect(model().attributeExists("userName", "blogList", "editMessage"))
				// model の userName が "John" であることを検証する
				.andExpect(model().attribute("userName", "John"))
				// model の editMessage が "記事編集" であることを検証する
				.andExpect(model().attribute("editMessage", "記事編集"))
				// model の blogList が取得した記事と一致することを検証する
				.andExpect(model().attribute("blogList", blog));

		// getBlogPost が引数 1L で 1 回呼ばれたことを検証する
		verify(blogService, times(1)).getBlogPost(1L);
	}

	// No.2 記事編集画面の表示（記事なし）をテストする
	@Test
	public void testGetBlogEditPage_NullBlog() throws Exception {
		// getBlogPost(1L) が null を返すようモックを設定する
		when(blogService.getBlogPost(1L)).thenReturn(null);

		// GET /user/blog/edit/1 をセッション付きで実行し、結果を検証する
		mockMvc.perform(get("/user/blog/edit/{blogId}", 1L).session(session))
				// HTTP ステータスが 200 OK であることを検証する（実装は redirect スペル誤りのためビュー解決される）
				.andExpect(status().isOk())
				// ビュー名が実装どおり "redirecr:/user/blog/list" であることを検証する
				.andExpect(view().name("redirecr:/user/blog/list"));

		// getBlogPost が引数 1L で 1 回呼ばれたことを検証する
		verify(blogService, times(1)).getBlogPost(1L);
	}

	// No.3 記事更新の成功をテストする
	@Test
	public void testBlogUpdate_Succeed() throws Exception {
		// editBlogPost が true を返すようモックを設定する
		when(blogService.editBlogPost(anyString(), any(LocalDate.class), anyString(), anyString(), anyLong(), anyLong()))
				.thenReturn(true);

		// POST /user/blog/update を各パラメータとセッション付きで実行し、結果を検証する
		mockMvc.perform(MockMvcRequestBuilders.post("/user/blog/update")
				// ブログタイトルを送信する
				.param("blogTitle", "Updated Blog")
				// 登録日を送信する
				.param("registerDate", "2023-06-02")
				// カテゴリーを送信する
				.param("category", "Updated Category")
				// ブログ詳細を送信する
				.param("blogDetail", "Updated Blog Detail")
				// ブログ ID を送信する
				.param("blogId", "1")
				// セッションを付与する
				.session(session))
				// HTTP ステータスが 200 OK であることを検証する
				.andExpect(status().isOk())
				// ビュー名が blog-edit-fix.html であることを検証する
				.andExpect(view().name("blog-edit-fix.html"));

		// editBlogPost が指定の引数で 1 回呼ばれたことを検証する
		verify(blogService, times(1)).editBlogPost(eq("Updated Blog"), eq(LocalDate.parse("2023-06-02")),
				eq("Updated Blog Detail"), eq("Updated Category"), eq(1L), eq(1L));
	}

	// No.4 記事更新の失敗をテストする
	@Test
	public void testBlogUpdate_Failure() throws Exception {
		// editBlogPost が false を返すようモックを設定する
		when(blogService.editBlogPost(anyString(), any(LocalDate.class), anyString(), anyString(), anyLong(), anyLong()))
				.thenReturn(false);

		// POST /user/blog/update を各パラメータとセッション付きで実行し、結果を検証する
		mockMvc.perform(MockMvcRequestBuilders.post("/user/blog/update")
				// ブログタイトルを送信する
				.param("blogTitle", "Updated Blog")
				// 登録日を送信する
				.param("registerDate", "2023-06-02")
				// カテゴリーを送信する
				.param("category", "Updated Category")
				// ブログ詳細を送信する
				.param("blogDetail", "Updated Blog Detail")
				// ブログ ID を送信する
				.param("blogId", "1")
				// セッションを付与する
				.session(session))
				// HTTP ステータスが 200 OK であることを検証する
				.andExpect(status().isOk())
				// ビュー名が blog-edit.html であることを検証する
				.andExpect(view().name("blog-edit.html"))
				// model に registerMessage が存在することを検証する
				.andExpect(model().attributeExists("registerMessage"));

		// editBlogPost が指定の引数で 1 回呼ばれたことを検証する
		verify(blogService, times(1)).editBlogPost(eq("Updated Blog"), eq(LocalDate.parse("2023-06-02")),
				eq("Updated Blog Detail"), eq("Updated Category"), eq(1L), eq(1L));
	}
}
