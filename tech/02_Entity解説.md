# 📘 02. Entity 解説

― テーブル1行＝Javaクラス。データの「形」を読む ―

> 対応する learn：第1章（Category）／第2章（Blog）／第3章（User）
> 解説対象：[BlogEntity.java](../src/main/java/blog/ex/model/entity/BlogEntity.java) / [UserEntity.java](../src/main/java/blog/ex/model/entity/UserEntity.java)

---

## 🎯 2-0. この章のゴール

- `@Entity` / `@Table` / `@Id` / `@GeneratedValue` / `@Column` の意味を実コードで説明できる
- このアプリの2テーブル（`blogs` / `users`）の構造を言える
- Lombok の `@Data` / `@NoArgsConstructor` / `@AllArgsConstructor` / `@RequiredArgsConstructor` / `@NonNull` が何を生成するか説明できる
- learn の理想形（`Category`・`@PrePersist`・`IDENTITY`）と実コードの違いを指摘できる

---

## 🧩 2-1. このアプリのテーブルは2つだけ

```text
users  ── (user_id) ──< blogs
```

- `users` … アカウント（ログインに使う）
- `blogs` … 記事。`user_id` で「誰の記事か」を持つ

> ⚠️ learn 第1章は `categories` テーブル（`Category` エンティティ）から始まりますが、
> **実コードに `categories` テーブルも `Category` クラスもありません。**
> カテゴリーは `blogs.category`（`String`）として記事ごとに直接持っています。
> （理由と理想形は [09](09_learnとの対応・発展トピック.md)）

---

## 🧱 2-2. BlogEntity（blogs テーブル）

[BlogEntity.java](../src/main/java/blog/ex/model/entity/BlogEntity.java) の本体（コメントを除いた骨格）：

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "blogs")
public class BlogEntity {

