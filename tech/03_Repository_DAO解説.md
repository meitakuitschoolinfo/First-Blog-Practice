# 📘 03. Repository(DAO) 解説

― メソッド名を書くだけで SQL が生える層 ―

> 対応する learn：第4章（Repository）
> 解説対象：[BlogDao.java](../src/main/java/blog/ex/model/dao/BlogDao.java) / [UserDao.java](../src/main/java/blog/ex/model/dao/UserDao.java)

---

## 🎯 3-0. この章のゴール

- `JpaRepository<T, ID>` を継承すると何が手に入るか説明できる
- **メソッド名から SQL が自動生成される**（クエリメソッド）仕組みを説明できる
- このアプリで定義された各クエリメソッドが、どんな SQL になるか言える
- `@Transactional` が `deleteByBlogId` に付いている理由を説明できる

---

## 🧩 3-1. DAO とは（このアプリでの位置づけ）

```text
[ Service ]
     │  blogDao.findByUserId(userId) のように呼ぶ
     ▼
[ DAO（インターフェース） ]  ← 実装クラスは書かない！
     │  Spring Data JPA が実行時に実装を自動生成
     ▼
[ DB（blogs / users） ]
```

ポイントは **「インターフェースしか書いていない」** こと。
`BlogDao` も `UserDao` も `interface` で、実装クラスは存在しません。
それでも動くのは、**Spring Data JPA が起動時にプロキシ実装を自動生成**してくれるからです。

---

## 🗃 3-2. BlogDao を読む

[BlogDao.java](../src/main/java/blog/ex/model/dao/BlogDao.java)（コメント除く骨格）：

```java
public interface BlogDao extends JpaRepository<BlogEntity, Long> {

    List<BlogEntity> findByUserId(Long userId);

    BlogEntity save(BlogEntity blogEntity);

    BlogEntity findByBlogTitleAndRegisterDate(String blogTitle, LocalDate registerDate);

    BlogEntity findByBlogId(Long blogId);

    @Transactional
    List<BlogEntity> deleteByBlogId(Long blogId);
}
```

### `extends JpaRepository<BlogEntity, Long>` の意味

| 型引数 | 意味 |
| --- | --- |
| `BlogEntity` | このリポジトリが扱う **エンティティ型** |
| `Long` | そのエンティティの **主キーの型**（`blogId` は `Long`） |

これを継承するだけで、`save` / `findAll` / `findById` / `deleteById` / `count` などの
**標準 CRUD メソッドが最初から使える**ようになります。

### 自分で定義したクエリメソッドと、生成されるSQL

Spring Data JPA は **メソッド名を解析**して SQL を作ります（「クエリメソッド」）。

| メソッド | 命名の分解 | 生成される SQL（概念） | 戻り値 |
| --- | --- | --- | --- |
| `findByUserId(Long)` | find + By + UserId | `SELECT * FROM blogs WHERE user_id = ?` | `List<BlogEntity>` |
| `findByBlogTitleAndRegisterDate(String, LocalDate)` | find + By + BlogTitle + And + RegisterDate | `... WHERE blog_title = ? AND register_date = ?` | `BlogEntity`（1件 or null） |
| `findByBlogId(Long)` | find + By + BlogId | `... WHERE blog_id = ?` | `BlogEntity`（1件 or null） |
| `deleteByBlogId(Long)` | delete + By + BlogId | `DELETE FROM blogs WHERE blog_id = ?` | `List<BlogEntity>` |

> 💡 `findByUserId` だけ `List` を返し、他の `findBy...` は単体を返します。
> これは **「その条件で複数件あり得るか／1件か」** を戻り値の型で表現しているだけ。
> （`user_id` は複数記事があり得る → List。`blog_id` は主キー → 単体）

### `save` を **明示的に再宣言** している意味

`save` は `JpaRepository` が既に持っているので、**書かなくても使えます**。
ここで再宣言しているのは「使うことを明示する」程度の意味で、機能的な追加はありません。
（learn 第4章の方針に沿った“見える化”の書き方）

### `@Transactional` が `deleteByBlogId` に付く理由

```java
@Transactional
List<BlogEntity> deleteByBlogId(Long blogId);
```

