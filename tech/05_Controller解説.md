# 📘 05. Controller 解説

― HTTP・画面・セッションをさばく「玄関口」 ―

> 対応する learn：第5章（Form）／第7章_1（Controller）
> 解説対象：[controller/](../src/main/java/blog/ex/controller/) 配下の8クラス

---

## 🎯 5-0. この章のゴール

- `@Controller` / `@RequestMapping` / `@GetMapping` / `@PostMapping` の役割を説明できる
- `@RequestParam` / `@PathVariable` / `Model` / `HttpSession` の使い分けを言える
- 8つのコントローラーが **どのURLで何を返すか** を一覧で把握できる
- 「Form クラスを作らず `@RequestParam` で受ける」という実装方針を理解する
- 実コードに潜む `redirecr:` タイプミスの影響を説明できる

---

## 🧭 5-1. 8コントローラーの全体マップ

機能ごとに **1クラス1責務** で分割されています。

| コントローラー | @RequestMapping | 主なエンドポイント | 返すビュー |
| --- | --- | --- | --- |
| [UserRegisterController](../src/main/java/blog/ex/controller/UserRegisterController.java) | `/user` | `GET /register`, `POST /register/process` | register.html → redirect login |
| [UserLoginController](../src/main/java/blog/ex/controller/UserLoginController.java) | `/user` | `GET /login`, `POST /login/process` | login.html → redirect list |
| [BlogListController](../src/main/java/blog/ex/controller/BlogListController.java) | `/user/blog` | `GET /list` | blog-list.html |
| [BlogRegisterController](../src/main/java/blog/ex/controller/BlogRegisterController.java) | `/user/blog` | `GET /register`, `POST /register/process` | blog-register(-fix).html |
| [BlogEditController](../src/main/java/blog/ex/controller/BlogEditController.java) | `/user/blog` | `GET /edit/{blogId}`, `POST /update` | blog-edit(-fix).html |
| [BlogImageEditController](../src/main/java/blog/ex/controller/BlogImageEditController.java) | `/user/blog` | `GET /image/edit/{blogId}`, `POST /image/update` | blog-img-edit.html |
| [BlogDeleteController](../src/main/java/blog/ex/controller/BlogDeleteController.java) | `/user/blog` | `GET /delete/list`, `GET /delete/detail/{blogId}`, `POST /delete` | blog-delete(-detail/-fix).html |
| [LogoutController](../src/main/java/blog/ex/controller/LogoutController.java) | `/user/blog` | `GET /logout` | redirect login |

> 💡 `@RequestMapping` はクラスに付く **共通プレフィックス**。
> 各メソッドの `@GetMapping("/list")` はこれと連結され、最終URLは `/user/blog/list` になる。

---

## 🧱 5-2. 共通の「型」を1つ覚える

8クラスは細部が違うだけで、**骨格は同じ**です。まず1つのテンプレを頭に入れます。

```java
@RequestMapping("/user/blog")          // ① URL の共通接頭辞
@Controller                            // ② DIコンテナに登録（画面を返すコントローラー）
public class XxxController {

    @Autowired private BlogService blogService;   // ③ Service を注入
    @Autowired private HttpSession session;       // ④ ログインユーザー保持

    @GetMapping("/xxx")                 // ⑤ 画面表示（GET）
    public String getXxxPage(Model model) {
        UserEntity userList = (UserEntity) session.getAttribute("user");  // ⑥ ログインユーザー取得
        ...
        model.addAttribute("...", ...); // ⑦ 画面に渡すデータ
        return "xxx.html";              // ⑧ テンプレート名
    }

    @PostMapping("/xxx/process")        // ⑨ 入力処理（POST）
    public String doXxx(@RequestParam ... , Model model) {
        if (blogService.xxx(...)) return "success.html";   // ⑩ 成功
        else { model.addAttribute("message", "失敗"); return "form.html"; } // ⑪ 失敗
    }
}
```

| 部位 | 役割 |
| --- | --- |
| `@Controller` | **HTMLを返す**コントローラー（`@RestController` ではない＝JSONでない） |
| `Model` | ビュー（Thymeleaf）に渡すデータ袋。`addAttribute(名前, 値)` |
| `@RequestParam` | フォームのフィールド（`name=...`）を引数で受け取る |
| `@PathVariable` | URL内の `{blogId}` を引数で受け取る |
| 戻り値 `"xxx.html"` | テンプレート名（forward）。`"redirect:/..."` ならリダイレクト |
| `HttpSession session` | ログイン中の `UserEntity` を `"user"` 属性で保持 |

