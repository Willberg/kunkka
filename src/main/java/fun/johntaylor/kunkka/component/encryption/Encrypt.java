package fun.johntaylor.kunkka.component.encryption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.util.UUID;

/**
 * @Author John
 * @Description 加密组件
 * @Date 2020/7/3 10:29 PM
 **/
@Component
public class Encrypt {
	@Value("${salt.password}")
	private String salt;

	public String md5WithSalt(String inputStr) {
		return DigestUtils.md5DigestAsHex((inputStr + salt).getBytes());
	}

	public String generateUniqueString() {
		return DigestUtils.md5DigestAsHex(String.format("%s%s", System.currentTimeMillis(), UUID.randomUUID().toString()).getBytes());
	}
}
