package blog.ex.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import blog.ex.model.entity.BlogEntity;
import blog.ex.model.entity.UserEntity;
import blog.ex.service.BlogService;
import jakarta.servlet.http.HttpSession;

/**
 * ブログ一覧表示機能を担当するコントローラーです。
 * ログイン中のユーザーが投稿したブログ記事の一覧を表示します。
 *
 * @RequestMapping("/user/blog") により、このコントローラーのエンドポイントは
 * すべて "/user/blog" で始まります。
 */
@RequestMapping("/user/blog")
@Controller
public class BlogListController {

	/** ブログに関するビジネスロジックを提供するサービス。 */
	@Autowired
	private BlogService blogService;

	/** ログインユーザー情報を保持するセッション。 */
	@Autowired
	private HttpSession session;

	/**
	 * ブログ一覧ページを表示します。
	 * セッションからログインユーザーを取得し、そのユーザーのブログ記事一覧を取得して
	 * blog-list.html に渡します。
	 *
	 * @param model ビューに渡すデータを格納するモデル
	 * @return blog-list.html
	 */
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
}
