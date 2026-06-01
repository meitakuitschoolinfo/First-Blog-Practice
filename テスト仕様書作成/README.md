# テスト仕様書作成

Blog アプリケーションの **Service 層・Controller 層** に対する単体テスト仕様書と、
1 行ずつ日本語コメントを付けたテストソースをまとめたフォルダです。

書式は提供された見本「単体テスト仕様書（ユーザーログインテスト）」に準拠しています。

## フォルダ構成

```
テスト仕様書作成/
├── README.md                       … 本ファイル
└── 単体テスト仕様書/                 … テスト仕様書（Markdown・PDF様式の表）
```

> テストソース（.java）は `src/test/java/blog/ex/` 配下へ移行済みです（下記「2. テストソース」参照）。

## 1. 単体テスト仕様書（`単体テスト仕様書/`）

各ファイルは「システム名／サブシステム名／対象クラス／テスト方式」のヘッダと、
「No・分類・テスト項目・検証内容・実施日・結果・備考」のテストケース一覧、テストデータ表で構成されます。
（実施日・結果は未実施のため空欄／`-`）

| No | 対象クラス | ファイル | ケース数 |
| --- | --- | --- | --- |
| 01 | UserLoginController | [01_UserLoginController_テスト仕様書.md](単体テスト仕様書/01_UserLoginController_テスト仕様書.md) | 6 |
| 02 | UserRegisterController | [02_UserRegisterController_テスト仕様書.md](単体テスト仕様書/02_UserRegisterController_テスト仕様書.md) | 3 |
| 03 | BlogListController | [03_BlogListController_テスト仕様書.md](単体テスト仕様書/03_BlogListController_テスト仕様書.md) | 3 |
| 04 | BlogRegisterController | [04_BlogRegisterController_テスト仕様書.md](単体テスト仕様書/04_BlogRegisterController_テスト仕様書.md) | 3 |
| 05 | BlogEditController | [05_BlogEditController_テスト仕様書.md](単体テスト仕様書/05_BlogEditController_テスト仕様書.md) | 4 |
| 06 | BlogImageEditController | [06_BlogImageEditController_テスト仕様書.md](単体テスト仕様書/06_BlogImageEditController_テスト仕様書.md) | 4 |
| 07 | BlogDeleteController | [07_BlogDeleteController_テスト仕様書.md](単体テスト仕様書/07_BlogDeleteController_テスト仕様書.md) | 5 |
| 08 | LogoutController | [08_LogoutController_テスト仕様書.md](単体テスト仕様書/08_LogoutController_テスト仕様書.md) | 2 |
| 09 | UserService | [09_UserService_テスト仕様書.md](単体テスト仕様書/09_UserService_テスト仕様書.md) | 4 |
| 10 | BlogService | [10_BlogService_テスト仕様書.md](単体テスト仕様書/10_BlogService_テスト仕様書.md) | 13 |

合計 **47 テストケース**。

## 2. テストソース（`src/test/java/blog/ex/` へ移行済み）

仕様書の各ケースに対応する JUnit 5 テストです。import 文・閉じ括弧を除く実行行すべてに日本語コメントを付けています。
ビルド・実行できるよう、ソースは正式なテストディレクトリへ移行しました。

| 種別 | テスト方式 | 配置先 | ファイル |
| --- | --- | --- | --- |
| Controller | Spring Boot Test + MockMvc + Mockito（Service を `@MockBean`） | `src/test/java/blog/ex/controller/` | `UserLoginControllerTest` / `UserRegisterControllerTest` / `LogoutControllerTest` / `BlogListControllerTest` / `BlogRegisterControllerTest` / `BlogEditControllerTest` / `BlogImageEditControllerTest` / `BlogDeleteControllerTest` |
| Service | JUnit 5 + Mockito（DAO を `@Mock`） | `src/test/java/blog/ex/service/` | `UserServiceTest` / `BlogServiceTest` |

### 実行方法

```bash
./mvnw test
```

> 移行に伴い、分割前の旧テスト `BlogControllerTest.java` / `BlogControllerTest2.java`（旧モノリシック `BlogController` を URL 経由でテスト）は、機能分割後の 6 つの `Blog*ControllerTest` に置き換わったため削除しました。`UserLoginControllerTest` / `UserRegisterControllerTest` は本コメント付き版で更新しています。

## 補足（実コードに関する所見）

- **記事なし時のリダイレクト**: `BlogEditController` / `BlogImageEditController` / `BlogDeleteController` の「記事が存在しない」分岐は、リダイレクト文字列が `redirecr:/user/blog/list`（`redirect` のスペル誤り）になっています。このため Spring はリダイレクトせず、その文字列をビュー名として解決します。テストは**現行コードの実挙動どおり**（`status().isOk()` + `view().name("redirecr:/user/blog/list")`）に記述しています。コードを修正する場合はテストの該当行も `redirectedUrl("/user/blog/list")` へ変更が必要です。
- **ユーザー登録の戻り**: `UserRegisterController.register` は `createAccount` の成否に関わらず常に `redirect:/user/login` を返します。重複ケースもリダイレクト先で検証しています。
