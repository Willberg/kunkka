package fun.johntaylor.kunkka.utils.encryption;

import org.springframework.util.DigestUtils;

import java.util.UUID;

/**
 * @Author John
 * @Description 加密工作类
 * @Date 2020/6/24 3:16 PM
 **/
public final class EncryptionUtil {
	private static final String SALT = "NDIOjf398402#)$(Hdjfiosj";

	private EncryptionUtil() {

	}

	public static String md5WithSalt(String inputStr) {
		return DigestUtils.md5DigestAsHex((inputStr + SALT).getBytes());
	}

	public static String generateUniqueString() {
		return DigestUtils.md5DigestAsHex(String.format("%s%s", System.currentTimeMillis(), UUID.randomUUID().toString()).getBytes());
	}
}
