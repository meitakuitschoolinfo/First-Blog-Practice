package blog.ex.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import blog.ex.model.entity.UserEntity;

/**
 *このソースコードは、UserDaoというインターフェースを定義しています。このインターフェースは、
 * JpaRepositoryというインターフェースを継承しています。
 * JpaRepositoryは、Spring Data JPAが提供する、
 * エンティティのCRUD操作を簡単に実装するためのインターフェースです.
 */
public interface UserDao extends JpaRepository<UserEntity, Long> {
	/**
	 *saveメソッドは、UserEntityを引数として受け取り、
	 *UserEntityを保存し、保存したUserEntityを返します
	 */
	UserEntity save(UserEntity userEntity);
	
	/**
	 findByEmailは、String型の引数を受け取り、
	 その引数と一致するemailを持つUserEntityを返します
	 */
	UserEntity findByEmail(String email);
	/**
	 * findByEmailAndPassword(String email,String password)：
	 * このメソッドは、String型のemailアドレスとpasswordを引数に取り、
	 * データベース内のUserEntityの中から、そのemailアドレスとpasswordと一致するものを検索し、
	 * それを返します。
	 ***/
	UserEntity findByEmailAndPassword(String email,String password);
}
