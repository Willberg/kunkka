package fun.johntaylor.kunkka.utils.error;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Author John
 * @Description 错误消息处理
 * @Date 2020/6/22 10:00 PM
 **/
@Slf4j
public final class MessageUtil {
	private static Map<String, Object> errorMessages = new HashMap<>();

	static {
		Yaml yml = new Yaml();
		try (InputStream reader = Thread.currentThread().getContextClassLoader().getResourceAsStream("error/messages.yml")) {
			Map map = yml.loadAs(reader, Map.class);
			initMap("", map);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	private static void initMap(String key, Map map) {
		Set<Map.Entry<String, Object>> set = map.entrySet();
		for (Map.Entry<String, Object> entry : set) {
			String newKey;
			if ("".equals(key)) {
				newKey = entry.getKey();
			} else {
				newKey = String.format("%s.%s", key, entry.getKey());
			}
			if (entry.getValue() instanceof Map) {
				initMap(newKey, (Map) entry.getValue());
			} else {
				errorMessages.put(newKey, entry.getValue());
			}
		}
	}

	private MessageUtil() {

	}

	private static String covertCode(String code) {
		String[] codes = code.split("\\.");
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (String c : codes) {
			if (i != 0) {
				sb.append(".");
			}
			if (c.matches("^[0-9]+$")) {
				sb.append(Integer.parseInt(c));
			} else {
				sb.append(c);
			}
			i++;
		}
		return sb.toString();
	}

	public static String getMessage(String code) {
		return (String) errorMessages.get(covertCode(code));
	}
}
