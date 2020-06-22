package fun.johntaylor.kunkka.service.user.impl;

import fun.johntaylor.kunkka.entity.user.User;
import fun.johntaylor.kunkka.repository.mybatis.user.UserMapper;
import fun.johntaylor.kunkka.service.user.UserService;
import fun.johntaylor.kunkka.utils.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author John
 * @Description 用户管理
 * @Date 2020/6/22 10:19 PM
 **/
@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserMapper userMapper;

	@Override
	public Result register(User user) {
		return Result.success(userMapper.insert(user));
	}

	@Override
	public Result login(User user) {
		return null;
	}

	@Override
	public Result logout(User user) {
		return null;
	}
}
