package fun.johntaylor.kunkka.entity.user.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @Author John
 * @Description 更新密码
 * @Date 2020/9/1 6:22 PM
 **/
@Data
public class UpdatePassword {
	/**
	 * 旧密码
	 */
	@NotEmpty
	private String oldPassword;

	/**
	 * 新密码
	 */
	@NotEmpty
	private String newPassword;
}
