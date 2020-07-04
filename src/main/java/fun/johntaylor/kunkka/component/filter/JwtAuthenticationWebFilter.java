package fun.johntaylor.kunkka.component.filter;

import fun.johntaylor.kunkka.utils.result.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * @Author John
 * @Description jwt鉴权过滤器
 * @Date 2020/7/2 5:11 PM
 **/
@Component
@Order(250)
public class JwtAuthenticationWebFilter implements WebFilter {
	@Value("${jwt.authentication.whitelist.urls}")
	private String authenticationWhiteListUrls;

	@Override
	public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
		ServerHttpRequest request = serverWebExchange.getRequest();
		if (request.getPath().value().matches(authenticationWhiteListUrls)) {
			return webFilterChain.filter(serverWebExchange);
		}
		String authorization = request.getHeaders().getFirst("Authorization");
		return webFilterChain.filter(serverWebExchange);

	}

	protected Mono<Void> setErrorResponse(ServerHttpResponse response, String message) {
		Result result = Result.failWithMessage(message);
		return response.writeWith(Mono.just(response.bufferFactory().wrap(result.toString().getBytes())));

	}
}
