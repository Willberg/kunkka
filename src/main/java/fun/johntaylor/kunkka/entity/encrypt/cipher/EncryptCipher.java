package fun.johntaylor.kunkka.entity.encrypt.cipher;

import fun.johntaylor.kunkka.utils.encrypt.EncryptUtil;
import lombok.Data;

/**
 * @Author John
 * @Description 脱密密码器
 * @Date 2020/8/20 11:25 AM
 **/
@Data
public class EncryptCipher {
	private Long id;

	private Long uid;

	private String name;

	private String userName;

	private String password;

	private String email;

	private String phoneNumber;

	private String link;

	private Long createTime;

	private Long updateTime;

	private Integer status;

	public void setEmail(String email) {
		this.email = EncryptUtil.encryptEmail(email);
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = EncryptUtil.encryptPhoneNumber(phoneNumber);
	}
}
