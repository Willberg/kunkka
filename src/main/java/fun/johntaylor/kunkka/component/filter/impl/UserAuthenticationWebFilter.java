package fun.johntaylor.kunkka.component.filter.impl;

import fun.johntaylor.kunkka.component.filter.BaseFilter;
import fun.johntaylor.kunkka.component.redis.cache.SessionCache;
import fun.johntaylor.kunkka.component.redis.cache.UserCache;
import fun.johntaylor.kunkka.component.redis.session.Session;
import fun.johntaylor.kunkka.entity.user.User;
import fun.johntaylor.kunkka.utils.error.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static fun.johntaylor.kunkka.component.redis.session.Session.SESSION_ID;


/**
 * @Author John
 * @Description 用户鉴权
 * @Date 2020/7/2 5:11 PM
 **/
@Component
@Order(200)
public class UserAuthenticationWebFilter extends BaseFilter implements WebFilter {

	@Value("${env}")
	private String env;

	@Value("${authentication.whitelist.urls}")
	private String authenticationWhiteListUrls;

	@Autowired
	private SessionCache sessionCache;

	@Autowired
	private Session session;

	@Autowired
	private UserCache userCache;

	@Override
	public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
		ServerHttpRequest request = serverWebExchange.getRequest();
		ServerHttpResponse response = serverWebExchange.getResponse();
		if (request.getPath().value().matches(authenticationWhiteListUrls)) {
			return webFilterChain.filter(serverWebExchange);
		}

		// 认证
		HttpCookie cookie = request.getCookies().getFirst(SESSION_ID);
		if (Objects.isNull(cookie)) {
			return setErrorResponse(response, ErrorCode.USER_AUTHENTICATION_ERROR);
		}
		String cookieValue = cookie.getValue();
		Long uid = sessionCache.get(cookieValue, Long.class);
		User user = userCache.get(uid, User.class);
		if (Objects.isNull(user)) {
			return setErrorResponse(response, ErrorCode.USER_AUTHENTICATION_ERROR);
		}

		// 刷新session
		session.refreshCookie(env, response, cookieValue, uid);

		// 权限校验

		return webFilterChain.filter(serverWebExchange);

	}
}