> 📌 learn 第5章は専用の **Form クラス**（画面入力1個を表すクラス）を作りますが、
> **実コードに Form クラスはありません**。すべて `@RequestParam` で個別に受け取ります（[09](09_learnとの対応・発展トピック.md)）。

---

## 👤 5-3. ユーザー登録 ― UserRegisterController

[UserRegisterController.java](../src/main/java/blog/ex/controller/UserRegisterController.java)

```java
@RequestMapping("/user")
@Controller
public class UserRegisterController {
    @Autowired private UserService userService;

    @GetMapping("/register")
    public String getUserRegisterPage() { return "register.html"; }   // 画面表示だけ

    @PostMapping("/register/process")
    public String register(@RequestParam String userName,
                           @RequestParam String email,
                           @RequestParam String password) {
        userService.createAccount(userName, email, password);
        return "redirect:/user/login";    // 成否に関わらずログイン画面へ
    }
}
```

読みどころ：

- 登録結果（`createAccount` の `true/false`）を **使っていない**。
  重複でも成功でも **必ず `redirect:/user/login`**。
  → 画面上は「登録できたかどうか」をユーザーに伝えない実装。
- `redirect:` はブラウザに「このURLへGETし直して」と返す（PRGパターン）。
- 対応テスト：`UserRegisterControllerTest`（No.2成功 / No.3重複でも同じ遷移）（[08](08_単体テスト解説_Controller編.md)）。

---

## 🔐 5-4. ログイン ― UserLoginController

[UserLoginController.java](../src/main/java/blog/ex/controller/UserLoginController.java)。**セッション認証の中心**。

```java
@PostMapping("/login/process")
public String login(@RequestParam String email, @RequestParam String password) {
    UserEntity userList = userService.loginAccount(email, password);
    if(userList == null) {
        return "redirect:/user/login";          // 失敗 → ログイン画面に戻す
    } else {
        session.setAttribute("user", userList); // ★成功 → セッションにユーザーを格納
        return "redirect:/user/blog/list";      // 一覧へ
    }
}
```

- ログイン成功時に **`session.setAttribute("user", userList)`**。
  これ以降、他のコントローラーは `session.getAttribute("user")` でログインユーザーを取り出せる。
- これが **このアプリの「ログイン状態」の正体**。Spring Security は使っていない（[06](06_セッション認証と画像アップロード.md)）。
- 対応テスト：`UserLoginControllerTest`（成功でlist遷移＋セッション、失敗でlogin遷移＋user属性null）。

---

## 📋 5-5. 一覧表示 ― BlogListController

[BlogListController.java](../src/main/java/blog/ex/controller/BlogListController.java)

```java
@GetMapping("/list")
public String getBlogListPage(Model model) {
    UserEntity userList = (UserEntity) session.getAttribute("user");
    Long userId = userList.getUserId();
    String userName = userList.getUserName();
    List<BlogEntity> blogList = blogService.findAllBlogPost(userId);
    model.addAttribute("userName", userName);
    model.addAttribute("blogList", blogList);
    return "blog-list.html";
}
```

- セッションから `userId` を取り出し、**そのユーザーの記事だけ**を取得して表示。
- `userName` と `blogList` を Model に詰め、`blog-list.html` がそれを描画。
- ⚠️ セッションに `"user"` が無い（未ログイン）状態で来ると、`userList` が null で
  `getUserId()` の行で **NullPointerException**。
  → このアプリは「ログイン後にしか来ない」前提で、未ログインガードを置いていない。
- 対応テスト：`BlogListControllerTest`（ビュー名・属性・Service呼び出し回数）。

---

## ✍️ 5-6. 新規登録 ― BlogRegisterController（画像保存あり）

[BlogRegisterController.java](../src/main/java/blog/ex/controller/BlogRegisterController.java)

```java
@PostMapping("/register/process")
public String blogRegister(@RequestParam String blogTitle,
        @RequestParam LocalDate registerDate,
        @RequestParam String category,
        @RequestParam MultipartFile blogImage,   // ★ファイルアップロード
        @RequestParam String blogDetail, Model model) {
    UserEntity userList = (UserEntity) session.getAttribute("user");
    Long userId = userList.getUserId();
    // ① 保存ファイル名 = 日時プレフィックス + 元ファイル名
    String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-").format(new Date())
                      + blogImage.getOriginalFilename();
    try {
        // ② 画像を static/blog-img/ に物理保存
        Files.copy(blogImage.getInputStream(),
                   Path.of("src/main/resources/static/blog-img/" + fileName));
    } catch (Exception e) { e.printStackTrace(); }
    // ③ DB登録（成否で画面分岐）
    if (blogService.createBlogPost(blogTitle, registerDate, fileName, blogDetail, category, userId)) {
        return "blog-register-fix.html";          // 成功 → 完了画面
    } else {
        model.addAttribute("registerMessage", "既に登録済みです");
        return "blog-register.html";              // 失敗 → 入力画面に戻す
    }
}
```

