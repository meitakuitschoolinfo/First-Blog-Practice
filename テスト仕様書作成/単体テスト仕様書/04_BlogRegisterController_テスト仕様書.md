# 単体テスト仕様書

| 項目 | 内容 | 項目 | 内容 |
| --- | --- | --- | --- |
| システム名 | Blog アプリケーション | 作成者 | （未記入） |
| サブシステム名 | ブログ新規登録機能 | 作成日 | （未記入） |
| 対象クラス | blog.ex.controller.BlogRegisterController | 実施者 | （未記入） |
| テスト方式 | Spring Boot Test + MockMvc + Mockito（BlogService をモック化） | - | - |

## テストケース一覧

| No | 分類 | テスト項目 | 検証内容 | 実施日 | 結果 | 備考・特記事項 |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | 表示テスト | 新規登録画面の表示（GET /user/blog/register） | HTTP ステータスが 200(OK) であること。ビュー名が "blog-register.html" であること。モデルに "userName" と "registerMessage" 属性が存在すること。"userName" が "John"、"registerMessage" が "新規記事追加" であること。 | | - | セッションに userId=1L / userName="John" を設定 |
| 2 | 正常系 | 記事登録の成功（POST /user/blog/register/process） | createBlogPost が true を返す場合、HTTP ステータスが 200(OK) でビュー名が "blog-register-fix.html" であること。createBlogPost が指定引数で 1 回呼び出されること。 | | - | multipart リクエスト。MockMultipartFile を使用 |
| 3 | 異常系 | 記事登録の失敗（POST /user/blog/register/process） | createBlogPost が false を返す場合、HTTP ステータスが 200(OK) でビュー名が "blog-register.html" であること。モデルに "registerMessage" 属性が存在すること。createBlogPost が 1 回呼び出されること。 | | - | 既に登録済みのケース。registerMessage="既に登録済みです" |

## テストデータ

| No | 項目 | 値 | 説明 |
| --- | --- | --- | --- |
| 1 | userId | 1L | セッションに格納するログインユーザーの ID |
| 2 | userName | "John" | セッションに格納するログインユーザーの名前 |
| 3 | email | "john@example.com" | UserEntity 生成用のメールアドレス |
| 4 | password | "password" | UserEntity 生成用のパスワード |
| 5 | registerDate(user) | LocalDateTime.now() | UserEntity の登録日時 |
| 6 | blogTitle | "Test Blog" | 登録するブログのタイトル |
| 7 | registerDate(blog) | "2023-06-01" | 登録するブログの登録日（LocalDate） |
| 8 | category | "Test Category" | 登録するブログのカテゴリー |
| 9 | blogDetail | "Test Blog Detail" | 登録するブログの詳細文 |
| 10 | blogImage(元ファイル名) | "test-image.jpg" | アップロードする画像の元ファイル名 |
| 11 | blogImage(保存ファイル名) | "yyyy-MM-dd-HH-mm-ss-" + "test-image.jpg" | サーバー保存時に付与される日時プレフィックス付きファイル名 |
| 12 | リクエストURL(表示) | /user/blog/register | 新規登録画面表示の GET エンドポイント |
| 13 | リクエストURL(登録) | /user/blog/register/process | 記事登録処理の POST(multipart) エンドポイント |
| 14 | 期待ビュー名(成功) | blog-register-fix.html | 登録成功時のビュー名 |
| 15 | 期待ビュー名(失敗/表示) | blog-register.html | 登録失敗時および画面表示時のビュー名 |
