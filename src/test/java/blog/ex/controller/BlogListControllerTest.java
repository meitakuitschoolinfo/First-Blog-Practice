package blog.ex.controller;

// JUnit のアサーション（assertEquals など）を静的インポートします。
import static org.junit.jupiter.api.Assertions.assertEquals;
// Mockito の引数マッチャ（任意の Long を表す anyLong）を静的インポートします。
import static org.mockito.ArgumentMatchers.anyLong;
// Mockito の verify メソッド呼び出し回数指定（times）を静的インポートします。
import static org.mockito.Mockito.times;
// Mockito のメソッド呼び出し検証（verify）を静的インポートします。
import static org.mockito.Mockito.verify;
// Mockito のスタブ設定（when）を静的インポートします。
import static org.mockito.Mockito.when;
// MockMvc で GET リクエストを生成する get メソッドを静的インポートします。
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// レスポンスのモデルを検証する model マッチャを静的インポートします。
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
// レスポンスの HTTP ステータスを検証する status マッチャを静的インポートします。
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
// レスポンスのビュー名を検証する view マッチャを静的インポートします。
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

// ユーザー登録日時に使用する LocalDateTime クラスをインポートします。
import java.time.LocalDateTime;
// ブログ一覧を格納する可変リスト ArrayList をインポートします。
import java.util.ArrayList;
// 一覧のコレクション型 List をインポートします。
import java.util.List;

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
// HTTP リクエストを模擬実行する MockMvc をインポートします。
import org.springframework.test.web.servlet.MockMvc;
// MockMvc 実行結果を取得する MvcResult をインポートします。
import org.springframework.test.web.servlet.MvcResult;

// 検証対象となるブログエンティティをインポートします。
import blog.ex.model.entity.BlogEntity;
// セッションへ格納するユーザーエンティティをインポートします。
import blog.ex.model.entity.UserEntity;
// モック化対象のサービスクラスをインポートします。
import blog.ex.service.BlogService;

/**
 * BlogListController（ブログ一覧表示機能）の単体テストクラスです。
 * MockMvc と Mockito を用いて、コントローラーの挙動を検証します。
 */
// Spring Boot のテストコンテキストを起動するアノテーションです。
@SpringBootTest
// MockMvc を自動構成するアノテーションです。
@AutoConfigureMockMvc
public class BlogListControllerTest {

	// HTTP リクエストを模擬実行するための MockMvc を注入します。
	@Autowired
	private MockMvc mockMvc;

	// BlogService をモックに差し替えてコントローラーへ注入します。
	@MockBean
	private BlogService blogService;

	// ログインユーザー情報を保持するテスト用セッションです。
	private MockHttpSession session;

	// 一覧取得時に返すモック用のブログリストを保持します。
	private List<BlogEntity> blogList;

	/**
	 * 各テスト実行前に共通のテストデータを準備します。
	 * ログインユーザーをセッションへ格納し、findAllBlogPost のスタブを設定します。
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

		// findAllBlogPost が返すブログリストを生成します。
		blogList = new ArrayList<>();
		// 1 件目のダミーブログを追加します。
		blogList.add(new BlogEntity());
		// 2 件目のダミーブログを追加します。
		blogList.add(new BlogEntity());

		// テスト用のモックセッションを生成します。
		session = new MockHttpSession();
		// セッションの "user" 属性へログインユーザーを格納します。
		session.setAttribute("user", user);

		// blogService.findAllBlogPost(1L) が呼ばれたら blogList を返すよう設定します。
		when(blogService.findAllBlogPost(1L)).thenReturn(blogList);
	}

	/**
	 * No.1 表示テスト:
	 * GET /user/blog/list が 200 を返し、ビュー名と userName 属性が正しいことを検証します。
	 */
	@Test
	public void testGetBlogListPage() throws Exception {
		// セッションを付与して /user/blog/list へ GET リクエストを実行します。
		mockMvc.perform(get("/user/blog/list").session(session))
				// HTTP ステータスが 200(OK) であることを検証します。
				.andExpect(status().isOk())
				// ビュー名が "blog-list.html" であることを検証します。
				.andExpect(view().name("blog-list.html"))
				// モデルに "userName" と "blogList" 属性が存在することを検証します。
				.andExpect(model().attributeExists("userName", "blogList"))
				// "userName" 属性が "John" であることを検証します。
				.andExpect(model().attribute("userName", "John"));
	}

	/**
	 * No.2 正常系:
	 * モデルの "blogList" 属性が findAllBlogPost のモック戻り値と一致することを検証します。
	 */
	@Test
	public void testGetBlogListPage_BlogListContent() throws Exception {
		// リクエストを実行し、ステータス 200 を確認したうえで結果を取得します。
		MvcResult result = mockMvc.perform(get("/user/blog/list").session(session))
				// HTTP ステータスが 200(OK) であることを検証します。
				.andExpect(status().isOk())
				// 実行結果を MvcResult として取り出します。
				.andReturn();

		// モデルから "blogList" 属性を取り出します。
		Object actualBlogList = result.getModelAndView().getModel().get("blogList");
		// 取り出した一覧がモックで設定したリストと一致することを検証します。
		assertEquals(blogList, actualBlogList);
	}

	/**
	 * No.3 正常系:
	 * blogService.findAllBlogPost が userId=1L で 1 回だけ呼ばれることを検証します。
	 */
	@Test
	public void testGetBlogListPage_ServiceCalled() throws Exception {
		// セッションを付与して一覧ページへ GET リクエストを実行します。
		mockMvc.perform(get("/user/blog/list").session(session))
				// HTTP ステータスが 200(OK) であることを検証します。
				.andExpect(status().isOk());

		// findAllBlogPost が引数 1L で 1 回だけ呼び出されたことを検証します。
		verify(blogService, times(1)).findAllBlogPost(anyLong());
	}
}
