# 📘 09. learnとの対応・発展トピック

― 「理想形（learn）」と「実コード」の差分を一望し、次の一歩を知る ―

> 対応する learn：第1章（Category）／第5章（Form）／第7章_2（Security）／第9章（Markdown）など
> 解説対象：learn 教材 全体 vs `src/` 実装

---

## 🎯 9-0. この章のゴール

- learn が教える理想形と、実コードの実装が **どこでズレているか**を一覧で把握できる
- それぞれのズレが「**なぜ生まれたか / どう埋めるか**」を説明できる
- このアプリを次に良くするための **改善の入口**を持つ

---

## 🗺 9-1. learn 章 → tech 章 → 実コードの対応表

| learn | テーマ | tech 解説 | 実コードの状態 |
| --- | --- | --- | --- |
| 第1章 | Category Entity | [02](02_Entity解説.md) | ❌ Category なし。`category` は文字列カラム |
| 第2章 | Blog Entity | [02](02_Entity解説.md) | ✅ `BlogEntity` あり（FKでなくStringカテゴリ） |
| 第3章 | User Entity | [02](02_Entity解説.md) | ✅ `UserEntity` あり |
| 第4章 | Repository | [03](03_Repository_DAO解説.md) | ✅ `BlogDao`/`UserDao` あり |
| 第5章 | Form | [05](05_Controller解説.md) | ❌ Form クラスなし。`@RequestParam` 直受け |
| 第6章 | Service | [04](04_Service解説.md) | ✅ `BlogService`/`UserService` あり |
| 第7章_1 | Controller | [05](05_Controller解説.md) | ✅ 8コントローラーに分割済み |
| 第7章_2 | Templates & SecurityConfig | [06](06_セッション認証と画像アップロード.md) | ❌ Spring Security なし。HttpSession 方式 |
| 第8章 | 単体テストとは | [07](07_単体テスト解説_Service編.md)・[08](08_単体テスト解説_Controller編.md) | ⚠️ あり（ただし JUnit assert・`@MockBean`・`@SpringBootTest`） |
| 第8章 | 画像アップロード対応 | [06](06_セッション認証と画像アップロード.md) | ✅ `MultipartFile`+`Files.copy` で実装 |
| 第9章 | マークダウン対応 | この章 9-5 | ❌ 未実装（依存もなし） |

凡例：✅ 実装あり / ⚠️ 実装あり(流派違い) / ❌ 実コードに無い

---

## 🧩 9-2. パッケージ・命名のズレ

| 項目 | learn | 実コード |
| --- | --- | --- |
| ルートパッケージ | `com.meitaku.blog` | `blog.ex` |
| エンティティ名 | `Blog` / `User` / `Category` | `BlogEntity` / `UserEntity` |
| リポジトリ名 | `BlogRepository` 等 | `BlogDao` / `UserDao`（`model.dao`） |
| 主キー戦略 | `IDENTITY` | `AUTO` |

> 💡 learn のコード断片（例：`com.meitaku.blog.entity.Category`）を実コードで探しても見つかりません。
> **learn は「教材としての理想形」**、tech は「**このリポジトリの実物**」という住み分けです。

---

## 🏷 9-3. 発展①：カテゴリーを「マスタ化」する（Category 化）

### 現状

```java
// BlogEntity
@NonNull @Column(name = "category")
private String category;        // ← 記事ごとに文字列でカテゴリーを持つ
```

- カテゴリーは記事行に直接書かれた **ただの文字列**。
- 弱点：表記ゆれ（「日記」「にっき」）が別物になる、カテゴリー一覧が作れない、改名が全記事更新になる。

### 理想形（learn 第1〜2章）

```text
categories (id, name)  1 ──< blogs (category_id FK)
```

- `Category` エンティティを作り、`blogs.category_id` で **外部キー参照**。
- `@ManyToOne` で `BlogEntity` から `Category` を関連付け。
- 利点：カテゴリーの一元管理・一覧・改名が容易。

