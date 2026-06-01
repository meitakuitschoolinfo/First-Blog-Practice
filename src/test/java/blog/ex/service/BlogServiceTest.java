package blog.ex.service; // テスト対象と同じ blog.ex.service パッケージに配置する

import static org.junit.jupiter.api.Assertions.assertEquals; // 値の一致を検証する assertEquals を読み込む
import static org.junit.jupiter.api.Assertions.assertFalse;  // false を検証する assertFalse を読み込む
import static org.junit.jupiter.api.Assertions.assertNull;   // null を検証する assertNull を読み込む
import static org.junit.jupiter.api.Assertions.assertTrue;   // true を検証する assertTrue を読み込む
import static org.mockito.ArgumentMatchers.any;   // 任意の引数を表す any マッチャを読み込む
import static org.mockito.ArgumentMatchers.eq;    // 特定値に一致させる eq マッチャを読み込む
import static org.mockito.Mockito.never;          // 呼び出されないことを検証する never を読み込む
import static org.mockito.Mockito.times;          // 呼び出し回数を指定する times を読み込む
import static org.mockito.Mockito.verify;         // モックの呼び出しを検証する verify を読み込む
import static org.mockito.Mockito.when;           // モックの戻り値を定義する when を読み込む

import java.time.LocalDate; // ブログの登録日を表す LocalDate を読み込む
import java.util.Arrays;    // リスト生成に使う Arrays を読み込む
import java.util.List;      // 一覧取得の戻り値型 List を読み込む

import org.junit.jupiter.api.Test;                 // テストメソッドを示す @Test を読み込む
import org.junit.jupiter.api.extension.ExtendWith; // Mockito 拡張を有効化する @ExtendWith を読み込む
import org.mockito.InjectMocks;                    // モックを注入する @InjectMocks を読み込む
import org.mockito.Mock;                           // モックを生成する @Mock を読み込む
import org.mockito.junit.jupiter.MockitoExtension; // JUnit5 と Mockito を連携させる拡張を読み込む

import blog.ex.model.dao.BlogDao;        // モック化対象の DAO を読み込む
import blog.ex.model.entity.BlogEntity;  // テストで扱うエンティティを読み込む

@ExtendWith(MockitoExtension.class) // JUnit5 上で Mockito アノテーションを有効化する
public class BlogServiceTest {

	@Mock // blogDao をモック化して DB アクセスを置き換える
	private BlogDao blogDao; // テスト対象が依存する DAO のモック

	@InjectMocks // 上記モックを BlogService に自動注入する
	private BlogService blogService; // テスト対象クラス

	// No.1 異常系：findAllBlogPost に userId=null を渡すと null が返る
	@Test
	public void testFindAllBlogPost_UserIdNull_ReturnsNull() {
		List<BlogEntity> result = blogService.findAllBlogPost(null); // userId に null を渡して一覧取得を実行する
		assertNull(result); // 戻り値が null であることを検証する
		verify(blogDao, never()).findByUserId(any()); // findByUserId が呼ばれないことを検証する
	}

	// No.2 正常系：userId 指定時に DAO のリストがそのまま返る
	@Test
	public void testFindAllBlogPost_UserIdSpecified_ReturnsList() {
		BlogEntity b1 = new BlogEntity("t1", LocalDate.of(2026, 6, 1), "i1.png", "d1", "日記", 1L); // 一覧の 1 件目を用意する
		BlogEntity b2 = new BlogEntity("t2", LocalDate.of(2026, 6, 1), "i2.png", "d2", "日記", 1L); // 一覧の 2 件目を用意する
		List<BlogEntity> list = Arrays.asList(b1, b2); // DAO が返す 2 件のリストを作成する
		when(blogDao.findByUserId(eq(1L))).thenReturn(list); // userId=1 で上記リストを返すよう設定する
		List<BlogEntity> result = blogService.findAllBlogPost(1L); // 一覧取得を実行する
		assertEquals(list, result); // 返却リストが DAO の戻り値と一致することを検証する
		assertEquals(2, result.size()); // 件数が 2 件であることを検証する
	}

