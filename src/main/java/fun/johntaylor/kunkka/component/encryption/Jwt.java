package fun.johntaylor.kunkka.component.encryption;

import fun.johntaylor.kunkka.entity.encrypt.user.EncryptUser;
import fun.johntaylor.kunkka.entity.user.User;
import fun.johntaylor.kunkka.utils.cache.impl.UserCache;
import fun.johntaylor.kunkka.utils.result.Result;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
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
			return null;
		}
	}

	/**
	 * 获取用户
	 * @param token
	 * @return
	 */
	public User getUser(String token) {
		if (Objects.isNull(token)) {
			return null;
		}

		Claims claims = getClaims(token);
		if (Objects.isNull(claims)) {
			return null;
		}

		try {
			Long uid = Long.parseLong(String.valueOf(claims.get(authId)));
			return UserCache.get(uid, User.class);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 获取用户
	 * @param request
	 * @return
	 */
	public User getUser(ServerHttpRequest request) {
		return getUser(getJwtToken(request));
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

	public void setTokenHeader(ServerHttpResponse response, Result<EncryptUser> result) {
		if (result.isSuccess()) {
			EncryptUser u = result.getData();
			response.getHeaders().add(TOKEN_HEADER, createToken(u.getId()));
		}
	}
}
