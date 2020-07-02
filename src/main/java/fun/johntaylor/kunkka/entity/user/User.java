package fun.johntaylor.kunkka.entity.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

/**
 * @Author John
 * @Description 用户表
 * @Date 2020/6/22 10:16 PM
 **/
@Data
public class User {
	private Long id;

	private String userName;

	private String password;

	@Pattern(regexp = "^[0-9]{11}$", message = "手机号不对")
	private String phoneNumber;

	@Email(message = "邮箱格式错误")
	private String email;

	private Long createTime;

	private Long updateTime;

	private Integer roleId;

	private Integer status;

	public static final Integer R_USER = 1;
	public static final Integer R_ADMIN = 2;

	public static final Integer S_DEL = 0;
	public static final Integer S_NORMAL = 1;
}