> 移行するなら：`Category` Entity 追加 → `BlogEntity` に `@ManyToOne Category` → 既存 `category` 文字列をマスタへ移送、の順。

---

## 📝 9-4. 発展②：Form クラスでバリデーションを入れる

### 現状

```java
@PostMapping("/register/process")
public String blogRegister(@RequestParam String blogTitle,
        @RequestParam LocalDate registerDate, @RequestParam String category,
        @RequestParam MultipartFile blogImage, @RequestParam String blogDetail, Model model) { ... }
```

- 入力項目を `@RequestParam` で**1個ずつ**受け取る。
- 弱点：項目が増えると引数が膨らむ／**入力チェックが一切ない**（空文字・桁数・形式）。

### 理想形（learn 第5章）

```java
public class BlogForm {
    @NotBlank private String blogTitle;
    @NotNull  private LocalDate registerDate;
    @Size(max = 100) private String category;
    ...
}

@PostMapping("/register/process")
public String blogRegister(@Valid BlogForm form, BindingResult result, Model model) {
    if (result.hasErrors()) { return "blog-register.html"; }  // 入力エラーで戻す
    ...
}
```

- 画面入力を **1つの Form オブジェクト**にまとめ、`@Valid` + `BindingResult` で
  **サーバーサイドバリデーション**を行う。
- 仕様書 `仕様書/17_バリデーション仕様書.md` に、入れるべきチェックの設計案がある。

---

## 🔐 9-5. 発展③：Spring Security の導入

### 現状（[06](06_セッション認証と画像アップロード.md)）

- `session.setAttribute("user", ...)` の**自前セッション認証**。
- URL 保護なし（未ログインでも直接アクセスでき、NPE になる）。
- パスワード**平文**保存・**平文照合**。

### 理想形（learn 第7章_2）

| 項目 | 導入内容 |
| --- | --- |
| 依存 | `spring-boot-starter-security` を追加 |
| 設定 | `SecurityFilterChain` で URL 認可（`/user/blog/**` は認証必須） |
| パスワード | `BCryptPasswordEncoder` でハッシュ化（`UserService` のコメント版を発展） |
| 認証 | `UserDetailsService` 実装でDBユーザーを認証 |
| CSRF | デフォルト有効（テストは `.with(csrf())` が必要に） |

実コードの `UserService` 末尾には、SHA-256＋salt のハッシュ化サンプルが**コメントで残っています**。
本来はこれ（理想は BCrypt）を採用し、`findByEmailAndPassword`（平文一致）を
「emailで引いてハッシュ照合」に置き換えるのが正攻法です。

---

## 📄 9-6. 発展④：Markdown 対応（learn 第9章）

### 現状

- 記事本文 `blogDetail` は **プレーン文字列**として保存・表示。
- pom.xml に Markdown ライブラリ（commonmark / flexmark 等）の**依存はありません**。

### 理想形（learn 第9章）

```text
[ 入力: Markdown文字列 ] ──保存──▶ [ DB ]
                                      │ 表示時
                                      ▼
                        [ commonmark で HTML 変換 ]
                                      ▼
                        [ Thymeleaf で th:utext 描画 ]
```

- 保存はMarkdownのまま、**表示時にHTMLへ変換**するのが定石。
- 変換HTMLを `th:utext`（エスケープしない出力）で描画する際は、
  **XSS対策のサニタイズ**が必須（信頼できない入力をそのまま `utext` しない）。

---

## 🧪 9-7. 発展⑤：テストの「流派」を learn 推奨へ寄せる

[07](07_単体テスト解説_Service編.md)・[08](08_単体テスト解説_Controller編.md) で触れた通り、実テストと learn の推奨には流派差があります。

