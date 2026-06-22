# 🛠 tech ― ソースコード解説書

このフォルダは、`learn/` の学習教材を土台にしながら、
**このリポジトリに実際に実装されているソースコード（`src/main` と `src/test`）** を
1行レベルで読み解くための **技術解説書（解説ノート）** です。

- `learn/` … 「どう作るか・なぜそう作るか」を学ぶ **チュートリアル（理想形）**
- `tech/`（このフォルダ） … 「**今このリポジトリに何が書かれているか**」を読み解く **コードリーディング解説**

> 📌 大事な前提：`learn/` と実コードには **意図的なズレ** があります。
> 例えば `learn/` は `com.meitaku.blog` パッケージ・`Category` エンティティ・`Form` クラス・Spring Security・Markdown を前提に説明していますが、
> **実コードは `blog.ex` パッケージで、`Category` も `Form` も Spring Security も Markdown も使っていません。**
> この解説書は **「実コードが正」** として書き、learn の理想形との差分は [09_learnとの対応・発展トピック.md](09_learnとの対応・発展トピック.md) にまとめています。

---

## 📚 目次

| No | ドキュメント | 対応する learn | 解説対象のソース |
| --- | --- | --- | --- |
| 01 | [アーキテクチャと全体像](01_アーキテクチャと全体像.md) | 全章の前提 | `BlogApplication` / `pom.xml` / `application.properties` |
| 02 | [Entity 解説](02_Entity解説.md) | 第1〜3章 | `BlogEntity` / `UserEntity` |
| 03 | [Repository(DAO) 解説](03_Repository_DAO解説.md) | 第4章 | `BlogDao` / `UserDao` |
| 04 | [Service 解説](04_Service解説.md) | 第6章 | `BlogService` / `UserService` |
| 05 | [Controller 解説](05_Controller解説.md) | 第5章・第7章_1 | `controller/` 配下の8クラス |
| 06 | [セッション認証と画像アップロード](06_セッション認証と画像アップロード.md) | 第7章_2・第8章(画像) | ログイン/ログアウト・画像保存処理 |
| 07 | [単体テスト解説（Service編）](07_単体テスト解説_Service編.md) | 第8章(単体テスト) | `BlogServiceTest` / `UserServiceTest` |
| 08 | [単体テスト解説（Controller編）](08_単体テスト解説_Controller編.md) | 第8章(単体テスト) | `controller/*Test`（8クラス） |
| 09 | [learnとの対応・発展トピック](09_learnとの対応・発展トピック.md) | 第1・5・7_2・9章 | Category / Form / Security / Markdown |

---

## 🧭 このアプリは何か（30秒サマリー）

ユーザー認証付きの **個人ブログ管理アプリ**（サーバーサイドレンダリング）。

```text
登録 → ログイン → ブログ一覧 → 記事の登録 / 編集 / 画像差し替え / 削除 → ログアウト
```

| 分類 | 採用技術 |
| --- | --- |
| 言語 | Java 17 |
| フレームワーク | Spring Boot 3.0.4（Spring MVC / Spring Data JPA） |
| ビュー | Thymeleaf |
| DB | PostgreSQL（Hibernate / JPA） |
| 補助 | Lombok |
| テスト | JUnit 5 / Spring Boot Test / Mockito / MockMvc |

---

## 🏗 レイヤー構成（このリポジトリの実物）

```text
[ ブラウザ ]
     │  HTTP（GET / POST）
     ▼
[ Controller ]  blog.ex.controller … 8クラス（機能ごとに分割）
     │  メソッド呼び出し
     ▼
[ Service ]     blog.ex.service … BlogService / UserService（業務判断）
     │
     ▼
[ DAO ]         blog.ex.model.dao … BlogDao / UserDao（JpaRepository）
     │
     ▼
[ Entity ]      blog.ex.model.entity … BlogEntity / UserEntity（テーブル1行）
     │
     ▼
[ PostgreSQL ]  blogs / users テーブル
```

ログインユーザーの保持は **`HttpSession`**（`"user"` 属性に `UserEntity` を格納）で行います。
Spring Security は **使っていません**（→ 詳細は [06](06_セッション認証と画像アップロード.md)）。

---

## 🔰 読む順番のおすすめ

1. まず [01_アーキテクチャと全体像](01_アーキテクチャと全体像.md) で地図を持つ
2. データの「形」から下→上に [02 Entity](02_Entity解説.md) → [03 DAO](03_Repository_DAO解説.md) → [04 Service](04_Service解説.md) → [05 Controller](05_Controller解説.md)
3. 横断機能の [06 セッション認証と画像](06_セッション認証と画像アップロード.md)
4. テストの読み解き [07 Service編](07_単体テスト解説_Service編.md) → [08 Controller編](08_単体テスト解説_Controller編.md)
5. 最後に [09 learnとの対応](09_learnとの対応・発展トピック.md) で「理想形と実装の差」を理解する

---

## ⚠️ 解説書を読むうえでの注意（実コードの既知のクセ）

このリポジトリの実コードには、学習段階ゆえの **クセ・あえて直していない点** が含まれます。
解説書では「ここはこうなっている」と **事実として** 記述します（無断で修正はしません）。代表例：

- `redirect:` のタイプミス `redirecr:` が編集・画像編集・削除詳細の3コントローラーに存在する（→ リダイレクトされずビュー解決される）。
- パスワードが **平文** で保存・照合される（`UserService` にハッシュ化のサンプルがコメントで残る）。
- 画像は `src/main/resources/static/blog-img/` に **実行時ディレクトリ相対** で直接保存される。
- `@GeneratedValue(strategy = GenerationType.AUTO)` を採用（learn は `IDENTITY` 推奨）。

これらの「なぜ問題か／どう直すか」は各ドキュメントと [09](09_learnとの対応・発展トピック.md) で触れます。
