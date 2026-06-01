# 単体テスト仕様書

| 項目 | 内容 | 項目 | 内容 |
| --- | --- | --- | --- |
| システム名 | Blog アプリケーション | 作成者 | （未記入） |
| サブシステム名 | ログアウト機能 | 作成日 | （未記入） |
| 対象クラス | blog.ex.controller.LogoutController | 実施者 | （未記入） |
| テスト方式 | Spring Boot Test + MockMvc + Mockito（Service をモック化） | - | - |

## テストケース一覧

| No | 分類 | テスト項目 | 検証内容 | 実施日 | 結果 | 備考・特記事項 |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | 正常系 | "/user/blog/logout" へのGETリクエストを実行 | status が 3xx リダイレクトであり、リダイレクト先URLが "/user/login" であることを検証 |  | - | session.invalidate() 実行後にログイン画面へリダイレクト |
| 2 | 正常系 | ログイン済みセッションで "/user/blog/logout" を実行 | リダイレクト先が "/user/login" であり、セッションが無効化され user 属性が取得できないことを検証 |  | - | セッション破棄の確認 |

## テストデータ

| No | 項目 | 値 | 説明 |
| --- | --- | --- | --- |
| 1 | リクエストURL | /user/blog/logout | ログアウト処理のエンドポイント |
| 2 | セッション属性名 | user | ログイン中ユーザーを保持する属性名 |
| 3 | userEntity | UserEntity(1L, "Akemi", "ake@test.com", "1234abcd", now) | ログイン済み状態を再現するためのセッション格納値 |