    @Id
    @Column(name = "blog_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long blogId;

    @NonNull @Column(name = "blog_title")
    private String blogTitle;

    @NonNull @DateTimeFormat(pattern = "yyyy-MM-dd") @Column(name = "register_date")
    private LocalDate registerDate;

    @NonNull @Column(name = "blog_image")
    private String blogImage;

    @NonNull @Column(name = "blog_detail")
    private String blogDetail;

    @NonNull @Column(name = "category")
    private String category;

    @Column(name = "user_id")
    private Long userId;

    // @RequiredArgsConstructor とは別に、明示的な6引数コンストラクタも定義
    public BlogEntity(@NonNull String blogTitle, @NonNull LocalDate registerDate,
            @NonNull String blogImage, @NonNull String blogDetail,
            @NonNull String category, Long userId) { ... }
}
```

### カラム対応表

| フィールド | カラム | 型 | NonNull | 役割 |
| --- | --- | --- | :---: | --- |
| `blogId` | `blog_id` | `Long` | （主キー） | 記事ID（自動採番） |
| `blogTitle` | `blog_title` | `String` | ✓ | タイトル |
| `registerDate` | `register_date` | `LocalDate` | ✓ | 登録日（**日付のみ**） |
| `blogImage` | `blog_image` | `String` | ✓ | 画像ファイル名 |
| `blogDetail` | `blog_detail` | `String` | ✓ | 本文 |
| `category` | `category` | `String` | ✓ | カテゴリー名 |
| `userId` | `user_id` | `Long` | （任意） | 投稿者ID |

### 読みどころ①：主キーは `Long`＋`AUTO`

```java
@Id
@GeneratedValue(strategy = GenerationType.AUTO)
private Long blogId;
```

- `@Id` … この項目が **PRIMARY KEY**。Entity に必ず1つ必要。
- `Long`（ラッパー型） … 新規作成時は `null`（＝未採番）を表せる。`long` だと 0 になり「ID=0で更新」事故の元（learn 第1章 1-6）。
- `GenerationType.AUTO` … 採番方法を **JPA におまかせ**。PostgreSQL では Hibernate がシーケンス等を選ぶ。

> 📝 learn 第1章は `IDENTITY`（DB の自動採番列）を推奨しています。
> 実コードは `AUTO`。どちらでも動きますが、DB を固定するなら `IDENTITY` の方が挙動が読みやすい、というのが learn の主張です（[09](09_learnとの対応・発展トピック.md)）。

### 読みどころ②：`registerDate` は `LocalDate`（時刻なし）

```java
@DateTimeFormat(pattern = "yyyy-MM-dd")
@Column(name = "register_date")
private LocalDate registerDate;
```

- `LocalDate` … **日付だけ**（時分秒なし）。記事は「日付」で十分という設計。
- `@DateTimeFormat(pattern = "yyyy-MM-dd")` … 画面から来る `"2026-06-01"` という文字列を `LocalDate` に変換するための書式指定。Controller の `@RequestParam LocalDate registerDate` がこの書式で受け取れるのはこの宣言のおかげ。
- 対する `UserEntity.registerDate` は `LocalDateTime`（時刻あり）。**同じ「登録日」でも型が違う**点に注意。

### 読みどころ③：`@Column` に制約を書いていない

learn 第1章は `nullable=false` / `unique=true` / `length=N` を **Entity に明記** することを推奨していますが、
実コードの `@Column` は **`name` しか指定していません**。

```java
@NonNull @Column(name = "blog_title")   // ← nullable も length も無い
private String blogTitle;
```

- DB 上は `ddl-auto=update` が型を作りますが、**NOT NULL / UNIQUE / 桁数の制約は付きません**。
- `@NonNull` は **Lombok のアノテーション**であって JPA の制約ではない点に注意（次節）。

---

## 🧰 2-3. Lombok アノテーションの読み方（最重要）

`BlogEntity` / `UserEntity` 双方に同じ4つ＋`@NonNull`が付いています。
**これらが何を自動生成しているか** を理解しないと、コンストラクタ呼び出しが読めません。

| アノテーション | 自動生成されるもの |
| --- | --- |
| `@Data` | 全フィールドの getter/setter＋`toString`/`equals`/`hashCode` |
| `@NoArgsConstructor` | 引数なしコンストラクタ `new BlogEntity()` |
| `@AllArgsConstructor` | 全フィールド（7個）を引数に取るコンストラクタ |
| `@RequiredArgsConstructor` | `@NonNull` 付きフィールドだけを引数に取るコンストラクタ |
| `@NonNull` | そのフィールドの setter/コンストラクタで **null チェック**（null なら `NullPointerException`） |

### `@NonNull` は「JPAのNOT NULL」ではない

- `@NonNull`（Lombok）＝ **Javaコード上で** null を渡したら例外を投げる、という実行時チェック。
- DB の `NOT NULL` 制約とは別物。**DB列にNOT NULLは付きません**（`@Column(nullable=false)` を書いていないため）。

### コンストラクタが「2系統」あるのに注意

`BlogEntity` には Lombok 生成分に加えて、**手書きの6引数コンストラクタ**もあります。

```java
// 手書き（user_id を含む6引数。blog_id・採番系は含まない）
public BlogEntity(String blogTitle, LocalDate registerDate, String blogImage,
                  String blogDetail, String category, Long userId) { ... }
```

これが **Service やテストで使われる主役コンストラクタ**です。例：

```java
// BlogService#createBlogPost 内
new BlogEntity(blogTitle, registerDate, fileName, blogDetail, category, userId);
```

```java
// BlogServiceTest 内
new BlogEntity("t1", LocalDate.of(2026,6,1), "i1.png", "d1", "日記", 1L);
```

> 💡 `@RequiredArgsConstructor` は `@NonNull` フィールド（`blogTitle`〜`category` の5個）ですが、
> 手書きの6引数（`userId` 込み）コンストラクタがあるため、**実際に使われるのは手書きの方**です。
> 引数の **順番**（title→date→image→detail→category→userId）は丸暗記でなく、このコンストラクタ定義を見て確認するのが正解。

---

## 🧱 2-4. UserEntity（users テーブル）

[UserEntity.java](../src/main/java/blog/ex/model/entity/UserEntity.java) の骨格：

```java
@Data @NoArgsConstructor @AllArgsConstructor @RequiredArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    @NonNull @Column(name = "user_name")  private String userName;
    @NonNull @Column(name = "email")      private String email;
    @NonNull @Column(name = "password")   private String password;
    @NonNull @Column(name = "register_date") private LocalDateTime registerDate;
}
```

### カラム対応表

| フィールド | カラム | 型 | 役割 |
| --- | --- | --- | --- |
| `userId` | `user_id` | `Long` | ユーザーID（主キー・自動採番） |
| `userName` | `user_name` | `String` | 表示名 |
| `email` | `email` | `String` | ログインID。**UNIQUE制約は付いていない**（コード上の重複チェックのみ） |
| `password` | `password` | `String` | **平文**で保存（学習用） |
| `registerDate` | `register_date` | `LocalDateTime` | 登録日時（**時刻あり**） |

### 読みどころ：4引数コンストラクタはどこから来る？

`UserEntity` には手書きコンストラクタがありません。よってテストの

```java
new UserEntity("John", "john@test.com", "password", LocalDateTime.now());  // 4引数
```

は **`@RequiredArgsConstructor`**（`@NonNull` の4フィールド）が生成したものです。
一方テストには **5引数**版もあります：

```java
new UserEntity(1L, "Akemi", "ake@test.com", "1234abcd", LocalDateTime.now()); // 5引数
```

こちらは **`@AllArgsConstructor`**（全5フィールド：`userId` 込み）が生成したものです。
👉 「4引数＝Required（NonNullのみ）／5引数＝All（全部）」と読み分けられます。

---

## 🔑 2-5. emailの重複・パスワード照合は「どこで」やる？

`UserEntity` の `@Column` には `unique=true` も `nullable=false` もありません。
ではログインIDの一意性やパスワード照合はどこで担保しているか？

- **email の重複チェック** … `UserService#createAccount` が `findByEmail` で事前検索（[04](04_Service解説.md)）
- **パスワード照合** … `UserService#loginAccount` が `findByEmailAndPassword` で **完全一致検索**（平文比較）