	// No.3 正常系：重複なしのブログが新規保存され true が返る
	@Test
	public void testCreateBlogPost_NoDuplicate_Success() {
		LocalDate date = LocalDate.of(2026, 6, 1); // 登録日を用意する
		when(blogDao.findByBlogTitleAndRegisterDate(eq("新規タイトル"), eq(date))).thenReturn(null); // 重複なし（null）を返すよう設定する
		boolean result = blogService.createBlogPost("新規タイトル", date, "image.png", "本文", "日記", 1L); // ブログ作成を実行する
		assertTrue(result); // 戻り値が true であることを検証する
		verify(blogDao, times(1)).save(any(BlogEntity.class)); // save が 1 回呼ばれたことを検証する
	}

	// No.4 異常系：タイトル＋登録日が重複する場合は保存されず false が返る
	@Test
	public void testCreateBlogPost_Duplicate_Fail() {
		LocalDate date = LocalDate.of(2026, 6, 1); // 登録日を用意する
		BlogEntity existing = new BlogEntity("既存タイトル", date, "img.png", "本文", "日記", 1L); // 既存のブログ Entity を用意する
		when(blogDao.findByBlogTitleAndRegisterDate(eq("既存タイトル"), eq(date))).thenReturn(existing); // 重複ありで既存 Entity を返すよう設定する
		boolean result = blogService.createBlogPost("既存タイトル", date, "img.png", "本文", "日記", 1L); // ブログ作成を実行する
		assertFalse(result); // 戻り値が false であることを検証する
		verify(blogDao, never()).save(any(BlogEntity.class)); // save が一度も呼ばれないことを検証する
	}

	// No.5 異常系：getBlogPost に blogId=null を渡すと null が返る
	@Test
	public void testGetBlogPost_BlogIdNull_ReturnsNull() {
		BlogEntity result = blogService.getBlogPost(null); // blogId に null を渡して取得を実行する
		assertNull(result); // 戻り値が null であることを検証する
		verify(blogDao, never()).findByBlogId(any()); // findByBlogId が呼ばれないことを検証する
	}

	// No.6 正常系：blogId 指定時に DAO の Entity がそのまま返る
	@Test
	public void testGetBlogPost_BlogIdSpecified_ReturnsEntity() {
		BlogEntity entity = new BlogEntity("タイトル", LocalDate.of(2026, 6, 1), "img.png", "本文", "日記", 1L); // 取得される Entity を用意する
		when(blogDao.findByBlogId(eq(10L))).thenReturn(entity); // blogId=10 で上記 Entity を返すよう設定する
		BlogEntity result = blogService.getBlogPost(10L); // 取得を実行する
		assertEquals(entity, result); // 返却 Entity が DAO の戻り値と同一であることを検証する
	}

	// No.7 異常系：editBlogPost に userId=null を渡すと false が返り保存されない
	@Test
	public void testEditBlogPost_UserIdNull_Fail() {
		LocalDate date = LocalDate.of(2026, 6, 1); // 登録日を用意する
		BlogEntity entity = new BlogEntity("タイトル", date, "img.png", "本文", "日記", 1L); // findByBlogId が返す Entity を用意する
		when(blogDao.findByBlogId(eq(10L))).thenReturn(entity); // blogId=10 で Entity を返すよう設定する（null ガード前に取得されるため）
		boolean result = blogService.editBlogPost("更新後タイトル", date, "更新本文", "趣味", null, 10L); // userId=null で編集を実行する
		assertFalse(result); // 戻り値が false であることを検証する
		verify(blogDao, never()).save(any(BlogEntity.class)); // save が呼ばれないことを検証する
	}

