# 単体テスト仕様書

| 項目 | 内容 | 項目 | 内容 |
| --- | --- | --- | --- |
| システム名 | Blog アプリケーション | 作成者 | （未記入） |
| サブシステム名 | ブログ一覧機能 | 作成日 | （未記入） |
| 対象クラス | blog.ex.controller.BlogListController | 実施者 | （未記入） |
| テスト方式 | Spring Boot Test + MockMvc + Mockito（BlogService をモック化） | - | - |

## テストケース一覧

| No | 分類 | テスト項目 | 検証内容 | 実施日 | 結果 | 備考・特記事項 |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | 表示テスト | ブログ一覧ページの表示（GET /user/blog/list） | HTTP ステータスが 200(OK) であること。ビュー名が "blog-list.html" であること。モデルに "userName" と "blogList" 属性が存在すること。"userName" が "John" であること。 | | - | セッションに userId=1L / userName="John" の UserEntity を設定 |
| 2 | 正常系 | 一覧データの内容検証 | モデルの "blogList" 属性が、blogService.findAllBlogPost(1L) のモック戻り値（BlogEntity 2 件のリスト）と一致すること。 | | - | findAllBlogPost(1L) をスタブし戻り値を検証 |
| 3 | 正常系 | サービス呼び出し検証 | blogService.findAllBlogPost(1L) が引数 1L で 1 回だけ呼び出されること。 | | - | Mockito verify(times(1)) で確認 |

## テストデータ

| No | 項目 | 値 | 説明 |
| --- | --- | --- | --- |
| 1 | userId | 1L | セッションに格納するログインユーザーの ID |
| 2 | userName | "John" | セッションに格納するログインユーザーの名前 |
| 3 | email | "john@example.com" | UserEntity 生成用のメールアドレス |
| 4 | password | "password" | UserEntity 生成用のパスワード |
| 5 | registerDate(user) | LocalDateTime.now() | UserEntity の登録日時 |
| 6 | blogList | BlogEntity 2 件のリスト | findAllBlogPost(1L) のモック戻り値 |
| 7 | リクエストURL | /user/blog/list | テスト対象の GET エンドポイント |
| 8 | 期待ビュー名 | blog-list.html | 一覧画面のビュー名 |
