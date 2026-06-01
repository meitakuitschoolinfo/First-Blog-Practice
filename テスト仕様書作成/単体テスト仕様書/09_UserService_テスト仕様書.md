# 単体テスト仕様書

| 項目 | 内容 | 項目 | 内容 |
| --- | --- | --- | --- |
| システム名 | Blog アプリケーション | 作成者 | （未記入） |
| サブシステム名 | ユーザー管理サービス（UserService） | 作成日 | （未記入） |
| 対象クラス | blog.ex.service.UserService | 実施者 | （未記入） |
| テスト方式 | JUnit 5 + Mockito（DAO をモック化） | - | - |

## テストケース一覧

| No | 分類 | テスト項目 | 検証内容 | 実施日 | 結果 | 備考・特記事項 |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | 正常系 | createAccount：メール未登録時の新規登録 | findByEmail が null（未登録）を返す場合、userDao.save が呼ばれ、戻り値が true であること。save に渡される UserEntity の userName/email/password が引数どおりであること |  | - | save が 1 回呼ばれることを verify |
| 2 | 異常系 | createAccount：メール重複時の登録拒否 | findByEmail が既存 UserEntity を返す場合、戻り値が false であり、userDao.save が一度も呼ばれないこと |  | - | save が呼ばれないことを verify(never) |
| 3 | 正常系 | loginAccount：認証成功 | findByEmailAndPassword が UserEntity を返す場合、その UserEntity がそのまま返却されること |  | - | 返却 Entity が DAO の戻り値と同一であること |
| 4 | 異常系 | loginAccount：認証失敗 | findByEmailAndPassword が null を返す場合、戻り値が null であること |  | - | メール／パスワード不一致を想定 |

## テストデータ

| No | 項目 | 値 | 説明 |
| --- | --- | --- | --- |
| 1 | userName | "John" | 新規登録ユーザー名 |
| 1 | email | "john@test.com" | 新規登録メールアドレス（DB 未登録） |
| 1 | password | "password" | 新規登録パスワード |
| 2 | email | "exist@test.com" | DB に既に存在するメールアドレス |
| 2 | （既存）UserEntity | new UserEntity("Exist","exist@test.com","pass", now) | findByEmail が返す既存ユーザー |
| 3 | email | "john@test.com" | ログイン用メールアドレス |
| 3 | password | "password" | ログイン用パスワード（一致） |
| 3 | 認証 UserEntity | new UserEntity("John","john@test.com","password", now) | findByEmailAndPassword が返す Entity |
| 4 | email | "ng@test.com" | 認証失敗用メールアドレス |
| 4 | password | "wrong" | 認証失敗用パスワード（不一致） |
