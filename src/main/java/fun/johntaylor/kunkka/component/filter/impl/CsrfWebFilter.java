package fun.johntaylor.kunkka.component.filter.impl;

import fun.johntaylor.kunkka.component.filter.BaseFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * @Author John
 * @Description 跨域请求伪造过滤器
 * @Date 2020/7/3 9:33 AM
 **/
@Component
@Order(50)
public class CsrfWebFilter extends BaseFilter implements WebFilter {
	/**
	 * 请求来源
	 */
	private static final String ORIGIN = "Origin";

	/**
	 * 预检请求
	 */
	private static final String OPTIONS = "options";

	@Value("${csrf.whitelist.urls}")
	private String csrfWhiteListUrls;

	@Override
	public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
		// 使用通源策略和白名单过滤
		ServerHttpRequest request = serverWebExchange.getRequest();
		String origin = request.getHeaders().getFirst(ORIGIN);
		// 没有来源，是正常请求
		if (Objects.isNull(origin)) {
			return webFilterChain.filter(serverWebExchange);
		}

		if (origin.matches(csrfWhiteListUrls)) {
			// 允许跨域资源共享的请求, Preflighted Request(预检请求)设置允许cors
			ServerHttpResponse response = serverWebExchange.getResponse();
			response.getHeaders().add("Access-Control-Allow-Origin", origin);
			// 如果不设置为true，浏览器将无法获取response
			response.getHeaders().add("Access-Control-Allow-Credentials", "true");
			if (OPTIONS.equalsIgnoreCase(request.getMethodValue())) {
				response.getHeaders().add("Access-Control-Allow-Methods", "*");
				response.getHeaders().add("Access-Control-Allow-Headers", "*, Content-Type");
				return response.writeWith(Mono.just(response.bufferFactory().wrap("".getBytes())));
			}

			return webFilterChain.filter(serverWebExchange);
		} else {
			// 非法跨域请求
			return setForbiddenResponse(serverWebExchange.getResponse());
		}
	}
}
