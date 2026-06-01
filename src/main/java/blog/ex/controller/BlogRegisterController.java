package blog.ex.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import blog.ex.model.entity.UserEntity;
import blog.ex.service.BlogService;
import jakarta.servlet.http.HttpSession;

/**
 * ブログ新規登録機能を担当するコントローラーです。
 * 新規記事追加画面の表示と、入力された記事のデータベースへの登録を行います。
 */
@RequestMapping("/user/blog")
@Controller
public class BlogRegisterController {

	/** ブログに関するビジネスロジックを提供するサービス。 */
	@Autowired
	private BlogService blogService;

	/** ログインユーザー情報を保持するセッション。 */
	@Autowired
	private HttpSession session;

	/**
	 * ブログ新規登録画面を表示します。
	 *
	 * @param model ビューに渡すデータを格納するモデル
	 * @return blog-register.html
	 */
	@GetMapping("/register")
	public String getBlogRegisterPage(Model model) {
		UserEntity userList = (UserEntity) session.getAttribute("user");
		String userName = userList.getUserName();
		model.addAttribute("userName", userName);
		model.addAttribute("registerMessage", "新規記事追加");
		return "blog-register.html";
	}

	/**
	 * ブログ記事を登録します。
	 * 画像ファイルをサーバーへ保存し、記事情報をデータベースへ登録します。
	 * 登録に成功した場合は完了画面、失敗した場合は登録画面へ戻ります。
	 *
	 * @param blogTitle    ブログタイトル
	 * @param registerDate 登録日
	 * @param category     カテゴリー
	 * @param blogImage    画像イメージ
	 * @param blogDetail   ブログ詳細
	 * @param model        ビューに渡すデータを格納するモデル
	 * @return 成功時:blog-register-fix.html / 失敗時:blog-register.html
	 */
	@PostMapping("/register/process")
	public String blogRegister(@RequestParam String blogTitle,
			@RequestParam LocalDate registerDate,
			@RequestParam String category,
			@RequestParam MultipartFile blogImage,
			@RequestParam String blogDetail, Model model) {
		UserEntity userList = (UserEntity) session.getAttribute("user");
		Long userId = userList.getUserId();
		String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-").format(new Date()) + blogImage.getOriginalFilename();
		try {
			Files.copy(blogImage.getInputStream(), Path.of("src/main/resources/static/blog-img/" + fileName));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (blogService.createBlogPost(blogTitle, registerDate, fileName, blogDetail, category, userId)) {
			return "blog-register-fix.html";
		} else {
			model.addAttribute("registerMessage", "既に登録済みです");
			return "blog-register.html";
		}
	}
}
