package fun.johntaylor.kunkka.utils.encryption;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Objects;

/**
 * @Author John
 * @Description jwt工具类
 * @Date 2020/7/2 2:32 PM
 **/
@Slf4j
public class JwtUtil {
	public static final String TOKEN_HEADER = "Authorization";
	public static final String TOKEN_PREFIX = "Bearer ";

	private static final String SUBJECT = "kunkka";

	private static final long EXPIRITION = 15 * 24 * 60 * 60 * 1000;

	private static final String SECRET_KEY = "dJF&*JOFJO#Jdiojocijiosj23e2d9i0fjsdj";

	private static final String AUTH_ID = "auth_id";

	/**
	 * 生成web token
	 * @param id
	 * @return
	 */
	public static String createToken(Long id) {
		return Jwts
				.builder()
				.setSubject(SUBJECT)
				.claim(AUTH_ID, id)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRITION))
				.signWith(SignatureAlgorithm.HS256, SECRET_KEY)
				.compact();
	}


	private static Claims getClaims(String token) {
		try {
			return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
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
	public static Long getUserId(String token) {
		Claims claims = getClaims(token);
		if (Objects.isNull(claims)) {
			return null;
		}
		return Long.parseLong(claims.get(AUTH_ID).toString());
	}

	/**
	 * 是否过期
	 * @param token
	 * @return
	 */
	public static boolean isExpiration(String token) {
		Claims claims = getClaims(token);
		if (Objects.isNull(claims)) {
			return true;
		}
		return claims.getExpiration().before(new Date());
	}
}
