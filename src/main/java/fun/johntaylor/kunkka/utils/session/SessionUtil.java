package fun.johntaylor.kunkka.utils.session;

import fun.johntaylor.kunkka.entity.encrypt.user.EncryptUser;
import fun.johntaylor.kunkka.entity.user.User;
import fun.johntaylor.kunkka.utils.cache.impl.SessionCache;
import fun.johntaylor.kunkka.utils.cache.impl.UserCache;
import fun.johntaylor.kunkka.utils.result.Result;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.DigestUtils;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

/**
 * @Author John
 * @Description
 * @Date 2020/7/9 3:34 PM
 **/
public final class SessionUtil {
	public static final String SESSION_COOKIE_NAME = "sessionCookie";
	public static final String SESSION_ID = "jSessionId";

	/**
	 * 设置cookie和session
	 * @param env
	 * @param response
	 * @param result
	 */
	public static void setCookie(String env, ServerHttpResponse response, Result<EncryptUser> result) {
		if (result.isSuccess()) {
			EncryptUser u = result.getData();
			String cookieValue = DigestUtils.md5DigestAsHex(String.format("%s%s", System.currentTimeMillis(), UUID.randomUUID().toString()).getBytes());
			SessionCache.set(cookieValue, u.getId());
			ResponseCookie cookie = genResponseCookie(env, cookieValue);
			response.getCookies().set(SESSION_COOKIE_NAME, cookie);
		}
	}

	/**
	 * 刷新cookie和session的时间
	 * @param env
	 * @param response
	 * @param cookieValue
	 * @param uid
	 */
	public static void refreshCookie(String env, ServerHttpResponse response, String cookieValue, Long uid) {
		SessionCache.set(cookieValue, uid);
		ResponseCookie cookie = genResponseCookie(env, cookieValue);
		response.getCookies().set(SESSION_COOKIE_NAME, cookie);
	}

	/**
	 * 根据环境设置cookie
	 * @param env
	 * @param cookieValue
	 * @return ResponseCookie
	 */
	private static ResponseCookie genResponseCookie(String env, String cookieValue) {
		boolean isProductive = "pro".equalsIgnoreCase(env);
		if (isProductive) {
			// cookie被同一个host的不同port共享，https://stackoverflow.com/questions/1612177/are-http-cookies-port-specific
			return ResponseCookie.from(SESSION_ID, cookieValue)
					//防止xss, 不能使用Document.cookie访问, https://developer.mozilla.org/en-US/docs/Web/HTTP/Cookies
					.httpOnly(true)
					// Specify SameSite=None and Secure if the cookie should be sent in cross-site requests. This enables third-party use.
					.sameSite("None")
					.secure(true)
					.maxAge(Duration.ofDays(15))
					.path("/")
					.build();
		} else {
			return ResponseCookie.from(SESSION_ID, cookieValue)
					.maxAge(Duration.ofDays(15))
					.path("/")
					.build();
		}
	}

	/**
	 *
	 * @param request
	 */
	public static User getUser(ServerHttpRequest request) {
		HttpCookie cookie = request.getCookies().getFirst(SESSION_ID);
		if (Objects.isNull(cookie)) {
			return new User();
		}

		String cookieValue = cookie.getValue();
		Long uid = SessionCache.get(cookieValue, Long.class);
		return UserCache.get(uid, User.class);
	}

	/**
	 * 过期session
	 * @param request
	 */
	public static void clearSession(ServerHttpRequest request) {
		HttpCookie cookie = request.getCookies().getFirst(SESSION_ID);
		if (Objects.nonNull(cookie)) {
			String cookieValue = cookie.getValue();
			SessionCache.clear(cookieValue);
		}
	}
}
