package fun.johntaylor.kunkka.component.encryption;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

/**
 * @Author John
 * @Description jwt组件
 * @Date 2020/7/3 10:31 PM
 **/
@Component
@Slf4j
public class Jwt {

	@Value("${jwt.subject}")
	private String subject;

	@Value("${jwt.expiration}")
	private long expiration;

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.auth.id}")
	private String authId;

	/**
	 * 生成web token
	 * @param id
	 * @return
	 */
	public String createToken(Long id) {
		return Jwts
				.builder()
				.setSubject(subject)
				.claim(authId, id)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + expiration))
				.signWith(SignatureAlgorithm.HS256, secret)
				.compact();
	}

	/**
	 * @Author John
	 * @Description 获取声明
	 * @Date 2020/7/3 10:38 PM
	 * @Param
	 * @return
	 **/
	private Claims getClaims(String token) {
		try {
			return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
		} catch (Exception e) {
			log.error("checkJwt error", e);
			return null;
		}
	}

	/**
	 * 获取用户ID
	 * @param token
	 * @return
	 */
	public Long getUserId(String token) {
		Claims claims = getClaims(token);
		if (Objects.isNull(claims)) {
			return null;
		}
		return Long.parseLong(claims.get(secret).toString());
	}

	/**
	 * 是否过期
	 * @param token
	 * @return
	 */
	public boolean isExpiration(String token) {
		Claims claims = getClaims(token);
		if (Objects.isNull(claims)) {
			return true;
		}
		return claims.getExpiration().before(new Date());
	}
}
