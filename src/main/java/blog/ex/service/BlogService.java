package blog.ex.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import blog.ex.model.dao.BlogDao;
import blog.ex.model.entity.BlogEntity;
/**Spring Frameworkの@Serviceアノテーションを使用して、
 * ブログ投稿に関するビジネスロジックを実装したBlogServiceクラスを定義*/
@Service
public class BlogService {
	/**
	 * @Autowired private BlogDao blogDao;
	 * userテーブルにアクセスして操作するため、UserDaoクインタフェース
	 * を使えるようにしておきます。
	 */
	@Autowired
	private BlogDao blogDao;
	/**findAllBlogPostメソッドは、引数としてuserIdを取ります。
	 * userIdがnullであれば、nullを返します。それ以外の場合は
	 * blogDao.findByUserId(userId)を呼び出して、指定されたuserIdに対応する
	 * すべてのブログ投稿を取得し、それらをBlogEntityオブジェクトのリストとして返します。**/
	/**
	 * @param userId ユーザーId
	 * @return
	 */
	public List<BlogEntity> findAllBlogPost(Long userId){
		/** userIdがnullであれば、nullを返します。**/
		if(userId == null) {
			return null;
			/**それ以外の場合は
			 * blogDao.findByUserId(userId)を呼び出して、指定されたuserIdに対応する
			 * すべてのブログ投稿を取得し、それらをBlogEntityオブジェクトのリストとして返します。**/
		}else {
			return blogDao.findByUserId(userId);
		}
	}
	/**このソースコードは、ブログ記事を作成するためのメソッド createBlogPostです。
	 * メソッドは、ブログ記事のタイトル、登録日、ファイル名、詳細などの情報を受け取ります。**/

	/**
	 * @param blogTitle　ブログのタイトル
	 * @param registerDate　登録日
	 * @param fileName　画像のファイル名
	 * @param blogDetail ブログの詳細文
	 * @param category　カテゴリー
	 * @param userId　ユーザーId
	 * @return
	 */
	public boolean createBlogPost(String blogTitle,LocalDate registerDate,String fileName,String blogDetail,String category,Long userId) {
		/**
		 * blogDao.findByBlogTitleAndRegisterDate(blogTitle, registerDate) によって、
		 * 既に同じタイトルと登録日のブログ記事が存在するかを検索します。**/
		BlogEntity blogList = blogDao.findByBlogTitleAndRegisterDate(blogTitle, registerDate);
		/**
		 * もし、存在しなければ、新しいブログ記事を作成して blogDao.save() によってデータベースに保存します**/
		if(blogList == null) {
			blogDao.save(new BlogEntity(blogTitle,registerDate,fileName,blogDetail,category,userId));
			/**新しいブログ記事が作成され、データベースに保存された場合は、true を返します。**/
			return true;
		}else {
			/**既に同じタイトルと登録日のブログ記事が存在した場合は、false を返します**/
			return false;
		}
	}
	/**getBlogPostメソッドは、与えられたblogIdに基づいて、blogDaoから該当する
	 * ブログエンティティを取得して返します。blogIdがnullである場合は、nullを返します**/
	public BlogEntity getBlogPost(Long blogId) {
		if(blogId == null) {
			return null;
		}else {
			return blogDao.findByBlogId(blogId);
		}
	}
	/**
	 * editBlogPostメソッド
	 * ブログ記事のタイトル、登録日、詳細などの情報を受け取り、指定されたblogIdに対応するブログ記事を更新します。
	 * blogDao.findByBlogId(blogId)によって、指定されたblogIdに対応するブログ記事を取得し、更新します。
	 * userIdがnullの場合は、falseを返します。更新に成功した場合は、trueを返します。**/
	public boolean editBlogPost(String blogTitle,LocalDate registerDate,String blogDetail,String category,Long userId,Long blogId) {
		BlogEntity blogList = blogDao.findByBlogId(blogId);
		if(userId == null) {
			return false;
		}else {
			blogList.setBlogId(blogId);
			blogList.setBlogTitle(blogTitle);
			blogList.setRegisterDate(registerDate);
			blogList.setCategory(category);
			blogList.setBlogDetail(blogDetail);
			blogList.setUserId(userId);
			blogDao.save(blogList);
			return true;
		}
	}
	/**
	 * public boolean editBlogImage(Long blogId, String fileName, Long userId): 
	 * メソッド名と引数を定義しています。ブログID、ファイル名、ユーザーIDを引数として受け取ります。戻り値はboolean型です。**/
	public boolean editBlogImage(Long blogId,String fileName,Long userId) {
		/**
		 * BlogEntity blogList = blogDao.findByBlogId(blogId);
		 *  引数で渡されたブログIDから、ブログ情報を取得します。ブログIDに該当するブログが存在しない場合はnullが返ります。**/
		BlogEntity blogList = blogDao.findByBlogId(blogId);
		/**
		 * if (fileName == null || blogList.getBlogImage().equals(fileName)) { ... }: 
		 * ファイル名がnullであるか、既に設定されている画像ファイル名と同じである場合は、処理を中断してfalseを返します。**/
		if(fileName == null || blogList.getBlogImage().equals(fileName)) {
			return false;
		}else {
			/**
			 * blogList.setBlogId(blogId);: ブログIDを設定します。
			 * blogList.setBlogImage(fileName);: 新しい画像ファイル名を設定します。
			 * blogList.setUserId(userId);: ユーザーIDを設定します。
			 * blogDao.save(blogList);: ブログ情報を更新して保存します。
			 * return true;: 画像の編集に成功した場合、trueを返します。**/
			blogList.setBlogId(blogId);
			blogList.setBlogImage(fileName);
			blogList.setUserId(userId);
			blogDao.save(blogList);
			return true;
		}
	}
	/**deleteByBlogIdメソッドを呼び出すことで、
	 * 指定されたIDのブログ記事を削除するdeleteBlogメソッドを定義しています。
	 * メソッドの戻り値の型はbooleanであり、メソッドの実行結果を呼び出し元に返します。
	 * このメソッドは、引数として渡されたblogIdがnullである場合はfalseを返します。
	 * そうでない場合は、BlogDaoインターフェースのdeleteByBlogIdメソッドを呼び出して、
	 * 削除処理を実行し、結果としてtrueを返します。**/
	public boolean deleteBlog(Long blogId) {
		if(blogId == null) {
			return false;
		}else {
			blogDao.deleteByBlogId(blogId);
			return true;
		}
	}
	
}
