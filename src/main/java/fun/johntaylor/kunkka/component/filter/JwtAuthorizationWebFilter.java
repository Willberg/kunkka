package fun.johntaylor.kunkka.component.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * @Author John
 * @Description jwt授权过滤器
 * @Date 2020/7/3 9:14 AM
 **/
@Component
@Order(200)
public class JwtAuthorizationWebFilter implements WebFilter {
	@Value("${jwt.authorization.urls}")
	private String authorizationUrls;

	@Override
	public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
		ServerHttpRequest request=  serverWebExchange.getRequest();
		if(!request.getPath().value().matches(authorizationUrls)){
			return webFilterChain.filter(serverWebExchange);
		}
		return webFilterChain
				.filter(serverWebExchange)
				.doFinally(e -> {

				});
	}

	private void setToken() {

	}

}
