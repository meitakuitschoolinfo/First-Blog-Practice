# 📘 08. 単体テスト解説（Controller編）

― MockMvc で「HTTPを擬似的に流して」画面遷移を検証する ―

> 対応する learn：第8章（単体テストとは）
> 解説対象：[controller/](../src/test/java/blog/ex/controller/) 配下の8テストクラス

---

## 🎯 8-0. この章のゴール

- `@SpringBootTest` + `@AutoConfigureMockMvc` + `@MockBean` の役割を説明できる
- `mockMvc.perform(...).andExpect(...)` の読み方を身につける
- `view()` / `model()` / `redirectedUrl()` / `status()` の検証マッチャを使い分けられる
- セッションやファイルアップロードを伴うテストの組み立てを読める
- `redirecr:` タイプミスを **テストがどう追認しているか**を理解する

---

## 🧩 8-1. Controller テストの戦略：本物のSpring＋偽物のService

```text
[ MockMvc ] ──擬似HTTP──▶ [ 本物のController ]
                                  │
                                  ▼
                          [ BlogService ]  ← @MockBean で偽物に差し替え
```

- `@SpringBootTest` で **Spring の DI コンテナを起動**（Controller は本物が動く）。
- `@AutoConfigureMockMvc` で **MockMvc**（HTTPを実際に送らず擬似実行する道具）を用意。
- `@MockBean` で **Service を偽物**に差し替え → DBに行かず、画面遷移とモデルだけを検証。

> 📝 learn 8-7 は「Controllerだけ起動する `@WebMvcTest` の方が速い」と推奨していますが、
> **実テストは `@SpringBootTest`（アプリ全体起動）**を採用しています。遅いが確実、という選択（[09](09_learnとの対応・発展トピック.md)）。

---

## 🔧 8-2. テストクラスの共通骨格

8クラスとも、ほぼ同じ形をしています（`BlogListControllerTest` を例に）：

```java
@SpringBootTest                  // ① アプリ全体を起動
@AutoConfigureMockMvc            // ② MockMvc を自動構成
public class BlogListControllerTest {

    @Autowired private MockMvc mockMvc;      // ③ 擬似HTTPクライアント
    @MockBean  private BlogService blogService;  // ④ Serviceを偽物に

    private MockHttpSession session;         // ⑤ ログイン済みを再現するセッション

    @BeforeEach
    public void prepareData() {              // ⑥ 各テスト前に共通準備
        UserEntity user = new UserEntity();
        user.setUserId(1L); user.setUserName("John"); ...
        session = new MockHttpSession();
        session.setAttribute("user", user);  // ★ログイン状態を作る
        when(blogService.findAllBlogPost(1L)).thenReturn(blogList);
    }
    ...
}
```

| 部位 | 役割 |
| --- | --- |
| `MockMvc` | `perform()` でリクエストを擬似送信し、`andExpect()` で結果検証 |
| `@MockBean` | Service を Mockito モック化（`when().thenReturn()` が使える） |
| `MockHttpSession` | `session.setAttribute("user", ...)` で**ログイン済み状態**を捏造 |
| `@BeforeEach` | 毎テスト前にユーザー・モックを準備（重複コードを集約） |

### なぜセッションを手で作るのか

実コードのコントローラーは `session.getAttribute("user")` で動きます（[06](06_セッション認証と画像アップロード.md)）。
テストでもこれが無いと NPE になるため、`MockHttpSession` にユーザーを入れ、
リクエスト時に `.session(session)` で**付与**して「ログイン済み」を再現します。

---

## 🅰️ 8-3. MockMvc の読み方（perform → andExpect）

```java
mockMvc.perform(get("/user/blog/list").session(session))   // リクエスト組み立て＋実行
    .andExpect(status().isOk())                            // HTTP 200 か
    .andExpect(view().name("blog-list.html"))              // 返すテンプレート名
    .andExpect(model().attributeExists("userName", "blogList"))  // モデルに属性があるか
    .andExpect(model().attribute("userName", "John"));     // 属性の値まで
```

### 主な検証マッチャ

| マッチャ | 検証内容 |
| --- | --- |
| `status().isOk()` | HTTPステータス 200 |
| `status().is3xxRedirection()` | 3xx（リダイレクト） |
| `view().name("xxx.html")` | コントローラーが返したビュー名 |
| `redirectedUrl("/user/login")` | リダイレクト先URL |
| `model().attributeExists("a","b")` | モデルにその属性が存在 |
| `model().attribute("a", 値)` | モデル属性の値が一致 |

### リクエストの作り方

| 種類 | 書き方 |
| --- | --- |
| GET | `get("/user/blog/list")` |
| GET（パス変数） | `get("/user/blog/edit/{blogId}", 1L)` |
| POST（フォーム） | `post("/user/blog/update").param("blogId","1")` |
| POST（ファイル） | `multipart("/user/blog/register/process").file(mockFile).param(...)` |
| セッション付与 | `.session(session)` |