ここがこのアプリで一番「やることが多い」メソッド。3ステップに分けて読む：

1. **ファイル名の生成** … `2026-06-22-14-30-05-焼肉.jpg` のように日時を前置。重複回避のため。
2. **物理保存** … `MultipartFile` を `Files.copy` で `static/blog-img/` に書き出し。
   保存パスが **実行時ディレクトリ相対**（`src/main/resources/...`）である点は要注意（[06](06_セッション認証と画像アップロード.md)）。
3. **DB登録** … `createBlogPost` に **生成済みファイル名（文字列）** を渡す。
   重複（同タイトル＋同日）なら `false` → エラーメッセージ付きで入力画面へ。

> 詳しい画像処理の仕組み・落とし穴は [06](06_セッション認証と画像アップロード.md) で深掘り。
> 対応テスト：`BlogRegisterControllerTest`（`multipart` でファイル添付、成功/失敗の画面分岐）。

---

## 🖊 5-7. 記事編集（テキスト）― BlogEditController

[BlogEditController.java](../src/main/java/blog/ex/controller/BlogEditController.java)

### 表示（GET /edit/{blogId}）

```java
@GetMapping("/edit/{blogId}")
public String getBlogEditPage(@PathVariable Long blogId, Model model) {
    ...
    BlogEntity blogList = blogService.getBlogPost(blogId);
    if (blogList == null) {
        return "redirecr:/user/blog/list";   // ★タイプミス（redirect ではない）
    } else {
        model.addAttribute("blogList", blogList);
        model.addAttribute("editMessage", "記事編集");
        return "blog-edit.html";
    }
}
```

- `@PathVariable Long blogId` … URL `/user/blog/edit/5` の `5` を受け取る。
- **🐛 `redirecr:` というタイプミス**（正しくは `redirect:`）。
  Spring は `redirecr:` を **リダイレクト指示と認識できず**、`redirecr:/user/blog/list` という
  **テンプレート名**として解決しようとする。
  → 実害：記事が無いとき一覧に飛ばず、存在しないビューを探して例外になる。
  → テストはこの**バグった現状をそのまま**検証している（`view().name("redirecr:/user/blog/list")`）（[08](08_単体テスト解説_Controller編.md)）。

### 更新（POST /update）

```java
@PostMapping("/update")
public String blogUpdate(@RequestParam String blogTitle, @RequestParam LocalDate registerDate,
        @RequestParam String category, @RequestParam String blogDetail,
        @RequestParam Long blogId, Model model) {
    Long userId = ((UserEntity) session.getAttribute("user")).getUserId();
    if (blogService.editBlogPost(blogTitle, registerDate, blogDetail, category, userId, blogId)) {
        return "blog-edit-fix.html";
    } else {
        model.addAttribute("registerMessage", "更新に失敗しました");
        return "blog-edit.html";
    }
}
```

- 画像（`blogImage`）は受け取らない＝テキスト項目だけ更新（画像は別コントローラー）。
- `editBlogPost` の引数順（title, date, **detail, category**, userId, blogId）は Service 定義に合わせる。

---

## 🖼 5-8. 画像編集 ― BlogImageEditController

[BlogImageEditController.java](../src/main/java/blog/ex/controller/BlogImageEditController.java)

- 表示 `GET /image/edit/{blogId}` … 編集対象を取得して `blog-img-edit.html`。
  ここにも **`redirecr:` タイプミス**あり（記事なし時）。
- 更新 `POST /image/update` … `BlogRegisterController` と **同じファイル保存処理**を行い、
  `editBlogImage(blogId, fileName, userId)` を呼ぶ。

```java
if (blogService.editBlogImage(blogId, fileName, userId)) {
    return "blog-edit-fix.html";              // 成功（※編集完了画面を共用）
} else {
    BlogEntity blogList = blogService.getBlogPost(blogId);  // 失敗時は再取得して
    model.addAttribute("blogList", blogList);               // 画面に記事を戻す
    model.addAttribute("editImageMessage", "更新失敗です");
    return "blog-img-edit.html";
}
```

