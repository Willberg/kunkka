package fun.johntaylor.kunkka.utils.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import fun.johntaylor.kunkka.utils.json.JsonUtil;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;

/**
 * @Author John
 * @Description 简单缓存
 * @Date 2020/6/24 4:29 PM
 **/
public class BaseCache {
	protected static final Cache<String, Cache<String, String>> CACHE = CacheBuilder.newBuilder().build();

	protected static synchronized <T> void set(String domain, Object key, T value) {
		Cache<String, String> domainCache = CACHE.getIfPresent(domain);
		if (Objects.isNull(domainCache)) {
			return;
		}

		domainCache.put(String.valueOf(key), JsonUtil.toJson(value));
	}

	private static String getJson(String domain, Object key) {
		Cache<String, String> domainCache = CACHE.getIfPresent(domain);
		if (Objects.isNull(domainCache)) {
			return null;
		}

		return Optional.ofNullable(domainCache.getIfPresent(String.valueOf(key))).orElse("");
	}

	protected static <T> T get(String domain, Object key, Class<T> clz) {
		return JsonUtil.fromJson(getJson(domain, key), clz);
	}

	protected static <T> T get(String domain, Object key, Type type) {
		return JsonUtil.fromJson(getJson(domain, key), type);
	}
}
