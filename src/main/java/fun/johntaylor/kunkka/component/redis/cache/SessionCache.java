package fun.johntaylor.kunkka.component.redis.cache;

import fun.johntaylor.kunkka.component.redis.RedisCache;
import fun.johntaylor.kunkka.constant.cache.CacheDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.time.Duration;

/**
 * @Author John
 * @Description session缓存
 * @Date 2020/9/1 3:37 PM
 **/
@Component
public class SessionCache {
	private static final Duration EXPIRATION = Duration.ofDays(15);
	private static final Duration CLEAR_EXPIRATION = Duration.ofMillis(1);

	@Autowired
	private RedisCache redisCache;

	public <T> void set(String key, T value) {
		redisCache.set(CacheDomain.SESSION_CACHE, key, value, EXPIRATION);
	}

	public <T> T get(String key, Class<T> cls) {
		return redisCache.get(CacheDomain.SESSION_CACHE, key, cls);
	}

	public <T> T get(String key, Type type) {
		return redisCache.get(CacheDomain.SESSION_CACHE, key, type);
	}

	public void clear(String key) {
		redisCache.set(CacheDomain.SESSION_CACHE, key, "", CLEAR_EXPIRATION);
	}
}
