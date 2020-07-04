package fun.johntaylor.kunkka.service.user.impl;

import fun.johntaylor.kunkka.component.encryption.Encrypt;
import fun.johntaylor.kunkka.constant.cache.CacheDomain;
import fun.johntaylor.kunkka.entity.user.User;
import fun.johntaylor.kunkka.repository.mybatis.user.UserMapper;
import fun.johntaylor.kunkka.service.user.UserService;
import fun.johntaylor.kunkka.utils.cache.SimpleCacheUtil;
import fun.johntaylor.kunkka.utils.encryption.EncryptionUtil;
import fun.johntaylor.kunkka.utils.error.ErrorCode;
import fun.johntaylor.kunkka.utils.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * @Author John
 * @Description 用户管理
 * @Date 2020/6/22 10:19 PM
 **/
@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserMapper userMapper;

	@Autowired
	private Encrypt encrypt;

	@Override
	public Result register(User user) {
		User oldUser = userMapper.selectByUser(user);
		if (Objects.nonNull(oldUser)) {
			return Result.fail(ErrorCode.USER_EXISTS);
		}

		// 系统自动生成唯一用户名
		if (Objects.isNull(user.getUserName())) {
			user.setUserName(encrypt.generateUniqueString());
		}
		user.setPassword(encrypt.md5WithSalt(user.getPassword()));
		user.setCreateTime(System.currentTimeMillis());
		user.setUpdateTime(System.currentTimeMillis());
		user.setRoleId(User.R_USER);
		user.setStatus(User.S_NORMAL);
		int row = userMapper.insert(user);
		SimpleCacheUtil.set(CacheDomain.USER_CACHE, user.getId(), user);
		return Result.success(row);
	}

	@Override
	public Result login(User user) {
		User dbUser = userMapper.selectByUser(user);
		if (Objects.isNull(dbUser)) {
			return Result.failWithMessage("账号或密码错误");
		}

		String password = encrypt.md5WithSalt(user.getPassword());
		if (!dbUser.getPassword().equals(password)) {
			return Result.failWithMessage("账号或密码错误");
		}
		SimpleCacheUtil.set(CacheDomain.USER_CACHE, dbUser.getId(), dbUser);
		return Result.success(dbUser.getId());
	}

	@Override
	public Result logout(User user) {
		return null;
	}
}
