package fun.johntaylor.kunkka.entity.cipher;

import fun.johntaylor.kunkka.entity.validation.Insert;
import fun.johntaylor.kunkka.entity.validation.Update;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @Author John
 * @Description 密码器
 * @Date 2020/8/20 10:45 AM
 **/
@Data
public class Cipher {
	/**
	 * Id
	 */
	@NotNull(message = "请设置ID", groups = {Update.class})
	private Long id;

	/**
	 * Uid
	 */
	private Long uid;

	/**
	 * 网站名
	 */
	@NotNull(message = "请设置网站名", groups = {Insert.class})
	private String name;

	/**
	 * 用户名
	 */
	private String userName;

	/**
	 * 密码明文
	 */
	private String password;

	/**
	 * salt
	 */
	private String salt;

	/**
	 * 邮箱
	 */
	@Email(message = "邮箱格式错误")
	private String email;

	/**
	 * 手机号
	 */
	@Pattern(regexp = "^[0-9]{11}$", message = "手机号不对")
	private String phoneNumber;

	/**
	 * 登录链接
	 */
	@NotNull(message = "请设置登录链接", groups = {Insert.class})
	private String link;

	/**
	 * 创建时间
	 */
	private Long createTime;

	/**
	 * 更新时间
	 */
	private Long updateTime;

	/**
	 * 状态 1--正常, 2--删除
	 */
	private Integer status;

	public static final Integer S_NORMAL = 1;
	public static final Integer S_DEL = 2;
}
