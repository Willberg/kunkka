package fun.johntaylor.kunkka.entity.encrypt.user;

import fun.johntaylor.kunkka.utils.encrypt.EncryptUtil;
import lombok.Data;

/**
 * @Author John
 * @Description 脱密后的User
 * @Date 2020/7/3 1:08 PM
 **/
@Data
public class EncryptUser {
	private Long id;

	private String userName;

	private String phoneNumber;

	private String email;

	private Long createTime;

	private Long updateTime;

	private Integer roleId;

	private Integer status;

	public static final Integer R_USER = 1;
	public static final Integer R_ADMIN = 2;

	public static final Integer S_DEL = 0;
	public static final Integer S_NORMAL = 1;

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = EncryptUtil.encryptPhoneNumber(phoneNumber);
	}

	public void setEmail(String email) {
		this.email = EncryptUtil.encryptEmail(email);
	}
}
