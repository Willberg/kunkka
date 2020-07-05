package fun.johntaylor.kunkka.utils.cache;

import fun.johntaylor.kunkka.constant.cache.CacheDomain;

import java.lang.reflect.Type;

/**
 * @Author John
 * @Description 限流缓存工具
 * @Date 2020/7/5 10:22 AM
 **/
public final class LimitCacheUtil {
	public static <T> void set(Object key, T value) {
		SimpleCacheUtil.set(CacheDomain.LIMIT_CACHE, key, value);
	}

	public static <T> T get(Object key, Class<T> cls) {
		return SimpleCacheUtil.get(CacheDomain.LIMIT_CACHE, key, cls);
	}

	public static <T> T get(Object key, Type type) {
		return SimpleCacheUtil.get(CacheDomain.LIMIT_CACHE, key, type);
	}
}
