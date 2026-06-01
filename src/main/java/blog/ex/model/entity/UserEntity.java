package blog.ex.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * このエンティティクラスは、usersというテーブルを表しています。テーブルのカラムは、以下の通りです。
 * user_id: プライマリーキーとして使用される、ユーザーIDを表すLong型のカラム。
 * user_name: ユーザー名を表す、文字列型のカラム。nullを許容しません。
 * email: メールアドレスを表す、文字列型のカラム。nullを許容しません。
 * password: パスワードを表す、文字列型のカラム。nullを許容しません。
 * register_date: 登録日時を表す、日時型のカラム。nullを許容します。
 * JPAを使用することで、このエンティティクラスはデータベースとのマッピングを簡単に行うことができます。
 * このエンティティクラスのインスタンスをデータベースに保存することができます。
 * さらに、JPAを使用することで、データベースからエンティティクラスのインスタンスを取得することもできます。
 */
/**
 * @Data Lombokのアノテーションで、クラスに対してGetter、 Setter、toString、 equals、hashCodeメソッドを
 *       自動生成します。
 */
@Data
/**
 * @NoArgsConstructor Lombokのアノテーションで、引数なしのデフォルトコンストラクタを自動生成します。
 */
@NoArgsConstructor
/**
 * @AllArgsConstructor Lombokのアノテーションで、全ての引数を持つコンストラクタを自動生成します。
 */
@AllArgsConstructor
/**
 * @RequiredArgsConstructor Lombokのアノテーションで、全ての引数を持つコンストラクタを自動生成します。
 */
@RequiredArgsConstructor
/**
 * @Entity JPAのアノテーションで、エンティティクラスであることを示します。
 */
@Entity
/**
 * @Table() JPAのアノテーションで、テーブル名を指定します。
 */
@Table(name = "users")
public class UserEntity {
	/**
	 * JPAのアノテーションで、プライマリーキーであることを示します
	 */
	@Id
	/**
	 * @Column(name="user_id") JPAのアノテーションで、フィールドとテーブルのカラムをマッピングします。
	 */
	@Column(name = "user_id")
	/**
	 * @GeneratedValue(strategy = GenerationType.AUTO)
	 *                          JPAのアノテーションで、プライマリーキーを自動生成する方法を指定します。
	 */
	@GeneratedValue(strategy = GenerationType.AUTO)
	/**
	 * フィールド変数で、エンティティのプライマリーキーとして使用されます。。
	 */
	private Long userId;

	/**
	 * @NonNull Lombokのアノテーションで、nullを許容しないことを示します。
	 */
	@NonNull
	@Column(name = "user_name")
	/**
	 * private String userName; フィールド変数で、ユーザー名を表します。
	 */
	private String userName;

	@NonNull
	@Column(name = "email")
	/**
	 * フィールド変数で、メールアドレスを表します。 private String email
	 */
	private String email;

	@NonNull
	@Column(name = "password")
	/**
	 * フィールド変数で、パスワードを表します。 private String password
	 */
	private String password;

	/**
	 * フィールド変数で、登録日時を表します。 private LocalDateTime registerDate
	 */
	@NonNull
	@Column(name = "register_date")
	private LocalDateTime registerDate;

}
