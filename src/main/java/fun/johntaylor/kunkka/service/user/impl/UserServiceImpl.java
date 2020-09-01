package fun.johntaylor.kunkka.service.user.impl;

import fun.johntaylor.kunkka.component.encryption.Encrypt;
import fun.johntaylor.kunkka.component.redis.cache.UserCache;
import fun.johntaylor.kunkka.entity.encrypt.user.EncryptUser;
import fun.johntaylor.kunkka.entity.user.User;
import fun.johntaylor.kunkka.repository.mybatis.user.UserMapper;
import fun.johntaylor.kunkka.service.user.UserService;
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

	@Autowired
	private UserCache userCache;

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
		userCache.set(user.getId(), user);
		EncryptUser u = CopyUtil.copyWithSet(user, new EncryptUser());
		return Result.success(u);
	}

	@Override
	public Result<EncryptUser> login(User user) {
		User sUser = userMapper.selectByUser(user);
		if (Objects.isNull(sUser)) {
			return Result.fail(ErrorCode.USER_AUTHENTICATION_ERROR);
		}

		String password = encrypt.md5WithSalt(user.getPassword());
		if (!sUser.getPassword().equals(password)) {
			return Result.fail(ErrorCode.USER_AUTHENTICATION_ERROR);
		}
		userCache.set(sUser.getId(), sUser);
		EncryptUser u = CopyUtil.copyWithSet(sUser, new EncryptUser());
		return Result.success(u);
	}

	@Override
	public Result<EncryptUser> getProfile(User user) {
		User u = userCache.get(user.getId(), User.class);
		EncryptUser encryptUser = CopyUtil.copyWithSet(u, new EncryptUser());
		return Result.success(encryptUser);
	}

	@Override
	public Result<EncryptUser> changePassword(Long uid, String oldPassword, String newPassword) {
		User old = userCache.get(uid, User.class);
		oldPassword = encrypt.md5WithSalt(oldPassword);
		if (!old.getPassword().equals(oldPassword)) {
			return Result.failWithMessage(ErrorCode.SYS_PARAMETER_ERROR, "密码错误");
		}

		String password = encrypt.md5WithSalt(newPassword);
		User user = CopyUtil.copyWithSet(old, new User());
		user.setPassword(password);
		user.setUpdateTime(System.currentTimeMillis());
		userMapper.update(user);
		userCache.set(uid, user);
		EncryptUser u = CopyUtil.copyWithSet(user, new EncryptUser());
		return Result.success(u);
	}
}
