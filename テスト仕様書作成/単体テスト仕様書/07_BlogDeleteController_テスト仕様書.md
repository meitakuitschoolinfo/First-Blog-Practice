# 単体テスト仕様書

| 項目 | 内容 | 項目 | 内容 |
| --- | --- | --- | --- |
| システム名 | Blog アプリケーション | 作成者 | （未記入） |
| サブシステム名 | ブログ記事削除機能 | 作成日 | （未記入） |
| 対象クラス | blog.ex.controller.BlogDeleteController | 実施者 | （未記入） |
| テスト方式 | Spring Boot Test + MockMvc + Mockito（BlogService をモック化） | - | - |

## テストケース一覧

| No | 分類 | テスト項目 | 検証内容 | 実施日 | 結果 | 備考・特記事項 |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | 表示テスト | 削除候補一覧の表示 | GET /user/blog/delete/list で findAllBlogPost が一覧を返す場合、ステータス 200、view 名が blog-delete.html、model に userName="John"・blogList（取得一覧）・deleteMessage="削除一覧" が設定されること。findAllBlogPost(1L) が 1 回呼ばれること。 |  | - | 一覧表示パターン |
| 2 | 表示テスト | 削除記事詳細の表示（記事あり） | GET /user/blog/delete/detail/{blogId} で getBlogPost が記事を返す場合、ステータス 200、view 名が blog-delete-detail.html、model に userName="John"・blogList（取得記事）・DeleteDetailMessage="削除記事詳細" が設定されること。getBlogPost が 1 回呼ばれること。 |  | - | 詳細表示パターン |
| 3 | 表示テスト | 削除記事詳細の表示（記事なし） | GET /user/blog/delete/detail/{blogId} で getBlogPost が null を返す場合、ステータス 200、view 名が "redirecr:/user/blog/list" となること（実コードのスペル通り）。getBlogPost が 1 回呼ばれること。 |  | - | 記事なし分岐 |
| 4 | 正常系 | 記事削除の成功 | POST /user/blog/delete で deleteBlog が true を返す場合、ステータス 200、view 名が blog-delete-fix.html となること。deleteBlog(1L) が 1 回呼ばれること。 |  | - | 削除成功パターン |
| 5 | 異常系 | 記事削除の失敗 | POST /user/blog/delete で deleteBlog が false を返す場合、ステータス 200、view 名が blog-delete.html、model に DeleteDetailMessage が存在すること。deleteBlog(1L) が 1 回呼ばれること。 |  | - | 削除失敗パターン |

## テストデータ

| No | 項目 | 値 | 説明 |
| --- | --- | --- | --- |
| 1 | ログインユーザー（session "user"） | userId=1L, userName="John" | @BeforeEach で MockHttpSession に設定する UserEntity |
| 2 | blogId（PathVariable / param） | 1L | 削除対象のブログ ID |
| 3 | findAllBlogPost(1L) の戻り値 | BlogEntity を 2 件含むリスト | No.1 で使用する一覧データ |
| 4 | getBlogPost(1L) の戻り値（記事あり） | blogId=1L, blogTitle="Test Blog" | No.2 で使用する BlogEntity |
| 5 | getBlogPost(1L) の戻り値（記事なし） | null | No.3 で使用 |
| 6 | deleteBlog(1L) の戻り値 | true（No.4）/ false（No.5） | 削除処理の成否を模擬 |
