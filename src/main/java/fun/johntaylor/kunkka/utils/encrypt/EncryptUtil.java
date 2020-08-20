package fun.johntaylor.kunkka.utils.encrypt;

import org.springframework.util.DigestUtils;

import java.util.*;
import java.util.stream.IntStream;

/**
 * @Author John
 * @Description 加密，解密，脱密工具
 * @Date 2020/8/20 11:01 AM
 **/
public final class EncryptUtil {
	private static final char[] UPPER_LETTERS = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
	private static final char[] LOWER_LETTERS = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
	private static final char[] SPECIAL_LETTERS = {'!', '@', '#', '$', '%', '^', '&', '*', '(', ')'};

	private EncryptUtil() {

	}

	/**
	 * 返回脱密后的手机号
	 * @param phoneNumber
	 * @return phoneNumber
	 */
	public static String encryptPhoneNumber(String phoneNumber) {
		return Optional
				.ofNullable(phoneNumber)
				.map(p -> String.format("%s****%s", p.substring(0, 3), p.substring(7)))
				.orElse(null);
	}

	/**
	 * 返回脱密后的email
	 * @param email
	 * @return email
	 */
	public static String encryptEmail(String email) {
		if (!Optional.ofNullable(email).isPresent()) {
			return null;
		}

		int idx = email.indexOf("@");
		String ev = email.substring(0, idx);
		int encryptLength = ev.length() / 2;
		StringBuilder sb = new StringBuilder();
		IntStream.range(0, encryptLength).forEach(v -> sb.append("*"));
		idx = encryptLength / 2 + sb.toString().length();
		return String.format("%s%s%s", email.substring(0, encryptLength / 2), sb.toString(), email.substring(idx));
	}

	/**
	 * 返回16位随机串
	 * @return 随机串
	 */
	public static String generateRandomString(boolean isSpecial) {
		int n = 16;
		Random r = new Random();
		List<Character> characterList = new ArrayList<>(n);
		for (int i = 0; i < n; i++) {
			if (i < 4) {
				characterList.add(UPPER_LETTERS[r.nextInt(26)]);
			} else if (i < 8) {
				char c = '0';
				c += r.nextInt(10);
				characterList.add(c);
			} else if (i < 12) {
				if (isSpecial) {
					characterList.add(SPECIAL_LETTERS[r.nextInt(10)]);
				} else {
					characterList.add(LOWER_LETTERS[r.nextInt(26)]);
				}
			} else {
				characterList.add(LOWER_LETTERS[r.nextInt(26)]);
			}
		}

		StringBuilder sb = new StringBuilder();
		while (characterList.size() > 0) {
			int idx = r.nextInt(characterList.size());
			sb.append(characterList.remove(idx));
		}

		return sb.toString();
	}

	/**
	 * 返回salt
	 * @return salt
	 */
	public static String genrateSalt() {
		return DigestUtils.md5DigestAsHex(String.format("%s%s", System.currentTimeMillis(), UUID.randomUUID().toString()).getBytes()).substring(16);
	}

	/**
	 * 通过salt加密txt
	 * @param txt
	 * @param salt
	 * @return encryptTxt
	 */
	public static String encrypt(String txt, String salt) {
		return DigestUtils.md5DigestAsHex((txt + salt).getBytes());
	}

	/**
	 * 通过salt加密password
	 * @param password
	 * @param salt
	 * @return encryptPassword
	 */
	public static String encryptPassword(String password, String salt) {
		String encryptTxt = encrypt(password, salt);
		StringBuilder sb = new StringBuilder();
		IntStream.range(0, encryptTxt.length())
				.forEach(i -> {
					if (i % 2 == 0) {
						String s = String.valueOf(encryptTxt.charAt(i));
						if (i % 3 == 0) {
							sb.append(s.toUpperCase());
						} else {
							sb.append(s);
						}
					}
				});

		return sb.toString();
	}
}
