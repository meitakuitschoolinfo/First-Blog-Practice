package blog.ex.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import blog.ex.model.dao.UserDao;
import blog.ex.model.entity.UserEntity;

//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.util.Base64;

@Service
public class UserService {
	/**
	 * @Autowired private UserDao userDao; userテーブルにアクセスして操作するため、UserDaoクインタフェース
	 *            を使えるようにしておきます。
	 */
	@Autowired
	private UserDao userDao;

	/**
	 * @param userName ユーザー名
	 * @param email    メールアドレス
	 * @param password パスワード
	 * @return
	 */

	/**
	 * createAccountソッドは、ユーザーアカウントを作成するためのメソッドで、引数としてuserName、email、passwordを受け取ります。
	 */

	public boolean createAccount(String userName, String email, String password) {
		/**
		 * 現在の日時を取得して、registerDateに保存します
		 */
		LocalDateTime registerDate = LocalDateTime.now();
		/**
		 * UserDaoインターフェースのfindByEmailメソッドを使用して、 指定されたemailアドレスを持つUserEntityを検索し,
		 * 結果をuserEntityに格納する。
		 */
		UserEntity userEntity = userDao.findByEmail(email);
		/**
		 * UserEntityが見つからなかった場合、
		 */
		if (userEntity == null) {
			/**
			 * 新しいUserEntityオブジェクトを作成し、
			 * 引数として受け取ったuserName、email、password、registerDateを設定します。
			 * の後、UserDaoのsaveメソッドを呼び出して、 新しいUserEntityオブジェクトを保存します。そして、trueを返します。
			 **/
			userDao.save(new UserEntity(userName, email, password, registerDate));
			return true;
		} else {
			/** UserEntityが見つかった場合、falseを返します。 **/
			return false;
		}
	}

	/**
	 * loginAccountソッドは、ユーザーアカウントのログイン機能を実装するためのメソッドで、引数としてemailとpasswordを受け取ります
	 **/
	/**
	 * @param email    メールアドレス
	 * @param password パスワード
	 * @return
	 */
	public UserEntity loginAccount(String email, String password) {
		/**
		 * 引数として受け取ったemailとpasswordを使用して、
		 * UserDaoインタフェースのfindByEmailAndPasswordメソッドを呼び出して、 該当するUserEntityを検索します。
		 **/
		
		UserEntity userEntity = userDao.findByEmailAndPassword(email, password);
		/**
		 * 検索結果として取得したUserEntityがnullであるかどうかを確認し、
		 * nullである場合はログインに失敗したことを示すためにnullを返します。
		 **/
		if (userEntity == null) {
			return null;
		} else {
			/** 検索結果として取得したUserEntityがnullでない場合は、
			 * ログインに成功したことを示すために検索結果のUserEntityを返します **/
			return userEntity;
		}
		/**
		 * つまり、このメソッドは、ユーザーアカウントのログインが成功したかどうかを判断するために使用されます。
		 * UserDaoインタフェースのfindByEmailAndPasswordメソッドが、
		 * emailとpasswordを使用してデータベース内のユーザーエンティティを検索するために使用されます。
		 **/
	}
	
	/*以下のソースはロバートさんの暗号化の際に使用したソースになりますので、
	 * ローバートさんは以下のソースをご参考下さい。*/
	/*public boolean createAccount(String userName, String email, String password) {
        LocalDateTime registerDate = LocalDateTime.now();
        UserEntity userEntity = userDao.findByEmail(email);

        if (userEntity == null) {
            // パスワードをハッシュ化して保存
            UUID uuid = UUID.randomUUID();
            String saltStr = uuid.toString();
            String salt = saltStr.substring(0,10);
            String hashedPassword = hashPassword(password+salt);
            userDao.save(new UserEntity(userName, email, hashedPassword,salt, registerDate));
            return true;
        } else {
            return false;
        }
    }

    public UserEntity loginAccount(String email, String password) {
    
        UserEntity userEntity = userDao.findByEmail(email);
       
        String salt = userEntity.getSalt();
        String hashPassword = hashPassword(password+salt);
        if (userEntity == null) {
            return null;
        } else if(userEntity.getPassword.equals(hashPassword){
            return userEntity;
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not found", e);
        }
    }*/

}