---

## 🗂 8-4. 8テストクラスの地図

| テストクラス | 件数 | 主な検証 |
| --- | --- | --- |
| [UserLoginControllerTest](../src/test/java/blog/ex/controller/UserLoginControllerTest.java) | 6 | 画面表示・ログイン成功(list遷移)・失敗3パターン・初期セッション空 |
| [UserRegisterControllerTest](../src/test/java/blog/ex/controller/UserRegisterControllerTest.java) | 3 | 画面表示・登録成功・重複でも login 遷移 |
| [BlogListControllerTest](../src/test/java/blog/ex/controller/BlogListControllerTest.java) | 3 | 表示・blogList内容一致・Service呼び出し回数 |
| [BlogRegisterControllerTest](../src/test/java/blog/ex/controller/BlogRegisterControllerTest.java) | 3 | 表示・登録成功(fix)・失敗(入力画面戻り)＋multipart |
| [BlogEditControllerTest](../src/test/java/blog/ex/controller/BlogEditControllerTest.java) | 4 | 表示(記事あり/なし)・更新成功/失敗 |
| [BlogImageEditControllerTest](../src/test/java/blog/ex/controller/BlogImageEditControllerTest.java) | 4 | 表示(記事あり/なし)・画像更新成功/失敗 |
| [BlogDeleteControllerTest](../src/test/java/blog/ex/controller/BlogDeleteControllerTest.java) | 5 | 一覧・詳細(あり/なし)・削除成功/失敗 |
| [LogoutControllerTest](../src/test/java/blog/ex/controller/LogoutControllerTest.java) | 2 | リダイレクト・セッション無効化 |

合計 **30ケース**。Service編の17と合わせて全47ケース。

---

## 🔍 8-5. ケース深掘り

### ① ログイン成功（UserLoginControllerTest No.2）

```java
when(userService.loginAccount(eq("ake@test.com"), eq("1234abcd"))).thenReturn(userEntity); // @BeforeEachで設定
...
RequestBuilder request = post("/user/login/process")
        .param("email", "ake@test.com").param("password", "1234abcd");
MvcResult result = mockMvc.perform(request)
        .andExpect(redirectedUrl("/user/blog/list"))   // ★成功で一覧へ
        .andReturn();
HttpSession session = result.getRequest().getSession();
session.getAttribute("user");   // ※参照のみ（assertは無い）
```

- `@BeforeEach` で「正しい組合せ→UserEntity / 誤り→null」を**4通り仕込む**のがミソ。
- 成功は `redirectedUrl("/user/blog/list")`、失敗系(No.3〜5)は `/user/login` ＋ `assertNull(user属性)`。

### ② 登録は重複でも同じ遷移（UserRegisterControllerTest No.3）

```java
when(userService.createAccount(eq("John"), eq("john@test.com"), eq("existingPassword"))).thenReturn(false);
...
mockMvc.perform(post("/user/register/process").param(...))
       .andExpect(redirectedUrl("/user/login"));   // falseでもloginへ
verify(userService, times(1)).createAccount(eq("John"), eq("john@test.com"), eq("existingPassword"));
```

- コントローラーが戻り値を捨てている（[05](05_Controller解説.md) 5-3）ので、**成功も重複も同じ `/user/login`**。
- テストはこの「現状の挙動」を `redirectedUrl("/user/login")` として追認している。

### ③ ファイルアップロード（BlogRegisterControllerTest No.2）

```java
String orgImage = "test-image.jpg";
String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-").format(new Date()) + orgImage; // ★同じ命名を再現
MockMultipartFile blogImage = new MockMultipartFile("blogImage", orgImage, "image/jpeg", new byte[0]);

mockMvc.perform(multipart("/user/blog/register/process")
        .file(blogImage)
        .param("blogTitle", "Test Blog").param("registerDate", "2023-06-01")
        .param("category", "Test Category").param("blogDetail", "Test Blog Detail")
        .session(session))
        .andExpect(view().name("blog-register-fix.html"));

verify(blogService, times(1)).createBlogPost(
        eq("Test Blog"), eq(LocalDate.parse("2023-06-01")), eq(fileName), ...);
```

読みどころ：

- `new byte[0]`（**空ファイル**）なので、実コードの `Files.copy` が走っても無害なファイルができるだけ。
- テスト側で **コントローラーと同じ `SimpleDateFormat` でファイル名を再現**し、
  `verify(...).createBlogPost(... eq(fileName) ...)` で「正しい命名で Service に渡したか」を検証。
  → 同じ秒内に実行される前提の、巧妙な突き合わせ。

