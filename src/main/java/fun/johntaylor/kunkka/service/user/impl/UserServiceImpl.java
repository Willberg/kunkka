package fun.johntaylor.kunkka.service.user.impl;

import fun.johntaylor.kunkka.component.encryption.Encrypt;
import fun.johntaylor.kunkka.constant.cache.CacheDomain;
import fun.johntaylor.kunkka.entity.encrypt.user.EncryptUser;
import fun.johntaylor.kunkka.entity.user.User;
import fun.johntaylor.kunkka.repository.mybatis.user.UserMapper;
import fun.johntaylor.kunkka.service.user.UserService;
import fun.johntaylor.kunkka.utils.cache.SimpleCacheUtil;
import fun.johntaylor.kunkka.utils.error.ErrorCode;
import fun.johntaylor.kunkka.utils.general.CopyUtil;
import fun.johntaylor.kunkka.utils.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	public Result<EncryptUser> register(User user) {
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
		userMapper.insert(user);
		SimpleCacheUtil.set(CacheDomain.USER_CACHE, user.getId(), user);
		EncryptUser u = CopyUtil.copyWithSet(user, new EncryptUser());
		return Result.success(u);
	}

	@Override
	public Result<EncryptUser> login(User user) {
		User sUser = userMapper.selectByUser(user);
		if (Objects.isNull(sUser)) {
			return Result.failWithMessage("账号或密码错误");
		}

		String password = encrypt.md5WithSalt(user.getPassword());
		if (!sUser.getPassword().equals(password)) {
			return Result.failWithMessage("账号或密码错误");
		}
		SimpleCacheUtil.set(CacheDomain.USER_CACHE, sUser.getId(), sUser);
		EncryptUser u = CopyUtil.copyWithSet(sUser, new EncryptUser());
		return Result.success(u);
	}

	@Override
	public Result logout(User user) {
		return null;
	}
}