- **更新系（delete/insert/update）のクエリメソッドはトランザクション内で実行する必要がある**。
- 参照系（`findBy...`）は Spring Data が読み取り用トランザクションを内部で用意するので明示不要。
- `deleteBy...` のような **派生 delete** は、明示的に `@Transactional` を付けないと
  実行時に `TransactionRequiredException` 等になり得るため、ここで付けています。
- 付けることで「途中で例外が出たらロールバック」され、データ整合性が守られます。

> 📝 戻り値が `List<BlogEntity>`（削除した件数/対象を返す形）になっていますが、
> 呼び出し元 `BlogService#deleteBlog` は **戻り値を使わず** `true` を返すだけです（[04](04_Service解説.md)）。

---

## 👤 3-3. UserDao を読む

[UserDao.java](../src/main/java/blog/ex/model/dao/UserDao.java)：

```java
public interface UserDao extends JpaRepository<UserEntity, Long> {

    UserEntity save(UserEntity userEntity);

    UserEntity findByEmail(String email);

    UserEntity findByEmailAndPassword(String email, String password);
}
```

| メソッド | 生成される SQL | どこで使う？ |
| --- | --- | --- |
| `findByEmail(String)` | `SELECT * FROM users WHERE email = ?` | 新規登録の **重複チェック**（`createAccount`） |
| `findByEmailAndPassword(String, String)` | `... WHERE email = ? AND password = ?` | **ログイン認証**（`loginAccount`） |
| `save(UserEntity)` | INSERT or UPDATE | アカウント保存 |

### `findByEmailAndPassword` ＝「平文パスワードでの認証」

このメソッドが **このアプリの認証の核心**です。

```text
email と password の両方が一致する行があれば → その UserEntity を返す（ログイン成功）
無ければ → null（ログイン失敗）
```

- パスワードは Entity で平文保存されているため、**SQL の WHERE で平文比較**しています。
- 本来はハッシュ化した値を保存し、「emailで引いてから、ハッシュを比較」する流れにすべき
  （`UserService` のコメントにそのサンプルが残っています → [04](04_Service解説.md)・[09](09_learnとの対応・発展トピック.md)）。

---

## 🔍 3-4. クエリメソッド命名の早見表

メソッド名は「魔法」ではなく **規則**です。覚えておくと自分で増やせます。

| 書き方 | 意味 |
| --- | --- |
| `findBy` + フィールド名 | その項目で検索 |
| `And` / `Or` | 条件の連結（`findByEmailAndPassword`） |
| `deleteBy` + フィールド名 | その項目で削除 |
| 戻り値 `List<T>` | 複数件返る前提 |
| 戻り値 `T`（単体） | 0〜1件返る前提（無ければ null） |

> ⚠️ フィールド名は **Entity のプロパティ名**（`blogTitle`）で書く。カラム名（`blog_title`）ではない。
> Spring Data がプロパティ名→カラム名へ変換します。

---

## ❌ 3-5. 気をつけポイント

| 事実 | 注意点 |
| --- | --- |
| `findBy...` が単体を返す設計 | 条件に一致が複数あると例外になり得る（`email` 重複時など）。DBにUNIQUEが無いので運用で防いでいる |
| `deleteByBlogId` の戻り値未使用 | 戻り値 `List` は捨てられ、Service は `true` 固定で返す |
| 実装クラスを書いていない | 「動かない？」ではなく **Spring Data が自動生成**。これがDAOの正しい姿 |
| 平文パスワードでの `findByEmailAndPassword` | 認証としては脆弱（ハッシュ化が本来必要） |

---

## ✅ 3-6. まとめ

- DAO は **インターフェースだけ**。Spring Data JPA が実装を自動生成する。
- `JpaRepository<Entity, ID>` 継承で標準CRUDが手に入る。
- **メソッド名 → SQL** が自動生成される（`findByUserId` 等）。
- 更新系の派生メソッド（`deleteByBlogId`）には `@Transactional` を付ける。
- 認証は `findByEmailAndPassword` による平文一致（学習用）。

---

➡️ 次は [04_Service解説](04_Service解説.md)。この DAO を呼び出して「業務判断」を行う層へ。
