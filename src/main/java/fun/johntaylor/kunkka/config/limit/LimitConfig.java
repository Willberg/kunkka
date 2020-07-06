package fun.johntaylor.kunkka.config.limit;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author John
 * @Description 令牌限流器
 * @Date 2020/7/6 9:57 PM
 **/
@Configuration
public class LimitConfig {
	@Value("${limit.max}")
	private Long limitMax;

	@Bean
	public RateLimiter rateLimiter() {
		return RateLimiter.create(limitMax);
	}
}
