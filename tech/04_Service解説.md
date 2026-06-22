# 📘 04. Service 解説

― 「業務の判断」を引き受ける層 ―

> 対応する learn：第6章（Service）
> 解説対象：[BlogService.java](../src/main/java/blog/ex/service/BlogService.java) / [UserService.java](../src/main/java/blog/ex/service/UserService.java)

---

## 🎯 4-0. この章のゴール

- `@Service` と `@Autowired` の役割を説明できる
- 各メソッドの **入力 → 判断（if）→ DAO 呼び出し → 戻り値** を追える
- 「null ガード」「重複チェック」など、**Service が持つべき判断**を指摘できる
- `boolean` を返すメソッドが「成功/失敗」をどう表現しているか説明できる

---

## 🧩 4-1. Service の立ち位置

```text
[ Controller ]  ── 画面/HTTP/セッションの担当
     │  値（String, Long...）を渡して呼ぶ
     ▼
[ Service ]     ── ★ここ：業務判断（nullか？重複か？）
     │  Entity を組み立てて DAO に渡す
     ▼
[ DAO ]         ── SQL の担当
```

Service は **「判断」** をします。
「userId が null なら何もしない」「同じ記事が既にあるなら登録しない」——
こうした **if 文＝業務ルール** が Service に集まります。

`@Service` を付けると Spring の DI コンテナに登録され、`@Autowired` で他クラス（Controller）へ注入されます。

---

## 🗂 4-2. BlogService（記事の業務ロジック）

[BlogService.java](../src/main/java/blog/ex/service/BlogService.java)。DAO を1つ注入し、5メソッドを提供します。

```java
@Service
public class BlogService {
    @Autowired
    private BlogDao blogDao;
    ...
}
```

| メソッド | 入力 | 判断（if） | 成功時の動作 | 戻り値 |
| --- | --- | --- | --- | --- |
| `findAllBlogPost` | userId | userId == null | `findByUserId` で一覧取得 | `List<BlogEntity>` / null |
| `createBlogPost` | title,date,file,detail,category,userId | 同タイトル＋同日付が既存か | `save` で新規登録 | `true` / `false` |
| `getBlogPost` | blogId | blogId == null | `findByBlogId` で1件取得 | `BlogEntity` / null |
| `editBlogPost` | title,date,detail,category,userId,blogId | userId == null | 取得→setして`save` | `true` / `false` |
| `editBlogImage` | blogId,fileName,userId | fileName==null か 画像名が同じ | 画像名setして`save` | `true` / `false` |
| `deleteBlog` | blogId | blogId == null | `deleteByBlogId` | `true` / `false` |

### 4-2-1. `findAllBlogPost` ― null ガードの基本形

```java
public List<BlogEntity> findAllBlogPost(Long userId){
    if(userId == null) {
        return null;
    } else {
        return blogDao.findByUserId(userId);
    }
}
```

- userId が無ければ DB を引かずに即 `null`。
- これがあるおかげで「ログインしていない＝userIdが取れない」ケースでも DAO を無駄打ちしない。
- テスト No.1（`testFindAllBlogPost_UserIdNull_ReturnsNull`）が **まさにこの分岐**を検証しています（[07](07_単体テスト解説_Service編.md)）。

### 4-2-2. `createBlogPost` ― 「重複なら登録しない」

```java
public boolean createBlogPost(String blogTitle, LocalDate registerDate, String fileName,
        String blogDetail, String category, Long userId) {
    BlogEntity blogList = blogDao.findByBlogTitleAndRegisterDate(blogTitle, registerDate);
    if(blogList == null) {
        blogDao.save(new BlogEntity(blogTitle, registerDate, fileName, blogDetail, category, userId));
        return true;            // 新規登録できた
    } else {
        return false;           // 同じタイトル＋日付が既にある → 登録しない
    }
}
```

