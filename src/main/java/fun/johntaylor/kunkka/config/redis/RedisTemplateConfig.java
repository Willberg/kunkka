package fun.johntaylor.kunkka.config.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @Author John
 * @Description redisTemplateConfig
 * @Date 2020/8/31 6:21 PM
 **/
@Configuration
public class RedisTemplateConfig {
	@Autowired
	private RedisConnectionFactory factory;

	@Bean
	StringRedisTemplate reactiveRedisTemplate(RedisConnectionFactory factory) {
		return new StringRedisTemplate(factory);
	}
}
