# 📘 07. 単体テスト解説（Service編）

― Mockito で「DBを使わずに業務判断だけ」を検証する ―

> 対応する learn：第8章（単体テストとは）
> 解説対象：[BlogServiceTest.java](../src/test/java/blog/ex/service/BlogServiceTest.java) / [UserServiceTest.java](../src/test/java/blog/ex/service/UserServiceTest.java)

---

## 🎯 7-0. この章のゴール

- `@ExtendWith(MockitoExtension.class)` / `@Mock` / `@InjectMocks` の3点セットを説明できる
- `when(...).thenReturn(...)` と `verify(...)` の役割を区別できる
- 「正常系/異常系」がメソッド名と検証内容にどう対応するか読める
- Service の **if 分岐をすべて網羅**するテスト設計を追える

---

## 🧩 7-1. Service テストの戦略：DBを使わない

Service の仕事は **業務判断（if）** でした（[04](04_Service解説.md)）。
ここで本物の DB を繋ぐと、遅く・環境依存になります。そこで——

```text
[ BlogService ]  ← テスト対象（本物）
     │
     ▼
[ BlogDao ]      ← @Mock で偽物に差し替え！（DBに行かない）
```

DAO を **モック（偽物）** にして、「DAO がこう返したら Service はこう動くか」だけを高速に検証します。
これが **Mockito** の役割。`@SpringBootTest` は使わず、**素のJUnit + Mockito** なので速い。

---

## 🔧 7-2. 3点セットの読み方

両テストの冒頭は同じ構造です（`BlogServiceTest` を例に）：

```java
@ExtendWith(MockitoExtension.class)   // ① JUnit5でMockitoを有効化
public class BlogServiceTest {

    @Mock                              // ② 偽物のDAOを作る
    private BlogDao blogDao;

    @InjectMocks                       // ③ ②を注入した本物のServiceを作る
    private BlogService blogService;
    ...
}
```

| アノテーション | 意味 |
| --- | --- |
| `@ExtendWith(MockitoExtension.class)` | `@Mock`/`@InjectMocks` を機能させる JUnit5 拡張 |
| `@Mock` | そのフィールドを **空の偽物**にする（全メソッドが既定で null/false を返す） |
| `@InjectMocks` | テスト対象を生成し、その中に `@Mock` を **自動注入** |

👉 これで `blogService` の中の `blogDao` は **偽物に差し替わった状態**になる。

---

## 🅰️ 7-3. Mockito の基本フロー（AAA）

各テストは learn 第8章の **AAA（Arrange-Act-Assert）** で読めます。

```java
// Arrange（準備）：モックがどう振る舞うか定義
when(blogDao.findByUserId(eq(1L))).thenReturn(list);

// Act（実行）：テスト対象を1回呼ぶ
List<BlogEntity> result = blogService.findAllBlogPost(1L);

// Assert（検証）：戻り値とモック呼び出しを確認
assertEquals(list, result);
verify(blogDao, times(1)).findByUserId(eq(1L));
```

### `when().thenReturn()` と `verify()` の違い（最重要）

| 道具 | 目的 | 例 |
| --- | --- | --- |
| `when(x).thenReturn(y)` | モックの **戻り値を仕込む**（入口の準備） | 「`findByBlogId(10L)` が呼ばれたら entity を返せ」 |
| `verify(x).method()` | モックが **呼ばれたか/何回か検証**（出口の確認） | 「`save` がちょうど1回呼ばれたか」 |
| `verify(x, never())` | **呼ばれていない**ことを検証 | 「重複時に `save` が呼ばれないこと」 |

### 引数マッチャ

| マッチャ | 意味 |
| --- | --- |
| `eq(1L)` | 引数がちょうど `1L` |
| `any()` / `any(BlogEntity.class)` | 任意の引数 |

---

## 🗂 7-4. BlogServiceTest（全13ケース）の地図

[BlogServiceTest.java](../src/test/java/blog/ex/service/BlogServiceTest.java) は、Service の **5メソッド × 分岐**を網羅します。

