package util;

import fun.johntaylor.kunkka.config.redis.RedisConnectionConfig;
import fun.johntaylor.kunkka.config.redis.RedisTemplateConfig;
import fun.johntaylor.kunkka.entity.user.User;
import fun.johntaylor.kunkka.utils.json.JsonUtil;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.Optional;

@SpringBootTest(classes = {RedisConnectionConfig.class, RedisTemplateConfig.class})
public class RedisTest {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Test
	public void testInsertStr() {
		User u = new User();
		u.setId(1L);
		u.setUserName("test1");
		stringRedisTemplate.opsForValue().set(String.valueOf(u.getId()), JsonUtil.toJson(u), Duration.ofMillis(20000));
		stringRedisTemplate.opsForValue().set("2", "", Duration.ofNanos(1));
		User user = JsonUtil.fromJson(Optional.ofNullable(stringRedisTemplate.opsForValue().get(String.valueOf(u.getId()))).orElse(""), User.class);
		System.out.println(user.getUserName());
	}

	@Test
	public void testSample() {
		RedisClient client = RedisClient.create("redis://test123@localhost");
		StatefulRedisConnection<String, String> connection = client.connect();
		RedisStringCommands sync = connection.sync();
		sync.set("key", "test");
		String value = (String) sync.get("key");
		System.out.println(value);
	}
}
