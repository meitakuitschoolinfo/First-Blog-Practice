# 単体テスト仕様書

| 項目 | 内容 | 項目 | 内容 |
| --- | --- | --- | --- |
| システム名 | Blog アプリケーション | 作成者 | （未記入） |
| サブシステム名 | ブログ記事編集機能（テキスト項目） | 作成日 | （未記入） |
| 対象クラス | blog.ex.controller.BlogEditController | 実施者 | （未記入） |
| テスト方式 | Spring Boot Test + MockMvc + Mockito（BlogService をモック化） | - | - |

## テストケース一覧

| No | 分類 | テスト項目 | 検証内容 | 実施日 | 結果 | 備考・特記事項 |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | 表示テスト | 記事編集画面の表示（記事あり） | GET /user/blog/edit/{blogId} で getBlogPost が記事を返す場合、ステータス 200、view 名が blog-edit.html、model に userName="John"・blogList（取得記事）・editMessage="記事編集" が設定されること。getBlogPost が 1 回呼ばれること。 |  | - | 正常表示パターン |
| 2 | 表示テスト | 記事編集画面の表示（記事なし） | GET /user/blog/edit/{blogId} で getBlogPost が null を返す場合、ステータス 200、view 名が "redirecr:/user/blog/list" となること（実コードのスペル通り。リダイレクトではなくビュー名解決）。getBlogPost が 1 回呼ばれること。 |  | - | 記事なし分岐。文字列 "redirecr" は実装通り |
| 3 | 正常系 | 記事更新の成功 | POST /user/blog/update で editBlogPost が true を返す場合、ステータス 200、view 名が blog-edit-fix.html となること。editBlogPost が指定引数で 1 回呼ばれること。 |  | - | 更新成功パターン |
| 4 | 異常系 | 記事更新の失敗 | POST /user/blog/update で editBlogPost が false を返す場合、ステータス 200、view 名が blog-edit.html、model に registerMessage が存在すること。editBlogPost が指定引数で 1 回呼ばれること。 |  | - | 更新失敗パターン |

## テストデータ

| No | 項目 | 値 | 説明 |
| --- | --- | --- | --- |
| 1 | ログインユーザー（session "user"） | userId=1L, userName="John" | @BeforeEach で MockHttpSession に設定する UserEntity |
| 2 | blogId（PathVariable） | 1L | 編集対象のブログ ID |
| 3 | getBlogPost(1L) の戻り値（記事あり） | blogId=1L, blogTitle="Test Blog", registerDate=2023-06-01 | No.1 で使用する BlogEntity |
| 4 | getBlogPost(1L) の戻り値（記事なし） | null | No.2 で使用 |
| 5 | 更新フォームパラメータ | blogTitle="Updated Blog", registerDate="2023-06-02", category="Updated Category", blogDetail="Updated Blog Detail", blogId="1" | No.3・No.4 の POST パラメータ |
| 6 | editBlogPost(...) の戻り値 | true（No.3）/ false（No.4） | 更新処理の成否を模擬 |
