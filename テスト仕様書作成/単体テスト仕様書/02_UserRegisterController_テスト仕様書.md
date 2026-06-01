# 単体テスト仕様書

| 項目 | 内容 | 項目 | 内容 |
| --- | --- | --- | --- |
| システム名 | Blog アプリケーション | 作成者 | （未記入） |
| サブシステム名 | ユーザー登録機能 | 作成日 | （未記入） |
| 対象クラス | blog.ex.controller.UserRegisterController | 実施者 | （未記入） |
| テスト方式 | Spring Boot Test + MockMvc + Mockito（Service をモック化） | - | - |

## テストケース一覧

| No | 分類 | テスト項目 | 検証内容 | 実施日 | 結果 | 備考・特記事項 |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | 表示テスト | "/user/register" へのGETリクエストを実行 | ビュー名が "register.html" であることを検証 |  | - | 登録画面表示 |
| 2 | 正常系 | 新規ユーザー(userName="John", email="john@test.com", password="password") で "/user/register/process" へPOST | リダイレクト先URLが "/user/login" であることを検証。createAccount が指定引数で1回呼ばれること |  | - | createAccount が true を返す（新規登録成功） |
| 3 | 異常系 | 既存ユーザー(userName="John", email="john@test.com", password="existingPassword") で "/user/register/process" へPOST | リダイレクト先URLが "/user/login" であることを検証。createAccount が指定引数で1回呼ばれること |  | - | createAccount が false を返す（重複ユーザー）。Controller は戻り値に関わらず redirect:/user/login |

## テストデータ

| No | 項目 | 値 | 説明 |
| --- | --- | --- | --- |
| 1 | userName | John | 登録するユーザー名 |
| 2 | email | john@test.com | 登録するメールアドレス |
| 3 | password（新規） | password | 新規登録成功用のパスワード |
| 4 | password（既存） | existingPassword | 既存ユーザー（重複）用のパスワード |

### モック設定（前提条件）

| No | モック対象メソッド | 引数 | 戻り値 |
| --- | --- | --- | --- |
| 1 | userService.createAccount | ("John", "john@test.com", "password") | true（新規登録成功） |
| 2 | userService.createAccount | ("John", "john@test.com", "existingPassword") | false（重複ユーザー） |
