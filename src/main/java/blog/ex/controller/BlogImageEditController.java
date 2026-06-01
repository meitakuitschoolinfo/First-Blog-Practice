package blog.ex.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import blog.ex.model.entity.BlogEntity;
import blog.ex.model.entity.UserEntity;
import blog.ex.service.BlogService;
import jakarta.servlet.http.HttpSession;

/**
 * ブログ記事の画像編集機能を担当するコントローラーです。
 * 画像編集画面の表示と、画像ファイルの差し替え（アップロード・更新）を行います。
 */
@RequestMapping("/user/blog")
@Controller
public class BlogImageEditController {

	/** ブログに関するビジネスロジックを提供するサービス。 */
	@Autowired
	private BlogService blogService;

	/** ログインユーザー情報を保持するセッション。 */
	@Autowired
	private HttpSession session;

	/**
	 * ブログ画像編集画面を表示します。
	 * 指定された blogId の記事が存在しない場合は一覧へリダイレクトします。
	 *
	 * @param blogId 編集対象のブログID
	 * @param model  ビューに渡すデータを格納するモデル
	 * @return 記事が存在する場合:blog-img-edit.html / 存在しない場合:一覧へリダイレクト
	 */
	@GetMapping("/image/edit/{blogId}")
	public String getBlogEditImagePage(@PathVariable Long blogId, Model model) {
		UserEntity userList = (UserEntity) session.getAttribute("user");
		String userName = userList.getUserName();
		model.addAttribute("userName", userName);
		BlogEntity blogList = blogService.getBlogPost(blogId);
		if (blogList == null) {
			return "redirecr:/user/blog/list";
		} else {
			model.addAttribute("blogList", blogList);
			model.addAttribute("editImageMessage", "画像編集");
			return "blog-img-edit.html";
		}
	}

	/**
	 * ブログ記事の画像を更新します。
	 * 新しい画像ファイルをサーバーへ保存し、記事の画像名を更新します。
	 *
	 * @param blogImage 新しい画像イメージ
	 * @param blogId    ブログID
	 * @param model     ビューに渡すデータを格納するモデル
	 * @return 成功時:blog-edit-fix.html / 失敗時:blog-img-edit.html
	 */
	@PostMapping("/image/update")
	public String blogImgUpdate(
			@RequestParam MultipartFile blogImage,
			@RequestParam Long blogId, Model model) {
		UserEntity userList = (UserEntity) session.getAttribute("user");
		Long userId = userList.getUserId();
		String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-").format(new Date()) + blogImage.getOriginalFilename();
		try {
			Files.copy(blogImage.getInputStream(), Path.of("src/main/resources/static/blog-img/" + fileName));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (blogService.editBlogImage(blogId, fileName, userId)) {
			return "blog-edit-fix.html";
		} else {
			BlogEntity blogList = blogService.getBlogPost(blogId);
			model.addAttribute("blogList", blogList);
			model.addAttribute("editImageMessage", "更新失敗です");
			return "blog-img-edit.html";
		}
	}
}
