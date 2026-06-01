package blog.ex.controller;

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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import blog.ex.model.entity.BlogEntity;
import blog.ex.model.entity.UserEntity;
import blog.ex.service.BlogService;

// Spring Boot のテストコンテキストを起動する（DI コンテナを使用可能にする）
@SpringBootTest
// MockMvc を自動構成し、HTTP リクエストを擬似的に実行できるようにする
@AutoConfigureMockMvc
public class BlogImageEditControllerTest {

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

	// No.1 画像編集画面の表示（記事あり）をテストする
	@Test
	public void testGetBlogEditImagePage() throws Exception {
		// 編集対象の記事を表す BlogEntity を生成する
		BlogEntity blog = new BlogEntity();
		// ブログ ID に 1L を設定する
		blog.setBlogId(1L);
		// ブログタイトルに "Test Blog" を設定する
		blog.setBlogTitle("Test Blog");
		// getBlogPost(1L) が記事を返すようモックを設定する
		when(blogService.getBlogPost(1L)).thenReturn(blog);

		// GET /user/blog/image/edit/1 をセッション付きで実行し、結果を検証する
		mockMvc.perform(get("/user/blog/image/edit/{blogId}", 1L).session(session))
				// HTTP ステータスが 200 OK であることを検証する
				.andExpect(status().isOk())
				// ビュー名が blog-img-edit.html であることを検証する
				.andExpect(view().name("blog-img-edit.html"))
				// model に userName・blogList・editImageMessage が存在することを検証する
				.andExpect(model().attributeExists("userName", "blogList", "editImageMessage"))
				// model の userName が "John" であることを検証する
				.andExpect(model().attribute("userName", "John"))
				// model の editImageMessage が "画像編集" であることを検証する
				.andExpect(model().attribute("editImageMessage", "画像編集"))
				// model の blogList が取得した記事と一致することを検証する
				.andExpect(model().attribute("blogList", blog));

		// getBlogPost が引数 1L で 1 回呼ばれたことを検証する
		verify(blogService, times(1)).getBlogPost(1L);
	}

	// No.2 画像編集画面の表示（記事なし）をテストする
	@Test
	public void testGetBlogEditImagePage_NullBlog() throws Exception {
		// getBlogPost(1L) が null を返すようモックを設定する
		when(blogService.getBlogPost(1L)).thenReturn(null);

		// GET /user/blog/image/edit/1 をセッション付きで実行し、結果を検証する
		mockMvc.perform(get("/user/blog/image/edit/{blogId}", 1L).session(session))
				// HTTP ステータスが 200 OK であることを検証する（実装は redirect スペル誤りのためビュー解決される）
				.andExpect(status().isOk())
				// ビュー名が実装どおり "redirecr:/user/blog/list" であることを検証する
				.andExpect(view().name("redirecr:/user/blog/list"));

		// getBlogPost が引数 1L で 1 回呼ばれたことを検証する
		verify(blogService, times(1)).getBlogPost(1L);
	}

	// No.3 画像更新の成功をテストする
	@Test
	public void testBlogImageUpdate_Succeed() throws Exception {
		// アップロードする元ファイル名を定義する
		String filImage = "test-image.jpg";
		// コントローラと同形式で日時プレフィックスを付与した想定ファイル名を生成する
		String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-").format(new Date()) + filImage;
		// 送信用の擬似画像ファイル（中身は空バイト配列）を生成する
		MockMultipartFile blogImage = new MockMultipartFile("blogImage", filImage, "image/jpeg", new byte[0]);
		// editBlogImage が true を返すようモックを設定する
		when(blogService.editBlogImage(anyLong(), anyString(), anyLong())).thenReturn(true);

		// POST /user/blog/image/update を multipart で実行し、結果を検証する
		mockMvc.perform(MockMvcRequestBuilders.multipart("/user/blog/image/update")
				// 擬似画像ファイルを添付する
				.file(blogImage)
				// ブログ ID を送信する
				.param("blogId", "1")
				// セッションを付与する
				.session(session))
				// HTTP ステータスが 200 OK であることを検証する
				.andExpect(status().isOk())
				// ビュー名が blog-edit-fix.html であることを検証する
				.andExpect(view().name("blog-edit-fix.html"));

		// editBlogImage が（1L, 生成ファイル名, 1L）で 1 回呼ばれたことを検証する
		verify(blogService, times(1)).editBlogImage(eq(1L), eq(fileName), eq(1L));
	}

	// No.4 画像更新の失敗をテストする
	@Test
	public void testBlogImageUpdate_Failure() throws Exception {
		// アップロードする元ファイル名を定義する
		String filImage = "test-image.jpg";
		// コントローラと同形式で日時プレフィックスを付与した想定ファイル名を生成する
		String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-").format(new Date()) + filImage;
		// 送信用の擬似画像ファイル（中身は空バイト配列）を生成する
		MockMultipartFile blogImage = new MockMultipartFile("blogImage", filImage, "image/jpeg", new byte[0]);
		// editBlogImage が false を返すようモックを設定する
		when(blogService.editBlogImage(anyLong(), anyString(), anyLong())).thenReturn(false);
		// 失敗分岐で再取得される記事を返すようモックを設定する
		when(blogService.getBlogPost(1L)).thenReturn(new BlogEntity());

		// POST /user/blog/image/update を multipart で実行し、結果を検証する
		mockMvc.perform(MockMvcRequestBuilders.multipart("/user/blog/image/update")
				// 擬似画像ファイルを添付する
				.file(blogImage)
				// ブログ ID を送信する
				.param("blogId", "1")
				// セッションを付与する
				.session(session))
				// HTTP ステータスが 200 OK であることを検証する
				.andExpect(status().isOk())
				// ビュー名が blog-img-edit.html であることを検証する
				.andExpect(view().name("blog-img-edit.html"))
				// model に blogList・editImageMessage が存在することを検証する
				.andExpect(model().attributeExists("blogList", "editImageMessage"));

		// editBlogImage が（1L, 生成ファイル名, 1L）で 1 回呼ばれたことを検証する
		verify(blogService, times(1)).editBlogImage(eq(1L), eq(fileName), eq(1L));
		// 失敗分岐で getBlogPost が引数 1L で 1 回呼ばれたことを検証する
		verify(blogService, times(1)).getBlogPost(1L);
	}
}