つまり制約を **DBでなくJava側のロジックで** 担保しています。
DB に UNIQUE が無いので、**理論上は二重登録の競合が起こりうる**（学習段階の割り切り）。

---

## ❌ 2-6. この Entity の「気をつけポイント」

| 事実 | 何が起こる／なぜ気にするか |
| --- | --- |
| `@Column` に制約未指定 | DB列に NOT NULL/UNIQUE/桁数が付かない。`@NonNull` はJava側だけ |
| パスワードが平文 | `users.password` がそのまま見える。本番はハッシュ必須 |
| `@PrePersist`/`@PreUpdate` 不使用 | 作成/更新日時の自動セットは無い。`registerDate` は Service が手で入れる |
| `BlogEntity` にコンストラクタ2系統 | どれが呼ばれるか混乱しやすい → 引数の数で見分ける |
| `category` が文字列カラム | カテゴリーのマスタ管理・一覧取得ができない（[09](09_learnとの対応・発展トピック.md)） |

---

## ✅ 2-7. まとめ

- Entity は **テーブル1行の形**。このアプリは `blogs` / `users` の2つ。
- 主キーは `Long` + `@GeneratedValue(AUTO)`。
- 制約は `@Column` に書かれておらず、**重複/必須チェックは Service 側のロジック**で担保。
- Lombok のコンストラクタ（Required=NonNullのみ / All=全部）と、`BlogEntity` の手書き6引数を見分けられると、Service・テストが一気に読めるようになる。
- learn の `Category` / `@PrePersist` / `IDENTITY` は **実コードには無い**理想形。

---

➡️ 次は [03_Repository(DAO)解説](03_Repository_DAO解説.md)。この Entity を DB と橋渡しする層へ。
