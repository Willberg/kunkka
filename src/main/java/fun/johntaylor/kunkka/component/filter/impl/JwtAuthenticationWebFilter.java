package fun.johntaylor.kunkka.component.filter.impl;

import fun.johntaylor.kunkka.component.encryption.Jwt;
import fun.johntaylor.kunkka.component.filter.BaseFilter;
import fun.johntaylor.kunkka.entity.user.User;
import fun.johntaylor.kunkka.utils.error.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * @Author John
 * @Description jwt鉴权过滤器
 * @Date 2020/7/2 5:11 PM
 **/
@Component
@Order(200)
public class JwtAuthenticationWebFilter extends BaseFilter implements WebFilter {
	@Autowired
	private Jwt jwt;

	@Value("${jwt.authentication.whitelist.urls}")
	private String authenticationWhiteListUrls;

	@Override
	public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
		ServerHttpRequest request = serverWebExchange.getRequest();
		if (request.getPath().value().matches(authenticationWhiteListUrls)) {
			return webFilterChain.filter(serverWebExchange);
		}

		// 认证
		ServerHttpResponse response = serverWebExchange.getResponse();
		String token = request.getHeaders().getFirst(Jwt.TOKEN_HEADER);
		if (StringUtils.isEmpty(token) || !token.startsWith(Jwt.TOKEN_PREFIX)) {
			return setErrorResponse(response, ErrorCode.USER_AUTHENTICATION_ERROR);
		}

		String jwtToken = jwt.getJwtToken(request);
		if (jwt.isExpiration(jwtToken)) {
			return setErrorResponse(response, ErrorCode.USER_AUTHENTICATION_ERROR);
		}

		// 权限校验
		User user = jwt.getUser(jwtToken);
		if (Objects.isNull(user)) {
			return setErrorResponse(response, ErrorCode.USER_AUTHENTICATION_ERROR);
		}

		return webFilterChain.filter(serverWebExchange);
	}
}
