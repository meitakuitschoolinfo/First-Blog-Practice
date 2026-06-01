package blog.ex.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import blog.ex.model.entity.BlogEntity;
import blog.ex.model.entity.UserEntity;
import blog.ex.service.BlogService;
import jakarta.servlet.http.HttpSession;

/**
 * ブログ記事編集機能（テキスト項目）を担当するコントローラーです。
 * 記事編集画面の表示と、タイトル・日付・カテゴリー・詳細の更新を行います。
 * 画像の編集は {@link BlogImageEditController} が担当します。
 */
@RequestMapping("/user/blog")
@Controller
public class BlogEditController {

	/** ブログに関するビジネスロジックを提供するサービス。 */
	@Autowired
	private BlogService blogService;

	/** ログインユーザー情報を保持するセッション。 */
	@Autowired
	private HttpSession session;

	/**
	 * ブログ記事編集画面を表示します。
	 * 指定された blogId の記事が存在しない場合は一覧へリダイレクトします。
	 *
	 * @param blogId 編集対象のブログID
	 * @param model  ビューに渡すデータを格納するモデル
	 * @return 記事が存在する場合:blog-edit.html / 存在しない場合:一覧へリダイレクト
	 */
	@GetMapping("/edit/{blogId}")
	public String getBlogEditPage(@PathVariable Long blogId, Model model) {
		UserEntity userList = (UserEntity) session.getAttribute("user");
		String userName = userList.getUserName();
		model.addAttribute("userName", userName);
		BlogEntity blogList = blogService.getBlogPost(blogId);
		if (blogList == null) {
			return "redirecr:/user/blog/list";
		} else {
			model.addAttribute("blogList", blogList);
			model.addAttribute("editMessage", "記事編集");
			return "blog-edit.html";
		}
	}

	/**
	 * ブログ記事を更新します。
	 *
	 * @param blogTitle    ブログタイトル
	 * @param registerDate 登録日
	 * @param category     カテゴリー
	 * @param blogDetail   ブログ詳細
	 * @param blogId       ブログID
	 * @param model        ビューに渡すデータを格納するモデル
	 * @return 成功時:blog-edit-fix.html / 失敗時:blog-edit.html
	 */
	@PostMapping("/update")
	public String blogUpdate(@RequestParam String blogTitle,
			@RequestParam LocalDate registerDate,
			@RequestParam String category,
			@RequestParam String blogDetail,
			@RequestParam Long blogId, Model model) {
		UserEntity userList = (UserEntity) session.getAttribute("user");
		Long userId = userList.getUserId();
		if (blogService.editBlogPost(blogTitle, registerDate, blogDetail, category, userId, blogId)) {
			return "blog-edit-fix.html";
		} else {
			model.addAttribute("registerMessage", "更新に失敗しました");
			return "blog-edit.html";
		}
	}
}
