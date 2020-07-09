package fun.johntaylor.kunkka.component.encryption;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
	public static final String TOKEN_HEADER = "Authorization";
	public static final String TOKEN_PREFIX = "Bearer ";

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
				.claim(authId, authId)
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
			return null;
		}
	}

	public String getJwtToken(ServerHttpRequest request) {
		String token = request.getHeaders().getFirst(TOKEN_HEADER);
		if (StringUtils.isEmpty(token) || !token.startsWith(Jwt.TOKEN_PREFIX)) {
			return null;
		}
		return token.substring(Jwt.TOKEN_PREFIX.length());
	}

	/**
	 * 是否过期
	 * @param token
	 * @return
	 */
	public boolean isExpiration(String token) {
		if (Objects.isNull(token)) {
			return true;
		}

		Claims claims = getClaims(token);
		if (Objects.isNull(claims)) {
			return true;
		}
		return claims.getExpiration().before(new Date());
	}

	/**
	 * 获取AuthID
	 * @param request
	 * @return
	 */
	public Long getAuthId(ServerHttpRequest request) {
		String token = getJwtToken(request);

		Claims claims = getClaims(token);
		if (Objects.isNull(claims)) {
			return null;
		}

		try {
			return Long.parseLong(String.valueOf(claims.get(authId)));
		} catch (Exception e) {
			return null;
		}
	}
}