- **重複の定義** ＝「タイトル」と「登録日」が両方一致（`findByBlogTitleAndRegisterDate`）。
- 重複していなければ `BlogEntity` を **手書き6引数コンストラクタ**で組み立てて `save`。
- `true`/`false` で「登録できた/重複で弾いた」を Controller に伝える。
  Controller はこの戻り値で画面を分岐（成功→完了画面 / 失敗→「既に登録済みです」）。

> 💡 ここで渡す `fileName` は **画像の保存ファイル名**。実際のファイル保存は Controller が担当し、
> Service には「保存後のファイル名（文字列）」だけが渡ってくる点に注意（[05](05_Controller解説.md)・[06](06_セッション認証と画像アップロード.md)）。

### 4-2-3. `getBlogPost` ― 取得の null ガード

```java
public BlogEntity getBlogPost(Long blogId) {
    if(blogId == null) return null;
    else return blogDao.findByBlogId(blogId);
}
```

編集・画像編集・削除詳細の各画面が、表示前にこれで1件取得します。

### 4-2-4. `editBlogPost` ― 「取得してから上書き保存」

```java
public boolean editBlogPost(String blogTitle, LocalDate registerDate, String blogDetail,
        String category, Long userId, Long blogId) {
    BlogEntity blogList = blogDao.findByBlogId(blogId);   // ① まず取得
    if(userId == null) {
        return false;
    } else {
        blogList.setBlogId(blogId);
        blogList.setBlogTitle(blogTitle);
        blogList.setRegisterDate(registerDate);
        blogList.setCategory(category);
        blogList.setBlogDetail(blogDetail);
        blogList.setUserId(userId);
        blogDao.save(blogList);                            // ② 上書き保存
        return true;
    }
}
```

読みどころ（実装のクセ）：

1. **取得（`findByBlogId`）が null ガードより前**にある。
   もし `blogId` に対応する記事が無いと `blogList` は null で、`setBlogId` 呼び出しで
   `NullPointerException` になり得る。**「記事が存在する前提」**の実装。
2. `blogImage`（画像名）は **set していない**。
   → テキスト編集では画像を触らない設計（画像は `editBlogImage` の担当）。
   `save` は取得した既存 Entity をそのまま使うので、画像名は維持される。
3. 判断条件が `userId == null`。
   テスト No.7 はこの分岐を検証するため、`findByBlogId` のモックを設定したうえで `userId=null` を渡しています（[07](07_単体テスト解説_Service編.md)）。

### 4-2-5. `editBlogImage` ― 「同じ画像なら更新しない」

```java
public boolean editBlogImage(Long blogId, String fileName, Long userId) {
    BlogEntity blogList = blogDao.findByBlogId(blogId);
    if(fileName == null || blogList.getBlogImage().equals(fileName)) {
        return false;        // ファイル名が無い or 既存と同名 → 更新しない
    } else {
        blogList.setBlogId(blogId);
        blogList.setBlogImage(fileName);   // 画像名だけ差し替え
        blogList.setUserId(userId);
        blogDao.save(blogList);
        return true;
    }
}
```

- 「**新しいファイル名でなければ無駄な更新をしない**」というルール。
- ここでも `findByBlogId` の結果が null だと `getBlogImage()` で NPE になり得る（記事存在前提）。
- テスト No.9（fileName=null）・No.10（同名）・No.11（新名で成功）がこの3分岐を網羅。

### 4-2-6. `deleteBlog` ― null ガードのみ

```java
public boolean deleteBlog(Long blogId) {
    if(blogId == null) return false;
    else { blogDao.deleteByBlogId(blogId); return true; }
}
```

- 削除は「IDがあれば消す、無ければ false」のシンプル分岐。
- `deleteByBlogId` の戻り値（List）は使わず、`true` を返すだけ。

---

## 👤 4-3. UserService（アカウントの業務ロジック）

[UserService.java](../src/main/java/blog/ex/service/UserService.java)。2メソッド。

### 4-3-1. `createAccount` ― 「同じメールがあれば登録しない」

