package fun.johntaylor.kunkka.service.user;

import fun.johntaylor.kunkka.entity.encrypt.user.EncryptUser;
import fun.johntaylor.kunkka.entity.user.User;
import fun.johntaylor.kunkka.utils.result.Result;

/**
 * @Author John
 * @Description 用户管理
 * @Date 2020/6/22 10:11 PM
 **/
public interface UserService {
	/**
	 * 注册
	 * @param user
	 * @return Result<EncryptUser>
	 */
	Result<EncryptUser> register(User user);

	/**
	 * 登录
	 * @param user
	 * @return Result<EncryptUser>
	 */
	Result<EncryptUser> login(User user);

	/**
	 * 获取用户身份
	 * @param user
	 * @return Result<EncryptUser>
	 */
	Result<EncryptUser> getProfile(User user);

	/**
	 * 修改密码
	 * @param uid
	 * @param oldPassword
	 * @param newPassword
	 * @return Result<EncryptUser>
	 */
	Result<EncryptUser> changePassword(Long uid, String oldPassword, String newPassword);
}
