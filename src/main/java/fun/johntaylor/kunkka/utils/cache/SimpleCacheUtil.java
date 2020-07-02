package fun.johntaylor.kunkka.utils.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import fun.johntaylor.kunkka.utils.json.JsonUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @Author John
 * @Description 简单缓存
 * @Date 2020/6/24 4:29 PM
 **/
@Slf4j
public final class SimpleCacheUtil {
	private static final Cache<String, String> CACHE = CacheBuilder.newBuilder()
			.expireAfterWrite(15, TimeUnit.DAYS)
			.build();

	private SimpleCacheUtil() {

	}

	private static String generateKeyByDomainKey(String domain, String key) {
		return String.format("%s_%s", domain, key);
	}

	private static String generateKeyByDomainKey(String domain, Object key) {
		if (key instanceof Number || key instanceof String) {
			return generateKeyByDomainKey(domain, String.valueOf(key));
		} else {
			log.error(String.format("domain:%s key generate error, key:%s", domain, key.toString()));
			return null;
		}
	}


	public static <T> void set(String domain, Object key, T value) {
		String realKey = Optional.ofNullable(generateKeyByDomainKey(domain, key)).orElse("");
		if ("".equals(key)) {
			return;
		}
		CACHE.put(realKey, JsonUtil.toJson(value));
	}

	public static <T> T get(String domain, Object key, Class<T> clz) {
		String realKey = Optional.ofNullable(generateKeyByDomainKey(domain, key)).orElse("");
		String json = Optional.ofNullable(CACHE.getIfPresent(realKey)).orElse("");
		return JsonUtil.fromJson(json, clz);
	}

	public static <T> T get(String domain, Object key, Type type) {
		String realKey = Optional.ofNullable(generateKeyByDomainKey(domain, key)).orElse("");
		String json = Optional.ofNullable(CACHE.getIfPresent(realKey)).orElse("");
		return JsonUtil.fromJson(json, type);
	}
}
