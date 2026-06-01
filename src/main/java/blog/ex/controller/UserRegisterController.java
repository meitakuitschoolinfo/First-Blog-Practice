package blog.ex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import blog.ex.service.UserService;
/**
 * @RequestMappingアノテーションは、HTTPリクエストに対するマッピングを設定するために使用されます。
 * "/user"と指定されているので、このコントローラーのすべてのエンドポイントは"/user"で始まります。
 *エンドポイント（Endpoint）は、Webサービスにおいて、外部からアクセス可能なURLやURIのことを指します。
 *つまり、クライアント（Webブラウザやスマートフォンアプリなど）がWebサービスにリクエストを送信するために使用するURLのことです。
 */
@RequestMapping("/user")
/**
 * @Controllerアノテーションは、Spring Frameworkがコンポーネントスキャン機能
 * によってこのクラスをDIコンテナに登録するために使用されます。
 * これにより、UserRegisterControllerクラスは、他のコンポーネントに注入されることができ,
 * システム内で一元的に管理されることができます。
 * コンポーネントスキャン機能は、Spring Frameworkにおいて、アノテーションで構成されたクラスを自動的に検出し、
 * DIコンテナに登録するための機能です
 * DI（Dependency Injection）コンテナは、Spring Frameworkが提供する機能の一つで、アプリケーションに必要なオブジェクトを管理する仕組みです。
 *通常、アプリケーションに必要なオブジェクトは、new演算子を使用してインスタンス化していますが、
 *DIコンテナを使用すると、オブジェクトの生成や注入を自動的に行うことができます。
 **/
@Controller
public class UserRegisterController {
	/**
	 * @Autowiredアノテーションは、DIコンテナが自動的にUserServiceインスタンスを
	 * 注入するために使用されます。これにより、UserRegisterControllerクラスは、
	 * UserServiceクラスのメソッドを呼び出すことができ、新規登録の処理を実行することができます。
	 **/
	@Autowired
	private UserService userService;
	/**
	 * @GetMappingアノテーションは、HTTP GETリクエストに対するマッピングを設定するために使用されます。
	 * "/register"と指定されているので、このエンドポイントは"/user/register"でアクセス可能です。
	 * getUserRegisterPage()メソッドは、"register.html"を返すことで、新規登録画面を表示します。**/
	@GetMapping("/register")
	public String getUserRegisterPage() {
		return "register.html";
	}

	/**
	 * @PostMappingアノテーションは、HTTP POSTリクエストに対するマッピングを設定するために使用されます。
	 * "/register/process"と指定されているので、このエンドポイントは"/user/register/process"でアクセス可能です。
	 * register()メソッドは、リクエストパラメーターからユーザー情報を取得し、
	 * UserServiceのcreateAccount()メソッドを呼び出して、新規登録処理を行います。処理が成功した場合、"/user/login"にリダイレクトします。**/
	/**
	 * @param userName ユーザー名
	 * @param email　メールアドレス
	 * @param password　パスワード
	 * @return　"redirect:/user/login"
	 */
	@PostMapping("/register/process")
	public String register(@RequestParam String userName,@RequestParam String email,@RequestParam String password) {
		userService.createAccount(userName,email,password);
		return "redirect:/user/login";
	}
}
