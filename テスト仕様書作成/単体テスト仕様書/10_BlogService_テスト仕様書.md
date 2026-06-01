# 単体テスト仕様書

| 項目 | 内容 | 項目 | 内容 |
| --- | --- | --- | --- |
| システム名 | Blog アプリケーション | 作成者 | （未記入） |
| サブシステム名 | ブログ管理サービス（BlogService） | 作成日 | （未記入） |
| 対象クラス | blog.ex.service.BlogService | 実施者 | （未記入） |
| テスト方式 | JUnit 5 + Mockito（DAO をモック化） | - | - |

## テストケース一覧

| No | 分類 | テスト項目 | 検証内容 | 実施日 | 結果 | 備考・特記事項 |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | 異常系 | findAllBlogPost：userId が null | userId が null の場合、戻り値が null であり、blogDao.findByUserId が呼ばれないこと |  | - | null ガード分岐 |
| 2 | 正常系 | findAllBlogPost：userId 指定時の一覧取得 | userId が非 null の場合、blogDao.findByUserId(userId) の戻り値（List）がそのまま返却されること |  | - | リスト件数・内容が DAO 戻り値と一致 |
| 3 | 正常系 | createBlogPost：重複なしの新規投稿 | findByBlogTitleAndRegisterDate が null の場合、blogDao.save が呼ばれ、戻り値が true であること |  | - | save が 1 回呼ばれることを verify |
| 4 | 異常系 | createBlogPost：タイトル＋登録日が重複 | findByBlogTitleAndRegisterDate が既存 Entity を返す場合、戻り値が false であり、save が呼ばれないこと |  | - | save が呼ばれないことを verify(never) |
| 5 | 異常系 | getBlogPost：blogId が null | blogId が null の場合、戻り値が null であり、blogDao.findByBlogId が呼ばれないこと |  | - | null ガード分岐 |
| 6 | 正常系 | getBlogPost：blogId 指定時の取得 | blogId が非 null の場合、blogDao.findByBlogId(blogId) の戻り値がそのまま返却されること |  | - | 返却 Entity が DAO 戻り値と同一 |
| 7 | 異常系 | editBlogPost：userId が null | userId が null の場合、戻り値が false であり、blogDao.save が呼ばれないこと |  | - | findByBlogId は呼ばれる（null ガード前に取得） |
| 8 | 正常系 | editBlogPost：userId 指定時の更新 | userId が非 null の場合、取得した Entity に各値を設定して blogDao.save が呼ばれ、戻り値が true であること |  | - | save が 1 回呼ばれることを verify |
| 9 | 異常系 | editBlogImage：fileName が null | fileName が null の場合、戻り値が false であり、blogDao.save が呼ばれないこと |  | - | findByBlogId は呼ばれる |
| 10 | 異常系 | editBlogImage：既存画像と同一ファイル名 | fileName が既存の blogImage と等しい場合、戻り値が false であり、save が呼ばれないこと |  | - | OR 条件の右辺分岐 |
| 11 | 正常系 | editBlogImage：新しいファイル名での更新 | fileName が非 null かつ既存画像と異なる場合、Entity に画像名を設定して blogDao.save が呼ばれ、戻り値が true であること |  | - | save が 1 回呼ばれることを verify |
| 12 | 異常系 | deleteBlog：blogId が null | blogId が null の場合、戻り値が false であり、blogDao.deleteByBlogId が呼ばれないこと |  | - | null ガード分岐 |
| 13 | 正常系 | deleteBlog：blogId 指定時の削除 | blogId が非 null の場合、blogDao.deleteByBlogId(blogId) が呼ばれ、戻り値が true であること |  | - | deleteByBlogId が 1 回呼ばれることを verify |

## テストデータ

| No | 項目 | 値 | 説明 |
| --- | --- | --- | --- |
| 1 | userId | null | 一覧取得の null ガード検証用 |
| 2 | userId | 1L | 一覧取得対象のユーザー ID |
| 2 | 戻り値 List | [BlogEntity, BlogEntity]（2 件） | findByUserId が返すブログ一覧 |
| 3 | blogTitle | "新規タイトル" | 重複なし投稿のタイトル |
| 3 | registerDate | LocalDate.of(2026,6,1) | 登録日 |
| 3 | fileName | "image.png" | 画像ファイル名 |
| 3 | blogDetail | "本文" | ブログ詳細 |
| 3 | category | "日記" | カテゴリー |
| 3 | userId | 1L | 投稿ユーザー ID |
| 4 | （既存）BlogEntity | new BlogEntity("既存タイトル", date, "img.png", "本文", "日記", 1L) | findByBlogTitleAndRegisterDate が返す既存投稿 |
| 5 | blogId | null | 取得の null ガード検証用 |
| 6 | blogId | 10L | 取得対象のブログ ID |
| 6 | 戻り値 BlogEntity | new BlogEntity("タイトル", date, "img.png", "本文", "日記", 1L) | findByBlogId が返す Entity |
| 7 | userId | null | 編集の null ガード検証用 |
| 7 | blogId | 10L | 編集対象のブログ ID |
| 8 | blogTitle / registerDate / blogDetail / category / userId / blogId | "更新後タイトル" / date / "更新本文" / "趣味" / 1L / 10L | 更新用パラメータ |
| 9 | blogId / fileName / userId | 10L / null / 1L | 画像編集の fileName null 検証用 |
| 10 | 既存 blogImage | "same.png" | 既存 Entity に設定済みの画像名 |
| 10 | fileName | "same.png" | 既存と同一のファイル名 |
| 11 | 既存 blogImage | "old.png" | 既存 Entity の画像名 |
| 11 | fileName | "new.png" | 新しいファイル名（既存と異なる） |
| 12 | blogId | null | 削除の null ガード検証用 |
| 13 | blogId | 10L | 削除対象のブログ ID |
