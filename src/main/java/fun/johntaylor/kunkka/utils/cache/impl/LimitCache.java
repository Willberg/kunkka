package fun.johntaylor.kunkka.utils.cache.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import fun.johntaylor.kunkka.constant.cache.CacheDomain;
import fun.johntaylor.kunkka.utils.cache.BaseCache;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

/**
 * @Author John
 * @Description 限流缓存工具
 * @Date 2020/7/5 10:22 AM
 **/
public final class LimitCache extends BaseCache {
	static {
		Cache<String, String> limitCache = CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.SECONDS).build();
		CACHE.put(CacheDomain.LIMIT_CACHE, limitCache);
	}

	public static <T> void set(String function, Object key, T value) {
		set(CacheDomain.LIMIT_CACHE, key, value);
	}

	public static <T> T get(Object key, Class<T> cls) {
		return get(CacheDomain.LIMIT_CACHE, key, cls);
	}

	public static <T> T get(Object key, Type type) {
		return get(CacheDomain.LIMIT_CACHE, key, type);
	}
}
