package fun.johntaylor.kunkka.config.redis;

import fun.johntaylor.kunkka.constant.redis.DataBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.time.Duration;

/**
 * @Author John
 * @Description redis connection相关配置
 * @Date 2020/8/31 4:54 PM
 **/
@Configuration
public class RedisConnectionConfig {
	@Value("${redis.host}")
	private String redisHost;

	@Value("${redis.port}")
	private Integer redisPort;

	@Value("${redis.password}")
	private String redisPassword;

	@Bean(name = "redisConnectionFactory")
	public RedisConnectionFactory lettuceConnectionFactory() {

		LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
//				.useSsl().and()
				.commandTimeout(Duration.ofSeconds(2))
				.shutdownTimeout(Duration.ZERO)
				.build();

		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
		config.setPassword(redisPassword);
		config.setDatabase(DataBase.KUNKKA);
		return new LettuceConnectionFactory(config, clientConfig);
	}
}
