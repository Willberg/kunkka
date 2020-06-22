package fun.johntaylor.kunkka.service.user;

import fun.johntaylor.kunkka.entity.user.User;
import fun.johntaylor.kunkka.utils.result.Result;
import reactor.core.publisher.Mono;

/**
 * @Author John
 * @Description 用户管理
 * @Date 2020/6/22 10:11 PM
 **/
public interface UserService {
	/**
	 * 注册
	 * @param user
	 * @return
	 */
	Result register(User user);

	/**
	 * 登录
	 * @param user
	 * @return
	 */
	Result login(User user);

	/**
	 * 退出
	 * @param user
	 * @return
	 */
	Result logout(User user);
}
