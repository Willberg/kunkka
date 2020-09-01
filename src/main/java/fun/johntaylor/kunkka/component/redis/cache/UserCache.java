package fun.johntaylor.kunkka.component.redis.cache;

import fun.johntaylor.kunkka.component.redis.RedisCache;
import fun.johntaylor.kunkka.constant.cache.CacheDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.time.Duration;

/**
 * @Author John
 * @Description 用户缓存
 * @Date 2020/9/1 2:33 PM
 **/
@Component
public class UserCache {
	private static final Duration EXPIRATION = Duration.ofDays(15);

	@Autowired
	private RedisCache redisCache;

	public <T> void set(Object key, T value) {
		redisCache.set(CacheDomain.USER_CACHE, key, value, EXPIRATION);
	}

	public <T> T get(Object key, Class<T> cls) {
		return redisCache.get(CacheDomain.USER_CACHE, key, cls);
	}

	public <T> T get(Object key, Type type) {
		return redisCache.get(CacheDomain.USER_CACHE, key, type);
	}
}
