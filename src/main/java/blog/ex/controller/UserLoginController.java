package blog.ex.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import blog.ex.model.entity.UserEntity;
import blog.ex.service.UserService;
import jakarta.servlet.http.HttpSession;
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
public class UserLoginController {
	/**
	 * @Autowiredアノテーションは、DIコンテナが自動的にUserServiceインスタンスを
	 * 注入するために使用されます。これにより、UserRegisterControllerクラスは、
	 * UserServiceクラスのメソッドを呼び出すことができ、ログインの処理を実行することができます。
	 **/
	@Autowired
	private UserService userService;
	@Autowired
	private HttpSession session;
	/**
	 * @GetMappingアノテーションは、HTTP GETリクエストに対するマッピングを設定するために使用されます。
	 * "/login"と指定されているので、このエンドポイントは"/user/login"でアクセス可能です。
	 * getUserLoginPage()メソッドは、"login.html"を返すことで、ログイン画面を表示します。**/
	@GetMapping("/login")
	public String getUserLoginPage() {
		return "login.html";
	}
	/**
	 * @PostMappingアノテーションは、HTTP POSTリクエストを処理するためのメソッドであることを示します。/login/processは、
	 * リクエストのURLの"/user"の下にある、このメソッドが処理するリクエストを指します。
	 * public String login(@RequestParam String email,@RequestParam String password)は、
	 * このメソッドがリクエストパラメータを受け取ることを示します。
	 * @RequestParamアノテーションは、メソッドの引数に対応するリクエストパラメータの値を受け取ることを示します。
	 * この場合、emailとpasswordがリクエストパラメータとして渡されます。**/
	@PostMapping("/login/process")
	public String login(@RequestParam String email,@RequestParam String password) {
		/**
		 * userService.loginAccount(email, password)は、UserServiceのloginAccountメソッドを呼び出して、
		 * 指定されたメールアドレスとパスワードでアカウントにログインできるかどうかを確認します。
		 * もしログインに成功した場合は、セッションにログイン情報を格納し、"redirect:/user/blog/list"にリダイレクトされ、
		 * それ以外の場合は"redirect:/user/login"にリダイレクトされる**/
		UserEntity userList = userService.loginAccount(email, password);
		if(userList == null) {
			return "redirect:/user/login";
		}else {
			session.setAttribute("user", userList);
			return "redirect:/user/blog/list";
		}
		/**
		 * つまりこのコードは、ログイン機能を担当しています。
		 * ユーザーがログインページ(/user/login)にアクセスした場合は、"login.html"を返し、ログインフォームを表示します。
		 * ユーザーがログインフォームを送信すると、login()メソッドが呼び出され、
		 * UserServiceのloginAccount()メソッドが呼び出され、アカウントの認証が行われます。
		 * 認証が成功した場合は、ブログページ(/user/blog/list)にリダイレクトします。
		 * 認証に失敗した場合は、ログインページ(/user/login)にリダイレクトします。 
		 * **/
	}
}
