package blog.ex.controller;

// Mockito の引数マッチャ（任意の Long）を静的インポートします。
import static org.mockito.ArgumentMatchers.anyLong;
// Mockito の引数マッチャ（任意の String）を静的インポートします。
import static org.mockito.ArgumentMatchers.anyString;
// Mockito の厳密一致マッチャ（eq）を静的インポートします。
import static org.mockito.ArgumentMatchers.eq;
// Mockito の型一致マッチャ（any）を静的インポートします。
import static org.mockito.ArgumentMatchers.any;
// Mockito の verify 呼び出し回数指定（times）を静的インポートします。
import static org.mockito.Mockito.times;
// Mockito のメソッド呼び出し検証（verify）を静的インポートします。
import static org.mockito.Mockito.verify;
// Mockito のスタブ設定（when）を静的インポートします。
import static org.mockito.Mockito.when;
// MockMvc で GET リクエストを生成する get メソッドを静的インポートします。
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// MockMvc で multipart リクエストを生成する multipart メソッドを静的インポートします。
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
// レスポンスのモデルを検証する model マッチャを静的インポートします。
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
// レスポンスの HTTP ステータスを検証する status マッチャを静的インポートします。
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
// レスポンスのビュー名を検証する view マッチャを静的インポートします。
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

// 画像保存ファイル名のプレフィックス生成に使う SimpleDateFormat をインポートします。
import java.text.SimpleDateFormat;
// ブログ登録日に使用する LocalDate をインポートします。
import java.time.LocalDate;
// ユーザー登録日時に使用する LocalDateTime をインポートします。
import java.time.LocalDateTime;
// ファイル名生成の現在日時取得に使う Date をインポートします。
import java.util.Date;

// 各テスト実行前の共通処理に使用する @BeforeEach をインポートします。
import org.junit.jupiter.api.BeforeEach;
// テストメソッドであることを示す @Test をインポートします。
import org.junit.jupiter.api.Test;
// DI（依存性注入）を行う @Autowired をインポートします。
import org.springframework.beans.factory.annotation.Autowired;
// MockMvc を自動構成する @AutoConfigureMockMvc をインポートします。
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// Spring Boot のテストコンテキストを起動する @SpringBootTest をインポートします。
import org.springframework.boot.test.context.SpringBootTest;
// Bean をモックに差し替える @MockBean をインポートします。
import org.springframework.boot.test.mock.mockito.MockBean;
// テスト用のモックセッション MockHttpSession をインポートします。
import org.springframework.mock.web.MockHttpSession;
// multipart リクエストへ添付する MockMultipartFile をインポートします。
import org.springframework.mock.web.MockMultipartFile;
// HTTP リクエストを模擬実行する MockMvc をインポートします。
import org.springframework.test.web.servlet.MockMvc;

// セッションへ格納するユーザーエンティティをインポートします。
import blog.ex.model.entity.UserEntity;
// モック化対象のサービスクラスをインポートします。
import blog.ex.service.BlogService;

/**
 * BlogRegisterController（ブログ新規登録機能）の単体テストクラスです。
 * 登録画面表示・登録成功・登録失敗の各ケースを MockMvc と Mockito で検証します。
 */
// Spring Boot のテストコンテキストを起動するアノテーションです。
@SpringBootTest
// MockMvc を自動構成するアノテーションです。
@AutoConfigureMockMvc
public class BlogRegisterControllerTest {

	// HTTP リクエストを模擬実行するための MockMvc を注入します。
	@Autowired
	private MockMvc mockMvc;

	// BlogService をモックに差し替えてコントローラーへ注入します。
	@MockBean
	private BlogService blogService;

	// ログインユーザー情報を保持するテスト用セッションです。
	private MockHttpSession session;

	/**
	 * 各テスト実行前に共通のテストデータを準備します。
	 * ログインユーザーを生成し、セッションへ格納します。
	 */
	@BeforeEach
	public void prepareData() {
		// ログインユーザーを表す UserEntity を生成します。
		UserEntity user = new UserEntity();
		// ユーザー ID に 1L を設定します。
		user.setUserId(1L);
		// ユーザー名に "John" を設定します。
		user.setUserName("John");
		// メールアドレスを設定します（NonNull 項目のため）。
		user.setEmail("john@example.com");
		// パスワードを設定します（NonNull 項目のため）。
		user.setPassword("password");
		// 登録日時を現在時刻で設定します（NonNull 項目のため）。
		user.setRegisterDate(LocalDateTime.now());

		// テスト用のモックセッションを生成します。
		session = new MockHttpSession();
		// セッションの "user" 属性へログインユーザーを格納します。
		session.setAttribute("user", user);
	}

