package fun.johntaylor.kunkka.entity.encrypt.user;

import lombok.Data;

import java.util.Optional;
import java.util.stream.IntStream;

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
		this.phoneNumber = Optional
				.ofNullable(phoneNumber)
				.map(p -> String.format("%s****%s", p.substring(0, 3), p.substring(6)))
				.orElse(null);
	}

	public void setEmail(String email) {
		if (Optional.ofNullable(email).isPresent()) {
			int idx = email.indexOf("@");
			String ev = email.substring(0, idx);
			int encryptLength = ev.length() / 2;
			StringBuilder sb = new StringBuilder();
			IntStream.range(0, encryptLength).forEach(v -> sb.append("*"));
			idx = encryptLength / 2 + sb.toString().length();
			this.email = String.format("%s%s%s", email.substring(0, encryptLength / 2), sb.toString(), email.substring(idx));
		}
	}
}
