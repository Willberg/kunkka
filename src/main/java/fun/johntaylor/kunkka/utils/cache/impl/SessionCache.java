package fun.johntaylor.kunkka.utils.cache.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import fun.johntaylor.kunkka.constant.cache.CacheDomain;
import fun.johntaylor.kunkka.utils.cache.BaseCache;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

/**
 * @Author John
 * @Description session缓存
 * @Date 2020/7/9 3:37 PM
 **/
public final class SessionCache extends BaseCache {
	private static Cache<String, String> limitCache;

	static {
		limitCache = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).build();
		CACHE.put(CacheDomain.SESSION_CACHE, limitCache);
	}

	public static <T> void set(String key, T value) {
		set(CacheDomain.SESSION_CACHE, key, value);
	}

	public static <T> T get(String key, Class<T> cls) {
		return get(CacheDomain.SESSION_CACHE, key, cls);
	}

	public static <T> T get(String key, Type type) {
		return get(CacheDomain.SESSION_CACHE, key, type);
	}

	public static void clear(String key) {
		limitCache.invalidate(key);
	}
}