	// No.8 正常系：userId 指定時に値が更新され save されて true が返る
	@Test
	public void testEditBlogPost_UserIdSpecified_Success() {
		LocalDate date = LocalDate.of(2026, 6, 1); // 登録日を用意する
		BlogEntity entity = new BlogEntity("旧タイトル", date, "img.png", "旧本文", "日記", 1L); // 更新対象の Entity を用意する
		when(blogDao.findByBlogId(eq(10L))).thenReturn(entity); // blogId=10 で Entity を返すよう設定する
		boolean result = blogService.editBlogPost("更新後タイトル", date, "更新本文", "趣味", 1L, 10L); // 編集を実行する
		assertTrue(result); // 戻り値が true であることを検証する
		assertEquals("更新後タイトル", entity.getBlogTitle()); // タイトルが更新値に設定されたことを検証する
		verify(blogDao, times(1)).save(entity); // 更新後 Entity で save が 1 回呼ばれたことを検証する
	}

	// No.9 異常系：editBlogImage に fileName=null を渡すと false が返り保存されない
	@Test
	public void testEditBlogImage_FileNameNull_Fail() {
		BlogEntity entity = new BlogEntity("タイトル", LocalDate.of(2026, 6, 1), "old.png", "本文", "日記", 1L); // findByBlogId が返す Entity を用意する
		when(blogDao.findByBlogId(eq(10L))).thenReturn(entity); // blogId=10 で Entity を返すよう設定する
		boolean result = blogService.editBlogImage(10L, null, 1L); // fileName=null で画像編集を実行する
		assertFalse(result); // 戻り値が false であることを検証する
		verify(blogDao, never()).save(any(BlogEntity.class)); // save が呼ばれないことを検証する
	}

	// No.10 異常系：既存画像と同一ファイル名なら false が返り保存されない
	@Test
	public void testEditBlogImage_SameFileName_Fail() {
		BlogEntity entity = new BlogEntity("タイトル", LocalDate.of(2026, 6, 1), "same.png", "本文", "日記", 1L); // 既存画像 same.png を持つ Entity を用意する
		when(blogDao.findByBlogId(eq(10L))).thenReturn(entity); // blogId=10 で Entity を返すよう設定する
		boolean result = blogService.editBlogImage(10L, "same.png", 1L); // 既存と同一の画像名で編集を実行する
		assertFalse(result); // 戻り値が false であることを検証する
		verify(blogDao, never()).save(any(BlogEntity.class)); // save が呼ばれないことを検証する
	}

	// No.11 正常系：新しいファイル名なら画像が更新され save されて true が返る
	@Test
	public void testEditBlogImage_NewFileName_Success() {
		BlogEntity entity = new BlogEntity("タイトル", LocalDate.of(2026, 6, 1), "old.png", "本文", "日記", 1L); // 既存画像 old.png を持つ Entity を用意する
		when(blogDao.findByBlogId(eq(10L))).thenReturn(entity); // blogId=10 で Entity を返すよう設定する
		boolean result = blogService.editBlogImage(10L, "new.png", 1L); // 既存と異なる新しい画像名で編集を実行する
		assertTrue(result); // 戻り値が true であることを検証する
		assertEquals("new.png", entity.getBlogImage()); // 画像名が new.png に更新されたことを検証する
		verify(blogDao, times(1)).save(entity); // 更新後 Entity で save が 1 回呼ばれたことを検証する
	}

	// No.12 異常系：deleteBlog に blogId=null を渡すと false が返り削除されない
	@Test
	public void testDeleteBlog_BlogIdNull_Fail() {
		boolean result = blogService.deleteBlog(null); // blogId=null で削除を実行する
		assertFalse(result); // 戻り値が false であることを検証する
		verify(blogDao, never()).deleteByBlogId(any()); // deleteByBlogId が呼ばれないことを検証する
	}

	// No.13 正常系：blogId 指定時に削除が実行され true が返る
	@Test
	public void testDeleteBlog_BlogIdSpecified_Success() {
		boolean result = blogService.deleteBlog(10L); // blogId=10 で削除を実行する
		assertTrue(result); // 戻り値が true であることを検証する
		verify(blogDao, times(1)).deleteByBlogId(eq(10L)); // deleteByBlogId が blogId=10 で 1 回呼ばれたことを検証する
	}
}