### ④ `redirecr:` タイプミスの追認（BlogEditControllerTest No.2）

```java
when(blogService.getBlogPost(1L)).thenReturn(null);   // 記事なし
mockMvc.perform(get("/user/blog/edit/{blogId}", 1L).session(session))
       .andExpect(status().isOk())                                  // ← リダイレクトされず200
       .andExpect(view().name("redirecr:/user/blog/list"));         // ← バグった文字列をそのまま検証
```

- 本来 `redirect:` なら 3xx になるはず。だが `redirecr:`（タイプミス）は **ビュー名として扱われる**ため 200。
- テストコメントにも「実装は redirect スペル誤りのためビュー解決される」と明記。
- 👉 **テストは「あるべき姿」ではなく「現在の実装」を固定**している。
  もしタイプミスを直すなら、このテストの期待値も `status().is3xxRedirection()` / `redirectedUrl(...)` に直す必要がある。
- 同じ追認が `BlogImageEditControllerTest` No.2、`BlogDeleteControllerTest` No.3 にもある。

### ⑤ 画像更新の失敗時、再取得まで検証（BlogImageEditControllerTest No.4）

```java
when(blogService.editBlogImage(anyLong(), anyString(), anyLong())).thenReturn(false);
when(blogService.getBlogPost(1L)).thenReturn(new BlogEntity());   // 失敗分岐で再取得される
...
.andExpect(view().name("blog-img-edit.html"))
.andExpect(model().attributeExists("blogList", "editImageMessage"));
verify(blogService, times(1)).editBlogImage(eq(1L), eq(fileName), eq(1L));
verify(blogService, times(1)).getBlogPost(1L);   // ★失敗時だけ呼ばれる再取得を検証
```

- コントローラーの失敗分岐（記事を再取得して画面に戻す → [05](05_Controller解説.md) 5-8）を、
  `getBlogPost` の `verify` で **正確に追跡**している。

### ⑥ ログアウトでセッション無効化（LogoutControllerTest No.2）

```java
MockHttpSession loginSession = new MockHttpSession();
loginSession.setAttribute("user", new UserEntity(1L, "Akemi", "ake@test.com", "1234abcd", LocalDateTime.now()));
MvcResult result = mockMvc.perform(get("/user/blog/logout").session(loginSession))
        .andExpect(redirectedUrl("/user/login")).andReturn();
HttpSession session = result.getRequest().getSession(false);   // 既存セッションを再生成しない
assertNull(session == null ? null : session.getAttribute("user"));  // 無効化を確認
```

- `getSession(false)` は「**無ければ作らない**」。`invalidate()` 後なので null か、user属性が消えている。
- 三項演算子で null 安全に取り出し、`assertNull` で「ログアウトできた」ことを確認。

---

## 🧠 8-6. Service編テストとの違い（早見表）

| 観点 | Service編（07） | Controller編（この章） |
| --- | --- | --- |
| 起動 | 何も起動しない（素のJUnit） | `@SpringBootTest`（アプリ全体） |
| モック化 | `@Mock`（DAO） | `@MockBean`（Service） |
| 入力 | メソッド直接呼び出し | `MockMvc` で擬似HTTP |
| 検証対象 | 戻り値・DAO呼び出し | ビュー名・モデル・リダイレクト・ステータス |
| 速度 | 速い | 遅い（毎回コンテキスト起動） |

👉 **「下の層は素のMockito、上の層はMockMvc」** という使い分けが、このリポジトリのテスト方針。

---

## ❌ 8-7. 気をつけポイント

| 事実 | 注意点 |
| --- | --- |
| `redirecr:` を追認 | テストが現状固定。バグ修正時はテストも直す必要 |
| `@SpringBootTest` を全テストに使用 | 数が増えると遅くなる（learn は `@WebMvcTest` 推奨） |
| `@MockBean` は将来非推奨 | Spring Boot 新版では `@MockitoBean` へ（[09](09_learnとの対応・発展トピック.md)） |
| ファイル名検証が時刻依存 | 同一秒前提。理論上は秒またぎでズレる可能性 |
| 実 `Files.copy` が走る | 空バイトだが `static/blog-img/` に実ファイルが残ることがある |

---

## ✅ 8-8. まとめ

- Controller テストは **本物のSpring起動＋Serviceモック**で、画面遷移を検証。
- `mockMvc.perform(リクエスト).andExpect(検証)` が基本形。
- `MockHttpSession` でログイン状態を再現、`multipart` でファイルを添付。
- 30ケースで全コントローラーの表示・成功・失敗を網羅。
- `redirecr:` タイプミスを含む **現状の挙動をそのまま固定**しているのが重要な特徴。

---

➡️ 最後は [09_learnとの対応・発展トピック](09_learnとの対応・発展トピック.md)。learn の理想形と実コードの差を総ざらいします。
