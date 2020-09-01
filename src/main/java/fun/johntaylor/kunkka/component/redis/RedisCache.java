package fun.johntaylor.kunkka.component.redis;

import fun.johntaylor.kunkka.utils.json.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.Optional;

/**
 * @Author John
 * @Description 缓存组件
 * @Date 2020/9/1 2:00 PM
 **/
@Component
public class RedisCache {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	public synchronized <T> void set(String domain, Object key, T value, Duration expiration) {
		stringRedisTemplate.opsForValue().set(generateKey(domain, key), JsonUtil.toJson(value), expiration);
	}

	private String generateKey(String domain, Object key) {
		return String.format("%s_%s", domain, String.valueOf(key));
	}

	private String getJson(String domain, Object key) {
		return Optional.ofNullable(stringRedisTemplate.opsForValue().get(generateKey(domain, key))).orElse("");
	}

	public <T> T get(String domain, Object key, Class<T> clz) {
		return JsonUtil.fromJson(getJson(domain, key), clz);
	}

	public <T> T get(String domain, Object key, Type type) {
		return JsonUtil.fromJson(getJson(domain, key), type);
	}
}