| No | テストメソッド | 対象 | 系統 | 検証の核 |
| --- | --- | --- | --- | --- |
| 1 | `testFindAllBlogPost_UserIdNull_ReturnsNull` | findAllBlogPost | 異常 | null返却＋`findByUserId`が呼ばれない |
| 2 | `testFindAllBlogPost_UserIdSpecified_ReturnsList` | findAllBlogPost | 正常 | DAOのリストがそのまま返る＋件数2 |
| 3 | `testCreateBlogPost_NoDuplicate_Success` | createBlogPost | 正常 | true＋`save`が1回 |
| 4 | `testCreateBlogPost_Duplicate_Fail` | createBlogPost | 異常 | false＋`save`が呼ばれない |
| 5 | `testGetBlogPost_BlogIdNull_ReturnsNull` | getBlogPost | 異常 | null＋`findByBlogId`が呼ばれない |
| 6 | `testGetBlogPost_BlogIdSpecified_ReturnsEntity` | getBlogPost | 正常 | DAOのEntityが返る |
| 7 | `testEditBlogPost_UserIdNull_Fail` | editBlogPost | 異常 | false＋`save`が呼ばれない |
| 8 | `testEditBlogPost_UserIdSpecified_Success` | editBlogPost | 正常 | true＋値が更新され`save`1回 |
| 9 | `testEditBlogImage_FileNameNull_Fail` | editBlogImage | 異常 | false＋`save`なし |
| 10 | `testEditBlogImage_SameFileName_Fail` | editBlogImage | 異常 | 既存と同名→false＋`save`なし |
| 11 | `testEditBlogImage_NewFileName_Success` | editBlogImage | 正常 | true＋画像名更新＋`save`1回 |
| 12 | `testDeleteBlog_BlogIdNull_Fail` | deleteBlog | 異常 | false＋`deleteByBlogId`なし |
| 13 | `testDeleteBlog_BlogIdSpecified_Success` | deleteBlog | 正常 | true＋`deleteByBlogId`1回 |

> 💡 メソッド名が **`対象_条件_期待結果`** になっているので、名前を読むだけで意図が分かる（learn 8-9 の命名規則そのもの）。

### ケース深掘り①：No.1（null ガードの検証）

```java
@Test
public void testFindAllBlogPost_UserIdNull_ReturnsNull() {
    List<BlogEntity> result = blogService.findAllBlogPost(null);  // Act
    assertNull(result);                                            // 戻り値検証
    verify(blogDao, never()).findByUserId(any());                 // DAOに行っていない検証
}
```

- ここには `when(...)` が **無い**。理由：null ガードで `return null` される＝**DAOを呼ばないから**、戻り値を仕込む必要がない。
- `verify(..., never())` で「**無駄にDBを叩いていない**」ことまで保証しているのが上手いところ。

### ケース深掘り②：No.4（重複なら save しない）

```java
when(blogDao.findByBlogTitleAndRegisterDate(eq("既存タイトル"), eq(date))).thenReturn(existing);
boolean result = blogService.createBlogPost("既存タイトル", date, "img.png", "本文", "日記", 1L);
assertFalse(result);
verify(blogDao, never()).save(any(BlogEntity.class));   // 重複時はsaveしない
```

- 「重複検索で既存を返す」と仕込む → Service が `false` を返し、`save` を呼ばないことを検証。
- 正常系 No.3 はその逆（`thenReturn(null)` で「重複なし」→ `save` 1回）。

### ケース深掘り③：No.8（編集の「値が変わったか」まで検証）

```java
BlogEntity entity = new BlogEntity("旧タイトル", date, "img.png", "旧本文", "日記", 1L);
when(blogDao.findByBlogId(eq(10L))).thenReturn(entity);
boolean result = blogService.editBlogPost("更新後タイトル", date, "更新本文", "趣味", 1L, 10L);
assertTrue(result);
assertEquals("更新後タイトル", entity.getBlogTitle());   // ★Entityが書き換わったか
verify(blogDao, times(1)).save(entity);                  // その同一インスタンスでsave
```

- `findByBlogId` が返す **同じ entity インスタンス** を Service が `set...` で書き換える設計（[04](04_Service解説.md)）。
- だから「`entity.getBlogTitle()` が更新後値になっているか」で**書き換えの事実**を検証できる。

### ケース深掘り④：No.7 の伏線（存在前提のクセを逆手に）

