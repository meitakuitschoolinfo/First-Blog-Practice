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

import java.time.LocalDateTime; // 登録日時を表す LocalDateTime を読み込む

import org.junit.jupiter.api.Test;                 // テストメソッドを示す @Test を読み込む
import org.junit.jupiter.api.extension.ExtendWith; // Mockito 拡張を有効化する @ExtendWith を読み込む
import org.mockito.InjectMocks;                    // モックを注入する @InjectMocks を読み込む
import org.mockito.Mock;                           // モックを生成する @Mock を読み込む
import org.mockito.junit.jupiter.MockitoExtension; // JUnit5 と Mockito を連携させる拡張を読み込む

import blog.ex.model.dao.UserDao;        // モック化対象の DAO を読み込む
import blog.ex.model.entity.UserEntity;  // テストで扱うエンティティを読み込む

@ExtendWith(MockitoExtension.class) // JUnit5 上で Mockito アノテーションを有効化する
public class UserServiceTest {

	@Mock // userDao をモック化して DB アクセスを置き換える
	private UserDao userDao; // テスト対象が依存する DAO のモック

	@InjectMocks // 上記モックを UserService に自動注入する
	private UserService userService; // テスト対象クラス

	// No.1 正常系：メール未登録時に新規登録され true が返る
	@Test
	public void testCreateAccount_NewUser_Success() {
		when(userDao.findByEmail(eq("john@test.com"))).thenReturn(null); // メール検索で未登録（null）を返すよう設定する
		boolean result = userService.createAccount("John", "john@test.com", "password"); // アカウント作成メソッドを実行する
		assertTrue(result); // 戻り値が true であることを検証する
		verify(userDao, times(1)).save(any(UserEntity.class)); // save が 1 回呼ばれたことを検証する
	}

	// No.2 異常系：メール重複時は登録されず false が返る
	@Test
	public void testCreateAccount_DuplicateEmail_Fail() {
		UserEntity existing = new UserEntity("Exist", "exist@test.com", "pass", LocalDateTime.now()); // 既存ユーザーを表す Entity を用意する
		when(userDao.findByEmail(eq("exist@test.com"))).thenReturn(existing); // メール検索で既存ユーザーを返すよう設定する
		boolean result = userService.createAccount("Exist", "exist@test.com", "pass"); // アカウント作成メソッドを実行する
		assertFalse(result); // 戻り値が false であることを検証する
		verify(userDao, never()).save(any(UserEntity.class)); // save が一度も呼ばれないことを検証する
	}

	// No.3 正常系：認証成功時に該当 UserEntity が返る
	@Test
	public void testLoginAccount_Success() {
		UserEntity user = new UserEntity("John", "john@test.com", "password", LocalDateTime.now()); // 認証成功で返る Entity を用意する
		when(userDao.findByEmailAndPassword(eq("john@test.com"), eq("password"))).thenReturn(user); // メールとパスワード一致時に Entity を返すよう設定する
		UserEntity result = userService.loginAccount("john@test.com", "password"); // ログインメソッドを実行する
		assertEquals(user, result); // 返却された Entity が DAO の戻り値と同一であることを検証する
	}

	// No.4 異常系：認証失敗時に null が返る
	@Test
	public void testLoginAccount_Fail() {
		when(userDao.findByEmailAndPassword(eq("ng@test.com"), eq("wrong"))).thenReturn(null); // 不一致時に null を返すよう設定する
		UserEntity result = userService.loginAccount("ng@test.com", "wrong"); // ログインメソッドを実行する
		assertNull(result); // 戻り値が null であることを検証する
	}
}