```java
public boolean createAccount(String userName, String email, String password) {
    LocalDateTime registerDate = LocalDateTime.now();      // 登録日時を“今”に
    UserEntity userEntity = userDao.findByEmail(email);    // 既存チェック
    if (userEntity == null) {
        userDao.save(new UserEntity(userName, email, password, registerDate)); // 4引数
        return true;
    } else {
        return false;      // メール重複 → 登録しない
    }
}
```

- **email の一意性をここで担保**（DBにUNIQUE制約が無いため、Service の責務）。
- `registerDate` を `LocalDateTime.now()` で**Service が手動セット**。
  （Entity に `@PrePersist` が無いので自動セットされない → [02](02_Entity解説.md)）
- `new UserEntity(...)` の4引数は `@RequiredArgsConstructor` 生成版。
- パスワードは **平文のまま** `save`。
- テスト No.1（新規→true）・No.2（重複→false）が対応（[07](07_単体テスト解説_Service編.md)）。

### 4-3-2. `loginAccount` ― 平文一致での認証

```java
public UserEntity loginAccount(String email, String password) {
    UserEntity userEntity = userDao.findByEmailAndPassword(email, password);
    if (userEntity == null) return null;     // 該当なし → ログイン失敗
    else return userEntity;                  // 該当あり → そのユーザーを返す
}
```

- email と password の **両方一致**で1件取れたら成功。null なら失敗。
- 返すのは `UserEntity` 本体。Controller はこれをセッションに入れる（[05](05_Controller解説.md)・[06](06_セッション認証と画像アップロード.md)）。
- if/else が実質「取れたものをそのまま返す」だけなので、`return userEntity;` 一行と等価。
  （learn 的には冗長だが、読みやすさ優先で残している）

### 4-3-3. コメントアウトされた「暗号化版」

`UserService` の末尾には、**ハッシュ化版 `createAccount`/`loginAccount`** が
コメントとして残されています（`MessageDigest`・SHA-256・salt を使う）。

```java
/* ... salt を付けて hashPassword(password+salt) を保存 ... */
```

- これは「本来はこうすべき」という参考実装。
- 採用するには `UserEntity` に `salt` カラムを追加し、`findByEmail` で引いてから比較する流れに変える必要がある。
- 詳細な発展は [09](09_learnとの対応・発展トピック.md)。

---

## 🧠 4-4. 「Service が返す boolean / null」の読み方

このアプリの Service は、結果を **2通りの形**で返します。

| 返し方 | 意味 | 使っているメソッド |
| --- | --- | --- |
| `boolean`（true/false） | 処理が成功したか | create/edit/delete 系 |
| オブジェクト or `null` | 取得できたか | find/get/login 系 |

Controller はこの戻り値を見て **画面を分岐**します。
「成功なら完了画面、失敗ならエラーメッセージ付きで元画面」というパターンが繰り返し出てきます（[05](05_Controller解説.md)）。

---

## ❌ 4-5. 気をつけポイント

| 事実 | 注意点 |
| --- | --- |
| `editBlogPost`/`editBlogImage` が「記事存在前提」 | `findByBlogId` が null だと NPE。存在しないIDで呼ぶと落ちる |
| `loginAccount` の if/else が冗長 | 実質 `return userEntity;` 一行で済む |
| パスワード平文 | `createAccount`/`loginAccount` ともに平文。ハッシュ化はコメントのみ |
| `registerDate` は Service が手動セット | Entityに自動日時の仕掛けが無いため |
| 例外を投げない設計 | 失敗は `false`/`null` で表現。呼び出し側の分岐が前提 |

---

## ✅ 4-6. まとめ

- Service は **業務判断（if）** の場所。null ガード・重複チェックがここに集まる。
- 結果は **`boolean`（成功可否）** か **`オブジェクト/null`（取得可否）** で返す。
- 編集系は「取得 → set → save」の流れ。**記事が存在する前提**で書かれている。
- 認証・登録日時・重複チェックは **Service の責務**（DB制約に頼っていない）。
- ハッシュ化は **コメントの参考実装のみ**で、実際は平文。

---

➡️ 次は [05_Controller解説](05_Controller解説.md)。この Service を呼び出し、画面とセッションをさばく層へ。