	/**
	 * No.1 表示テスト:
	 * GET /user/blog/register が 200 を返し、ビュー名とモデル属性が正しいことを検証します。
	 */
	@Test
	public void testGetBlogRegisterPage() throws Exception {
		// セッションを付与して /user/blog/register へ GET リクエストを実行します。
		mockMvc.perform(get("/user/blog/register").session(session))
				// HTTP ステータスが 200(OK) であることを検証します。
				.andExpect(status().isOk())
				// ビュー名が "blog-register.html" であることを検証します。
				.andExpect(view().name("blog-register.html"))
				// モデルに "userName" と "registerMessage" 属性が存在することを検証します。
				.andExpect(model().attributeExists("userName", "registerMessage"))
				// "userName" 属性が "John" であることを検証します。
				.andExpect(model().attribute("userName", "John"))
				// "registerMessage" 属性が "新規記事追加" であることを検証します。
				.andExpect(model().attribute("registerMessage", "新規記事追加"));
	}

	/**
	 * No.2 正常系:
	 * createBlogPost が true を返す場合に登録が成功し、完了画面へ遷移することを検証します。
	 */
	@Test
	public void testBlogRegister_Succeed() throws Exception {
		// createBlogPost が任意の引数で呼ばれたら true を返すようスタブを設定します。
		when(blogService.createBlogPost(anyString(), any(LocalDate.class), anyString(), anyString(), anyString(), anyLong()))
				.thenReturn(true);

		// アップロードする画像の元ファイル名を定義します。
		String orgImage = "test-image.jpg";
		// コントローラーが付与するのと同じ日時プレフィックス付き保存ファイル名を生成します。
		String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-").format(new Date()) + orgImage;
		// multipart リクエストへ添付する空データの画像ファイルを生成します。
		MockMultipartFile blogImage = new MockMultipartFile("blogImage", orgImage, "image/jpeg", new byte[0]);

		// /user/blog/register/process へ multipart の POST リクエストを実行します。
		mockMvc.perform(multipart("/user/blog/register/process")
				// 画像ファイルをリクエストへ添付します。
				.file(blogImage)
				// ブログタイトルのパラメータを設定します。
				.param("blogTitle", "Test Blog")
				// 登録日のパラメータを設定します。
				.param("registerDate", "2023-06-01")
				// カテゴリーのパラメータを設定します。
				.param("category", "Test Category")
				// ブログ詳細のパラメータを設定します。
				.param("blogDetail", "Test Blog Detail")
				// セッションをリクエストへ付与します。
				.session(session))
				// HTTP ステータスが 200(OK) であることを検証します。
				.andExpect(status().isOk())
				// ビュー名が "blog-register-fix.html" であることを検証します。
				.andExpect(view().name("blog-register-fix.html"));

		// createBlogPost が指定した引数で 1 回だけ呼ばれたことを検証します。
		verify(blogService, times(1)).createBlogPost(eq("Test Blog"), eq(LocalDate.parse("2023-06-01")), eq(fileName), eq("Test Blog Detail"), eq("Test Category"), eq(1L));
	}

	/**
	 * No.3 異常系:
	 * createBlogPost が false を返す場合に登録が失敗し、登録画面へ戻ることを検証します。
	 */
	@Test
	public void testBlogRegister_Failure() throws Exception {
		// createBlogPost が任意の引数で呼ばれたら false を返すようスタブを設定します。
		when(blogService.createBlogPost(anyString(), any(LocalDate.class), anyString(), anyString(), anyString(), anyLong()))
				.thenReturn(false);

		// アップロードする画像の元ファイル名を定義します。
		String orgImage = "test-image.jpg";
		// コントローラーが付与するのと同じ日時プレフィックス付き保存ファイル名を生成します。
		String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-").format(new Date()) + orgImage;
		// multipart リクエストへ添付する空データの画像ファイルを生成します。
		MockMultipartFile blogImage = new MockMultipartFile("blogImage", orgImage, "image/jpeg", new byte[0]);

		// /user/blog/register/process へ multipart の POST リクエストを実行します。
		mockMvc.perform(multipart("/user/blog/register/process")
				// 画像ファイルをリクエストへ添付します。
				.file(blogImage)
				// ブログタイトルのパラメータを設定します。
				.param("blogTitle", "Test Blog")
				// 登録日のパラメータを設定します。
				.param("registerDate", "2023-06-01")
				// カテゴリーのパラメータを設定します。
				.param("category", "Test Category")
				// ブログ詳細のパラメータを設定します。
				.param("blogDetail", "Test Blog Detail")
				// セッションをリクエストへ付与します。
				.session(session))
				// HTTP ステータスが 200(OK) であることを検証します。
				.andExpect(status().isOk())
				// ビュー名が "blog-register.html" であることを検証します。
				.andExpect(view().name("blog-register.html"))
				// モデルに "registerMessage" 属性が存在することを検証します。
				.andExpect(model().attributeExists("registerMessage"));

		// createBlogPost が指定した引数で 1 回だけ呼ばれたことを検証します。
		verify(blogService, times(1)).createBlogPost(eq("Test Blog"), eq(LocalDate.parse("2023-06-01")), eq(fileName), eq("Test Blog Detail"), eq("Test Category"), eq(1L));
	}
}