- 成功時の遷移先が `blog-edit-fix.html`（テキスト編集と**同じ完了画面**を再利用）。
- 失敗時は **記事を再取得**して画面に戻す点が登録時と違う（編集画面は記事表示が必要なため）。
- 対応テスト：`BlogImageEditControllerTest`（成功/失敗、失敗時の `getBlogPost` 再呼び出しまで検証）。

---

## 🗑 5-9. 削除 ― BlogDeleteController

[BlogDeleteController.java](../src/main/java/blog/ex/controller/BlogDeleteController.java)。**3メソッド**でひとつの削除フローを作る。

```text
GET /delete/list            … 削除候補の一覧（blog-delete.html）
GET /delete/detail/{blogId} … 1件の詳細を確認（blog-delete-detail.html） ※redirecrタイプミスあり
POST /delete                … 実際に削除（成功 blog-delete-fix.html / 失敗 blog-delete.html）
```

```java
@PostMapping("/delete")
public String blogDelete(@RequestParam Long blogId, Model model) {
    if (blogService.deleteBlog(blogId)) {
        return "blog-delete-fix.html";
    } else {
        model.addAttribute("DeleteDetailMessage", "記事削除に失敗しました");
        return "blog-delete.html";
    }
}
```

- 「一覧 → 詳細確認 → 削除実行」の3段階で **誤削除を防ぐ** UI フロー。
- `detail` 表示の `redirecr:` タイプミスは Edit と同様。
- 対応テスト：`BlogDeleteControllerTest`（5ケース：一覧/詳細あり/詳細なし/削除成功/削除失敗）。

---

## 🚪 5-10. ログアウト ― LogoutController

[LogoutController.java](../src/main/java/blog/ex/controller/LogoutController.java)

```java
@GetMapping("/logout")
public String logout() {
    session.invalidate();             // セッションを破棄（user属性ごと消える）
    return "redirect:/user/login";
}
```

- `session.invalidate()` で **セッション全体を無効化** → ログイン状態が消える。
- ここは `redirect:` が **正しく**書かれている（タイプミスなし）。
- 対応テスト：`LogoutControllerTest`（3xxリダイレクト＋セッション無効化でuser属性null）。

---

## 🧩 5-11. コントローラー横断の共通パターン早見表

| パターン | 実装 | 出てくる場所 |
| --- | --- | --- |
| ログインユーザー取得 | `(UserEntity) session.getAttribute("user")` | List/Register/Edit/ImageEdit/Delete |
| 画面に名前表示 | `model.addAttribute("userName", ...)` | 各表示系 |
| 成功/失敗で画面分岐 | `if(service.xxx()) return 完了; else { addAttribute(message); return 入力; }` | Register/Edit/ImageEdit/Delete |
| 画像保存 | `SimpleDateFormat` + `Files.copy` | Register/ImageEdit |
| PRG（Post-Redirect-Get） | `return "redirect:/..."` | Login/Register/Logout |
| 記事なし時の遷移 | `return "redirecr:/user/blog/list"` 🐛 | Edit/ImageEdit/Delete詳細 |

---

## ❌ 5-12. 気をつけポイント

| 事実 | 注意点 |
| --- | --- |
| `redirecr:` タイプミス（3箇所） | リダイレクトされずビュー解決 → 例外。テストは現状を追認 |
| 未ログインガード無し | セッションに `user` が無いと NPE。ログイン前提の作り |
| Form クラス不使用 | `@RequestParam` を都度並べる。項目が増えると冗長 |
| バリデーション無し | `@Valid`/`BindingResult` を使っていない（[09](09_learnとの対応・発展トピック.md)） |
| ユーザー登録の結果を画面に反映しない | `createAccount` の戻り値を捨てている |
| 画像保存の例外を握りつぶし | `catch(Exception e){ printStackTrace(); }` で続行してしまう |

---

## ✅ 5-13. まとめ

- 8コントローラーは **機能ごとに分割**、骨格は共通（表示GET＋処理POST）。
- ログイン状態は **`HttpSession` の `"user"` 属性**で持ち回る。
- 入力は **Form クラスを作らず `@RequestParam`** で個別受け取り。
- 成功/失敗で画面を分ける「if 分岐」が全体に共通。
- `redirecr:` タイプミスなど **学習段階のクセ**が残っており、テストもその現状を検証している。

---

➡️ 次は [06_セッション認証と画像アップロード](06_セッション認証と画像アップロード.md)。横断機能を1段深く掘ります。
