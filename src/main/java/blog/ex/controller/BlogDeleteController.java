package blog.ex.controller;

import java.util.List;

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
 * ブログ記事削除機能を担当するコントローラーです。
 * 削除候補一覧の表示、削除記事詳細の表示、記事の削除処理を行います。
 */
@RequestMapping("/user/blog")
@Controller
public class BlogDeleteController {

	/** ブログに関するビジネスロジックを提供するサービス。 */
	@Autowired
	private BlogService blogService;

	/** ログインユーザー情報を保持するセッション。 */
	@Autowired
	private HttpSession session;

	/**
	 * 削除候補のブログ一覧ページを表示します。
	 *
	 * @param model ビューに渡すデータを格納するモデル
	 * @return blog-delete.html
	 */
	@GetMapping("/delete/list")
	public String getBlogDeleteListPage(Model model) {
		UserEntity userList = (UserEntity) session.getAttribute("user");
		Long userId = userList.getUserId();
		String userName = userList.getUserName();
		List<BlogEntity> blogList = blogService.findAllBlogPost(userId);
		model.addAttribute("userName", userName);
		model.addAttribute("blogList", blogList);
		model.addAttribute("deleteMessage", "削除一覧");
		return "blog-delete.html";
	}

	/**
	 * 削除記事の詳細ページを表示します。
	 * 指定された blogId の記事が存在しない場合は一覧へリダイレクトします。
	 *
	 * @param blogId 削除対象のブログID
	 * @param model  ビューに渡すデータを格納するモデル
	 * @return 記事が存在する場合:blog-delete-detail.html / 存在しない場合:一覧へリダイレクト
	 */
	@GetMapping("/delete/detail/{blogId}")
	public String getBlogDeleteDetailPage(@PathVariable Long blogId, Model model) {
		UserEntity userList = (UserEntity) session.getAttribute("user");
		String userName = userList.getUserName();
		model.addAttribute("userName", userName);
		BlogEntity blogList = blogService.getBlogPost(blogId);
		if (blogList == null) {
			return "redirecr:/user/blog/list";
		} else {
			model.addAttribute("blogList", blogList);
			model.addAttribute("DeleteDetailMessage", "削除記事詳細");
			return "blog-delete-detail.html";
		}
	}

	/**
	 * ブログ記事を削除します。
	 *
	 * @param blogId 削除対象のブログID
	 * @param model  ビューに渡すデータを格納するモデル
	 * @return 成功時:blog-delete-fix.html / 失敗時:blog-delete.html
	 */
	@PostMapping("/delete")
	public String blogDelete(@RequestParam Long blogId, Model model) {
		if (blogService.deleteBlog(blogId)) {
			return "blog-delete-fix.html";
		} else {
			model.addAttribute("DeleteDetailMessage", "記事削除に失敗しました");
			return "blog-delete.html";
		}
	}
}
