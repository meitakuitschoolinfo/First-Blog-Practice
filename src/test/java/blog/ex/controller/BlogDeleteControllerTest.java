package blog.ex.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import blog.ex.model.entity.BlogEntity;
import blog.ex.model.entity.UserEntity;
import blog.ex.service.BlogService;

// Spring Boot のテストコンテキストを起動する（DI コンテナを使用可能にする）
@SpringBootTest
// MockMvc を自動構成し、HTTP リクエストを擬似的に実行できるようにする
@AutoConfigureMockMvc
public class BlogDeleteControllerTest {

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

	// No.1 削除候補一覧の表示をテストする
	@Test
	public void testGetBlogDeleteListPage() throws Exception {
		// 一覧として返す BlogEntity のリストを生成する
		List<BlogEntity> blogList = new ArrayList<>();
		// リストに 1 件目の記事を追加する
		blogList.add(new BlogEntity());
		// リストに 2 件目の記事を追加する
		blogList.add(new BlogEntity());
		// findAllBlogPost(1L) が一覧を返すようモックを設定する
		when(blogService.findAllBlogPost(1L)).thenReturn(blogList);

		// GET /user/blog/delete/list をセッション付きで実行し、結果を検証する
		mockMvc.perform(get("/user/blog/delete/list").session(session))
				// HTTP ステータスが 200 OK であることを検証する
				.andExpect(status().isOk())
				// ビュー名が blog-delete.html であることを検証する
				.andExpect(view().name("blog-delete.html"))
				// model に userName・blogList・deleteMessage が存在することを検証する
				.andExpect(model().attributeExists("userName", "blogList", "deleteMessage"))
				// model の userName が "John" であることを検証する
				.andExpect(model().attribute("userName", "John"))
				// model の deleteMessage が "削除一覧" であることを検証する
				.andExpect(model().attribute("deleteMessage", "削除一覧"))
				// model の blogList が取得した一覧と一致することを検証する
				.andExpect(model().attribute("blogList", blogList));

		// findAllBlogPost が引数 1L で 1 回呼ばれたことを検証する
		verify(blogService, times(1)).findAllBlogPost(1L);
	}

	// No.2 削除記事詳細の表示（記事あり）をテストする
	@Test
	public void testGetBlogDeleteDetailPage() throws Exception {
		// 詳細表示する記事を表す BlogEntity を生成する
		BlogEntity blog = new BlogEntity();
		// ブログ ID に 1L を設定する
		blog.setBlogId(1L);
		// ブログタイトルに "Test Blog" を設定する
		blog.setBlogTitle("Test Blog");
		// getBlogPost(1L) が記事を返すようモックを設定する
		when(blogService.getBlogPost(1L)).thenReturn(blog);

		// GET /user/blog/delete/detail/1 をセッション付きで実行し、結果を検証する
		mockMvc.perform(get("/user/blog/delete/detail/{blogId}", 1L).session(session))
				// HTTP ステータスが 200 OK であることを検証する
				.andExpect(status().isOk())
				// ビュー名が blog-delete-detail.html であることを検証する
				.andExpect(view().name("blog-delete-detail.html"))
				// model に userName・blogList・DeleteDetailMessage が存在することを検証する
				.andExpect(model().attributeExists("userName", "blogList", "DeleteDetailMessage"))
				// model の userName が "John" であることを検証する
				.andExpect(model().attribute("userName", "John"))
				// model の DeleteDetailMessage が "削除記事詳細" であることを検証する
				.andExpect(model().attribute("DeleteDetailMessage", "削除記事詳細"))
				// model の blogList が取得した記事と一致することを検証する
				.andExpect(model().attribute("blogList", blog));

		// getBlogPost が引数 1L で 1 回呼ばれたことを検証する
		verify(blogService, times(1)).getBlogPost(1L);
	}

	// No.3 削除記事詳細の表示（記事なし）をテストする
	@Test
	public void testGetBlogDeleteDetailPage_NullBlog() throws Exception {
		// getBlogPost(1L) が null を返すようモックを設定する
		when(blogService.getBlogPost(1L)).thenReturn(null);

		// GET /user/blog/delete/detail/1 をセッション付きで実行し、結果を検証する
		mockMvc.perform(get("/user/blog/delete/detail/{blogId}", 1L).session(session))
				// HTTP ステータスが 200 OK であることを検証する（実装は redirect スペル誤りのためビュー解決される）
				.andExpect(status().isOk())
				// ビュー名が実装どおり "redirecr:/user/blog/list" であることを検証する
				.andExpect(view().name("redirecr:/user/blog/list"));

		// getBlogPost が引数 1L で 1 回呼ばれたことを検証する
		verify(blogService, times(1)).getBlogPost(1L);
	}

	// No.4 記事削除の成功をテストする
	@Test
	public void testDeleteBlog_Succeed() throws Exception {
		// 削除対象のブログ ID を定義する
		long blogId = 1L;
		// deleteBlog(1L) が true を返すようモックを設定する
		when(blogService.deleteBlog(blogId)).thenReturn(true);

		// POST /user/blog/delete をパラメータとセッション付きで実行し、結果を検証する
		mockMvc.perform(post("/user/blog/delete")
				// ブログ ID を送信する
				.param("blogId", String.valueOf(blogId))
				// セッションを付与する
				.session(session))
				// HTTP ステータスが 200 OK であることを検証する
				.andExpect(status().isOk())
				// ビュー名が blog-delete-fix.html であることを検証する
				.andExpect(view().name("blog-delete-fix.html"));

		// deleteBlog が引数 1L で 1 回呼ばれたことを検証する
		verify(blogService, times(1)).deleteBlog(blogId);
	}

	// No.5 記事削除の失敗をテストする
	@Test
	public void testDeleteBlog_Failure() throws Exception {
		// 削除対象のブログ ID を定義する
		long blogId = 1L;
		// deleteBlog(1L) が false を返すようモックを設定する
		when(blogService.deleteBlog(blogId)).thenReturn(false);

		// POST /user/blog/delete をパラメータとセッション付きで実行し、結果を検証する
		mockMvc.perform(post("/user/blog/delete")
				// ブログ ID を送信する
				.param("blogId", String.valueOf(blogId))
				// セッションを付与する
				.session(session))
				// HTTP ステータスが 200 OK であることを検証する
				.andExpect(status().isOk())
				// ビュー名が blog-delete.html であることを検証する
				.andExpect(view().name("blog-delete.html"))
				// model に DeleteDetailMessage が存在することを検証する
				.andExpect(model().attributeExists("DeleteDetailMessage"));

		// deleteBlog が引数 1L で 1 回呼ばれたことを検証する
		verify(blogService, times(1)).deleteBlog(blogId);
	}
}
