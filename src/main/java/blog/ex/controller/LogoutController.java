package blog.ex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

/**
 * ログアウト機能を担当するコントローラーです。
 * セッションを破棄し、ログイン画面へリダイレクトします。
 */
@RequestMapping("/user/blog")
@Controller
public class LogoutController {

	/** ログインユーザー情報を保持するセッション。 */
	@Autowired
	private HttpSession session;

	/**
	 * ログアウト処理を行います。
	 * 現在のセッションを無効化し、ログイン画面へリダイレクトします。
	 *
	 * @return ログイン画面（/user/login）へのリダイレクト
	 */
	@GetMapping("/logout")
	public String logout() {
		session.invalidate();
		return "redirect:/user/login";
	}
}