| 項目 | 実テスト | learn 第8章の推奨 | 寄せるなら |
| --- | --- | --- | --- |
| アサーション | JUnit `assertEquals` | AssertJ `assertThat` | 可読性重視なら AssertJ |
| モックBean | `@MockBean` | `@MockitoBean` | 新しいSpring Bootでは `@MockitoBean` |
| Controller起動 | `@SpringBootTest` | `@WebMvcTest` | 速度重視なら `@WebMvcTest` |
| Repositoryテスト | なし | `@DataJpaTest` | DAOのクエリ検証を足すなら |

> どちらも**間違いではありません**。現状のテストは全47ケースが通る前提で書かれており、
> learn は「より速く・より新しい書き方」を志向している、という関係です。

---

## 🐛 9-8. 実コードの既知の不具合・改善候補（総まとめ）

tech 各章で指摘した点を、優先度の目安付きで集約します。

| 優先 | 箇所 | 内容 | 直し方の方向 |
| --- | --- | --- | --- |
| 高 | パスワード平文 | 保存・照合とも平文（[02](02_Entity解説.md)・[04](04_Service解説.md)） | BCrypt でハッシュ化＋salt |
| 高 | 未ログインガード無し | セッションに `user` 無いと NPE（[06](06_セッション認証と画像アップロード.md)） | ガード追加 or Spring Security |
| 中 | `redirecr:` タイプミス | Edit/ImageEdit/Delete詳細の3箇所（[05](05_Controller解説.md)） | `redirect:` に修正＋テスト期待値も修正 |
| 中 | 画像保存パス | 実行時相対 `src/main/...`（[06](06_セッション認証と画像アップロード.md)） | 外部ディレクトリ＋絶対パス＋設定化 |
| 中 | 画像例外握りつぶし | 保存失敗してもDB登録に進む（[06](06_セッション認証と画像アップロード.md)） | 失敗時はロールバック/エラー画面 |
| 中 | 入力バリデーション無し | 空文字・桁数・形式の検証なし（[05](05_Controller解説.md)） | Form＋`@Valid` |
| 低 | カテゴリ文字列 | 表記ゆれ・一覧不可（9-3） | Category マスタ化 |
| 低 | `editBlogPost` 存在前提 | 無効IDで NPE（[04](04_Service解説.md)） | null チェック追加 |
| 低 | 登録結果を画面に出さない | `createAccount` の戻り値破棄（[05](05_Controller解説.md)） | 成否メッセージ表示 |

> ⚠️ これらは **「現状こうなっている」という観察**です。この解説書では**勝手に修正していません**。
> 直す場合は、対応する単体テストの期待値も併せて見直してください（特に `redirecr:` はテストが現状を固定しているため）。

---

## 📚 9-9. 併せて読みたい既存ドキュメント

このリポジトリには learn / tech 以外にも設計資料があります。

| フォルダ | 内容 |
| --- | --- |
| `仕様書/` | テーブル定義・ER図・クラス図・シーケンス図・エンドポイント一覧・バリデーション仕様 など18点 |
| `テスト仕様書作成/` | 各 Controller / Service の単体テスト仕様書（テストコードと1対1対応） |
| `learn/` | 概念を学ぶチュートリアル（理想形） |
| `tech/`（本書） | 実コードを読み解く解説 |

特に **`仕様書/15_テストケース一覧.md`** と `テスト仕様書作成/` は、
[07](07_単体テスト解説_Service編.md)・[08](08_単体テスト解説_Controller編.md) と相互参照すると理解が深まります。

---

## ✅ 9-10. まとめ

- learn は **理想形**（Category/Form/Security/Markdown 前提）、tech は **実コード**（それらが無い現状）。
- 主なズレ：パッケージ名・カテゴリ文字列・Formなし・Securityなしのセッション認証・Markdown未実装。
- テストは **流派違い**（JUnit assert / `@MockBean` / `@SpringBootTest`）だが正しく機能している。
- 既知の改善候補は **パスワードハッシュ化・未ログインガード・`redirecr:`修正**あたりが入口。
- 修正時は **対応テストの期待値**も一緒に直すこと。

---

🎉 これで tech 解説書は完結です。[README（目次）](README.md) に戻って、気になる層からもう一度読み直してみてください。
