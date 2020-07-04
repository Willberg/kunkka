package fun.johntaylor.kunkka.component.filter;

import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * @Author John
 * @Description 基本过滤器
 * @Date 2020/7/3 9:03 AM
 **/
@Component
@Order(100)
public class JsonWebFilter implements WebFilter {

	@Override
	public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
		ServerHttpResponse response = serverWebExchange.getResponse();
		response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
		return webFilterChain.filter(serverWebExchange);
	}
}
