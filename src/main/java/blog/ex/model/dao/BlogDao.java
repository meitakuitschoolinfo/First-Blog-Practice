package blog.ex.model.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import blog.ex.model.entity.BlogEntity;
import jakarta.transaction.Transactional;
/**
 *このソースコードは、BlogDaoというインターフェースを定義しています。このインターフェースは、
 * JpaRepositoryというインターフェースを継承しています。
 * JpaRepositoryは、Spring Data JPAが提供する、
 * エンティティのCRUD操作を簡単に実装するためのインターフェースです.
 */
/**
 * 具体的には、BlogEntityを操作するために、Spring Data JPAのJpaRepositoryを継承しています。
 * これにより、データベースにアクセスするための基本的なCRUDメソッドが提供されます。**/
public interface BlogDao extends JpaRepository<BlogEntity, Long> {
	/**
	 * findByUserIdというメソッドを定義します。
	 * このメソッドは、指定されたuserIdに基づいて、データベース内のBlogEntityを
	 * 検索するために使用されます。このメソッドは、戻り値としてList<BlogEntity>を返します
	 * 。つまり、userIdに一致する複数のBlogEntityを取得することができます。**/
	List<BlogEntity> findByUserId(Long userId);
	/**
	 *このメソッドは、BlogEntityのオブジェクトを引数として受け取り、
	 * そのオブジェクトをデータベースに保存します。また、このメソッドの戻り値は、保存されたBlogEntityオブジェクトです。
	 * 具体的には、このメソッドを実行することで、渡されたBlogEntityオブジェクトがデータベースに保存されます。
	 * 保存されたBlogEntityオブジェクトは、その後の処理で使用することができます。**/
	BlogEntity save(BlogEntity blogEntity);
	/**
	 * このメソッドは、blogTitleとregisterDateを検索条件として使用して、BlogEntityオブジェクトを取得します。**/
	BlogEntity findByBlogTitleAndRegisterDate(String blogTitle,LocalDate registerDate);
	/**
	 * findByBlogIdというメソッドを定義します。
	 * このメソッドは、指定されたblogIdに基づいて、データベース内のBlogEntityを
	 * 検索するために使用されます。このメソッドは、戻り値としてList<BlogEntity>を返します.
	 * つまり、blogIdに一致する複数のBlogEntityを取得することができます。**/
	BlogEntity findByBlogId(Long blogId);
	/**
	 * @Transactionalアノテーションが付いたメソッドがあります。このアノテーションは、トランザクション管理を行うことを示しており、
	 * メソッドの実行中に例外が発生した場合は、トランザクションがロールバックされます。トランザクションは、
	 * 複数のデータベース操作をまとめ、一括で処理することができる機能です。このようにトランザクションを管理することで、
	 * データの整合性や安全性を確保することができます。**/
	@Transactional
	/**BlogEntityのIDに基づいて、該当するBlogEntityを削除するために使用されます。ただし、戻り値の型がList<BlogEntity?となっています。
	 * これは、削除されたエンティティを返すことで、呼び出し元で処理を行うことができます。**/
    List<BlogEntity> deleteByBlogId(Long blogId);
}
