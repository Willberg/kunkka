package util;

import fun.johntaylor.kunkka.constant.cache.CacheDomain;
import fun.johntaylor.kunkka.entity.encrypt.user.EncryptUser;
import fun.johntaylor.kunkka.entity.user.User;
import fun.johntaylor.kunkka.utils.cache.BaseCache;
import fun.johntaylor.kunkka.utils.cache.impl.UserCache;
import fun.johntaylor.kunkka.utils.general.CopyUtil;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.stream.IntStream;

public class StringTest {

	@Test
	public void testEncryptUser() {
		EncryptUser u = new EncryptUser();
		u.setPhoneNumber("1234578901");
		System.out.println(u.getPhoneNumber());
		IntStream.range(1, 12).forEach(n -> {
			StringBuilder sb = new StringBuilder();
			for (int i = 1; i <= n; i++) {
				sb.append(i);
			}
			u.setEmail(sb.toString() + "@123.com");
			System.out.println(u.getEmail());
		});
	}

	@Test
	public void testCopyUtil() {
		User old = new User();
		old.setUserName("test");
		old.setPhoneNumber("12345678901");
		old.setPassword("123");
		old.setEmail("234@123.com");
		old.setRoleId(1);
		EncryptUser fresh = CopyUtil.copyWithSet(old, new EncryptUser());
		System.out.println(fresh.getUserName());
		System.out.println(fresh.getPhoneNumber());
		System.out.println(fresh.getEmail());
		System.out.println(fresh.getRoleId());
		System.out.println(fresh.getStatus());
	}

	@Test
	public void testCache() {
		UserCache.set(null, new User());
		User u = UserCache.get(null, User.class);
		System.out.println(u.getId());

		UserCache.set(null, null);
		u = UserCache.get(null, User.class);
		System.out.println(u.getId());
	}

	@Test
	public void testLong() {
		System.out.println(Long.parseLong("-1"));
		String s = null;
		System.out.println(Long.parseLong(Optional.ofNullable(s).orElse("")));
	}
}