```java
when(blogDao.findByBlogId(eq(10L))).thenReturn(entity);  // ← null ガードより先に呼ばれるため必要
boolean result = blogService.editBlogPost("更新後タイトル", date, "更新本文", "趣味", null, 10L);
assertFalse(result);
```

- コメントに「**null ガード前に取得されるため**」とある通り、`editBlogPost` は
  `findByBlogId` を **userId チェックより前**に呼ぶ（[04](04_Service解説.md) 4-2-4）。
- もし entity を仕込まないと `findByBlogId` が null を返し、後続で NPE になり得る。
  この **実装のクセを踏まえて** モックを用意しているのが、このテストの読みどころ。

---

## 👤 7-5. UserServiceTest（全4ケース）

[UserServiceTest.java](../src/test/java/blog/ex/service/UserServiceTest.java)

| No | テストメソッド | 対象 | 系統 | 検証の核 |
| --- | --- | --- | --- | --- |
| 1 | `testCreateAccount_NewUser_Success` | createAccount | 正常 | メール未登録→true＋`save`1回 |
| 2 | `testCreateAccount_DuplicateEmail_Fail` | createAccount | 異常 | メール重複→false＋`save`なし |
| 3 | `testLoginAccount_Success` | loginAccount | 正常 | 認証成功→該当Entity返却 |
| 4 | `testLoginAccount_Fail` | loginAccount | 異常 | 認証失敗→null |

### ケース：No.1（新規登録）

```java
when(userDao.findByEmail(eq("john@test.com"))).thenReturn(null);   // 未登録を演出
boolean result = userService.createAccount("John", "john@test.com", "password");
assertTrue(result);
verify(userDao, times(1)).save(any(UserEntity.class));            // 1回保存
```

- `findByEmail` が null（＝未登録）→ Service が `save` して `true`。
- `save` の引数は `any(UserEntity.class)`。
  Service 内で `registerDate = LocalDateTime.now()` を**実行時に生成**するため、
  Entity を厳密一致 `eq` で指定できない（毎回 now が変わる）。だから `any` が適切。

### ケース：No.3（ログイン成功）

```java
UserEntity user = new UserEntity("John", "john@test.com", "password", LocalDateTime.now());
when(userDao.findByEmailAndPassword(eq("john@test.com"), eq("password"))).thenReturn(user);
UserEntity result = userService.loginAccount("john@test.com", "password");
assertEquals(user, result);    // DAOが返したものをそのまま返す
```

- `assertEquals(user, result)` は **同じ参照**を返していることの確認。
  （`@Data` が `equals` を生成しているので値比較にもなるが、ここでは同一インスタンス）

---

## 🧠 7-6. このテスト群から学べる「設計の型」

| 型 | 具体例 |
| --- | --- |
| **1分岐 = 1テスト** | `editBlogImage` の3分岐→No.9/10/11の3本 |
| **正常系と異常系をペアで** | create成功(No.3)↔重複(No.4)、login成功(No.3)↔失敗(No.4) |
| **戻り値＋副作用の両方を検証** | `assertTrue` ＋ `verify(save)` |
| **「呼ばれない」も検証** | `verify(never())` で無駄打ち防止を保証 |
| **実装のクセを織り込む** | No.7 の「null前にfindByBlogId」を見越したモック |

> 📝 learn 8-8 は AssertJ（`assertThat`）と `@MockitoBean` を推奨していますが、
> **実テストは JUnit Assertions（`assertEquals` 等）と素の Mockito**を使っています。
> どちらも正しく、結果は同じ。流派の違いと捉えてください（[09](09_learnとの対応・発展トピック.md)）。

---

## ✅ 7-7. まとめ

- Service テストは **DAO をモック化**して、業務判断（if）だけを高速検証。
- `@Mock`/`@InjectMocks`/`@ExtendWith(MockitoExtension.class)` の3点セット。
- `when().thenReturn()` で入口を仕込み、`verify()` で出口（呼ばれた/呼ばれない）を確認。
- `BlogServiceTest` 13本＋`UserServiceTest` 4本で、**全メソッドの全分岐を網羅**。
- メソッド名 `対象_条件_期待結果` を読むだけで意図が分かる。

---

➡️ 次は [08_単体テスト解説（Controller編）](08_単体テスト解説_Controller編.md)。今度は HTTP を擬似的に流す MockMvc です。
