# 単体テスト仕様書

| 項目 | 内容 | 項目 | 内容 |
| --- | --- | --- | --- |
| システム名 | Blog アプリケーション | 作成者 | （未記入） |
| サブシステム名 | ブログ記事画像編集機能 | 作成日 | （未記入） |
| 対象クラス | blog.ex.controller.BlogImageEditController | 実施者 | （未記入） |
| テスト方式 | Spring Boot Test + MockMvc + Mockito（BlogService をモック化） | - | - |

## テストケース一覧

| No | 分類 | テスト項目 | 検証内容 | 実施日 | 結果 | 備考・特記事項 |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | 表示テスト | 画像編集画面の表示（記事あり） | GET /user/blog/image/edit/{blogId} で getBlogPost が記事を返す場合、ステータス 200、view 名が blog-img-edit.html、model に userName="John"・blogList（取得記事）・editImageMessage="画像編集" が設定されること。getBlogPost が 1 回呼ばれること。 |  | - | 正常表示パターン |
| 2 | 表示テスト | 画像編集画面の表示（記事なし） | GET /user/blog/image/edit/{blogId} で getBlogPost が null を返す場合、ステータス 200、view 名が "redirecr:/user/blog/list" となること（実コードのスペル通り）。getBlogPost が 1 回呼ばれること。 |  | - | 記事なし分岐 |
| 3 | 正常系 | 画像更新の成功 | POST /user/blog/image/update（multipart）で editBlogImage が true を返す場合、ステータス 200、view 名が blog-edit-fix.html となること。editBlogImage が（blogId=1L, 生成ファイル名, userId=1L）で 1 回呼ばれること。 |  | - | multipart 送信。ファイル名は日時プレフィックス + 元ファイル名 |
| 4 | 異常系 | 画像更新の失敗 | POST /user/blog/image/update（multipart）で editBlogImage が false を返す場合、ステータス 200、view 名が blog-img-edit.html、model に blogList・editImageMessage が存在すること。editBlogImage が 1 回、失敗分岐の getBlogPost が 1 回呼ばれること。 |  | - | 失敗時に getBlogPost で再取得し画面再表示 |

## テストデータ

| No | 項目 | 値 | 説明 |
| --- | --- | --- | --- |
| 1 | ログインユーザー（session "user"） | userId=1L, userName="John" | @BeforeEach で MockHttpSession に設定する UserEntity |
| 2 | blogId（PathVariable / param） | 1L | 編集対象のブログ ID |
| 3 | getBlogPost(1L) の戻り値（記事あり） | blogId=1L, blogTitle="Test Blog" | No.1・No.4 で使用する BlogEntity |
| 4 | getBlogPost(1L) の戻り値（記事なし） | null | No.2 で使用 |
| 5 | アップロード画像（MockMultipartFile） | name="blogImage", originalFilename="test-image.jpg", contentType="image/jpeg", content=new byte[0] | No.3・No.4 で送信する画像ファイル |
| 6 | 生成ファイル名 | "yyyy-MM-dd-HH-mm-ss-" + "test-image.jpg" | コントローラが日時プレフィックスを付与して生成（検証用に同形式で算出） |
| 7 | editBlogImage(...) の戻り値 | true（No.3）/ false（No.4） | 画像更新処理の成否を模擬 |
