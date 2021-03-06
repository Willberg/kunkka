package util;

import fun.johntaylor.kunkka.entity.encrypt.user.EncryptUser;
import fun.johntaylor.kunkka.entity.user.User;
import fun.johntaylor.kunkka.utils.cache.impl.SessionCache;
import fun.johntaylor.kunkka.utils.cache.impl.UserCache;
import fun.johntaylor.kunkka.utils.encrypt.EncryptUtil;
import fun.johntaylor.kunkka.utils.general.CopyUtil;
import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;
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
//		System.out.println(u.getId());

		Long uid = SessionCache.get(null, Long.class);
		uid = SessionCache.get(uid.toString(), Long.class);
		User user = UserCache.get(uid, User.class);
	}

	@Test
	public void testLong() {
		System.out.println(Long.parseLong("-1"));
		String s = null;
		System.out.println(Long.parseLong(Optional.ofNullable(s).orElse("")));
	}

	@Test
	public void testCache2() throws InterruptedException {
		SessionCache.set("test", 11L);
		Thread.sleep(35000);
		System.out.println(SessionCache.get("test", Long.class));

		SessionCache.set("test", 12L);
		Thread.sleep(25000);
		System.out.println(SessionCache.get("test", Long.class));
		SessionCache.set("test", 12L);
		Thread.sleep(25000);
		System.out.println(SessionCache.get("test", Long.class));
	}

	@Test
	public void testUUID() {
		System.out.println(UUID.randomUUID().toString());
	}

	@Test
	public void testMd5() {
		System.out.println(DigestUtils.md5DigestAsHex(UUID.randomUUID().toString().getBytes()));
	}

	@Test
	public void printCharacters() {
		StringBuilder upper = new StringBuilder();
		StringBuilder lower = new StringBuilder();
		IntStream.range(0, 26).forEach(i -> {
			System.out.print((new Random()).nextInt(26) + " ");
			char c = 'A';
			c += i;
			upper.append(String.format("'%s',", c));
			c = 'a';
			c += i;
			lower.append(String.format("'%s',", c));
		});
		System.out.println();
		System.out.println(upper.substring(0, upper.length() - 1));
		System.out.println(lower.substring(0, lower.length() - 1));

		System.out.println(EncryptUtil.generateRandomString(true));
		System.out.println(EncryptUtil.generateRandomString(false));
	}

}
