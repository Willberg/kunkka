package fun.johntaylor.kunkka.utils.cache.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import fun.johntaylor.kunkka.constant.cache.CacheDomain;
import fun.johntaylor.kunkka.utils.cache.BaseCache;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

/**
 * @Author John
 * @Description 用户缓存工具
 * @Date 2020/7/5 10:22 AM
 **/
public class UserCache extends BaseCache {
	static {
		final Cache<String, String> userCache = CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.DAYS).build();
		CACHE.put(CacheDomain.USER_CACHE, userCache);
	}

	public static <T> void set(Object key, T value) {
		set(CacheDomain.USER_CACHE, key, value);
	}

	public static <T> T get(Object key, Class<T> cls) {
		return get(CacheDomain.USER_CACHE, key, cls);
	}

	public static <T> T get(Object key, Type type) {
		return get(CacheDomain.USER_CACHE, key, type);
	}
}
