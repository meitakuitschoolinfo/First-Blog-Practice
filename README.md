# Blog アプリケーション

Spring Boot で構築された、ユーザー認証付きの個人ブログ管理アプリケーションです。
ユーザーはアカウントを登録・ログインし、自分のブログ記事を一覧・登録・編集・画像差し替え・削除できます。

---

## 1. 概要

| 項目 | 内容 |
| --- | --- |
| アプリ名 | Blog（blog.ex） |
| 種別 | Web アプリケーション（サーバーサイドレンダリング） |
| 主な機能 | ユーザー登録 / ログイン / ログアウト / ブログ一覧 / 登録 / 編集 / 画像編集 / 削除 |
| アーキテクチャ | MVC（Controller - Service - DAO - Entity）+ Thymeleaf ビュー |

---

## 2. 技術スタック

| 分類 | 使用技術 |
| --- | --- |
| 言語 | Java 17 |
| フレームワーク | Spring Boot 3.0.4（Spring MVC / Spring Data JPA） |
| テンプレートエンジン | Thymeleaf |
| データベース | PostgreSQL |
| ORM | Hibernate（JPA） |
| ライブラリ | Lombok |
| ビルドツール | Maven（Maven Wrapper 同梱） |
| テスト | JUnit 5 / Spring Boot Test / Mockito（MockMvc） |

---

## 3. ディレクトリ構成（概要）

```
blog/
├── pom.xml                       … Maven 設定
├── README.md                     … 本ファイル
├── 仕様書/                        … 各種設計ドキュメント
└── src/
    ├── main/
    │   ├── java/blog/ex/
    │   │   ├── BlogApplication.java       … 起動クラス
    │   │   ├── controller/                … コントローラー（機能ごとに分割）
    │   │   ├── service/                   … ビジネスロジック
    │   │   └── model/
    │   │       ├── dao/                   … データアクセス（JpaRepository）
    │   │       └── entity/                … エンティティ（テーブルマッピング）
    │   └── resources/
    │       ├── application.properties     … DB / JPA 設定
    │       ├── templates/                 … Thymeleaf テンプレート
    │       └── static/                    … CSS / JS / 画像
    └── test/java/blog/ex/                 … テストコード
```

詳細は [仕様書/02_フォルダ階層.md](仕様書/02_フォルダ階層.md) を参照してください。

---

## 4. コントローラー構成（機能ごとに分割）

`BlogController` に集約されていた処理を、機能単位のコントローラーへ分割しました。
すべて `@RequestMapping("/user/blog")` を共有しつつ、責務を分離しています。

| コントローラー | 担当機能 | 主なエンドポイント |
| --- | --- | --- |
| `UserRegisterController` | ユーザー新規登録 | `GET /user/register`, `POST /user/register/process` |
| `UserLoginController` | ログイン | `GET /user/login`, `POST /user/login/process` |
| `BlogListController` | ブログ一覧表示 | `GET /user/blog/list` |
| `BlogRegisterController` | ブログ新規登録 | `GET /user/blog/register`, `POST /user/blog/register/process` |
| `BlogEditController` | ブログ記事編集（テキスト） | `GET /user/blog/edit/{blogId}`, `POST /user/blog/update` |
| `BlogImageEditController` | ブログ画像編集 | `GET /user/blog/image/edit/{blogId}`, `POST /user/blog/image/update` |
| `BlogDeleteController` | ブログ削除 | `GET /user/blog/delete/list`, `GET /user/blog/delete/detail/{blogId}`, `POST /user/blog/delete` |
| `LogoutController` | ログアウト | `GET /user/blog/logout` |

---

## 5. セットアップ・起動方法

### 5.1 前提

- **JDK 17**（`pom.xml` の `java.version=17`。本プロジェクトは Lombok を使用するため、JDK 17 でのビルドを推奨します）
- **PostgreSQL**（ローカルに起動していること）

### 5.2 データベース準備

`src/main/resources/application.properties` の設定に合わせて DB を用意します。

```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/blog_demo
spring.datasource.username=postgres
spring.datasource.password=123456
spring.jpa.hibernate.ddl-auto=update
```

- ポート `5433` で PostgreSQL を起動し、`blog_demo` データベースを作成してください。
- `ddl-auto=update` により、`users` / `blogs` テーブルは起動時に自動生成されます。

### 5.3 ビルドと起動

```bash
# Windows
mvnw.cmd spring-boot:run

# macOS / Linux
./mvnw spring-boot:run
```

起動後、ブラウザで以下へアクセスします。

```
http://localhost:8080/user/login
```

### 5.4 テスト実行

```bash
./mvnw test
```

---

## 6. 利用の流れ

1. `/user/register` でアカウントを登録する。
2. `/user/login` でログインする（成功するとブログ一覧へ遷移）。
3. ブログ一覧（`/user/blog/list`）から記事の登録・編集・削除を行う。
4. `/user/blog/logout` でログアウトする。

---

## 7. 設計ドキュメント

| ドキュメント | 内容 |
| --- | --- |
| [01_テーブル定義書](仕様書/01_テーブル定義書.md) | DB テーブル（users / blogs）のカラム定義 |
| [02_フォルダ階層](仕様書/02_フォルダ階層.md) | プロジェクトのフォルダ／ファイル構成 |
| [03_メソッド定義書](仕様書/03_メソッド定義書.md) | DAO / Service / Controller の全メソッド仕様 |
| [04_クラス図](仕様書/04_クラス図.md) | クラス構成と関連（Mermaid） |
| [05_シーケンス図](仕様書/05_シーケンス図.md) | 主要処理のシーケンス（Mermaid） |
| [06_ER図](仕様書/06_ER図.md) | エンティティ関連図（Mermaid） |
| [07_画面定義書](仕様書/07_画面定義書.md) | 各画面の項目・入出力定義 |
| [08_画面遷移図](仕様書/08_画面遷移図.md) | 画面遷移（Mermaid） |
| [09_画面イベント一覧](仕様書/09_画面イベント一覧.md) | 画面ごとのイベント・バリデーション一覧 |
| [10_機能一覧表](仕様書/10_機能一覧表.md) | 機能要件一覧（機能ID／画面／コントローラーの対応） |
| [11_エンドポイント一覧](仕様書/11_エンドポイント一覧.md) | 全 URL マッピングの早見表 |
| [12_CRUD図](仕様書/12_CRUD図.md) | 機能 × テーブルの CRUD マトリクス |
| [13_メッセージ一覧](仕様書/13_メッセージ一覧.md) | 画面メッセージの一覧（メッセージID付き） |
| [14_セキュリティ非機能設計](仕様書/14_セキュリティ非機能設計.md) | 現状の課題と改善案（認証・パスワード等） |
| [15_テストケース一覧](仕様書/15_テストケース一覧.md) | 自動テストの観点・ケース一覧 |
| [16_システム構成図](仕様書/16_システム構成図.md) | 技術構成・処理フロー（Mermaid） |
| [17_バリデーション仕様書](仕様書/17_バリデーション仕様書.md) | 入力チェックの現状と設計案 |
| [18_画面レイアウト](仕様書/18_画面レイアウト.md) | 各画面のワイヤーフレーム |

> Mermaid 図は GitHub・VS Code（Markdown Preview Mermaid 拡張）などでレンダリングされます。

---

## 8. 注意事項

- 本アプリのパスワードは平文で保存されています（学習用）。`UserService` 内にハッシュ化のサンプルがコメントで残されています。実運用ではハッシュ化が必須です。
- アップロード画像は `src/main/resources/static/blog-img/` に直接保存されます。
