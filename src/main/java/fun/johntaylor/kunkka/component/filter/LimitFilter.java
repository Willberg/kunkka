package fun.johntaylor.kunkka.component.filter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * @Author John
 * @Description 限流过滤器
 * @Date 2020/7/3 10:23 PM
 **/
@Component
@Order(1)
public class LimitFilter implements WebFilter {

	@Override
	public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
		return webFilterChain.filter(serverWebExchange);
	}


	/**
	 * @Author John
	 * @Description 固定窗口
	 * @Date 2020/7/5 10:29 AM
	 * @Param
	 * @return
	 **/
	public boolean isFixedWindowLimit() {
		return false;
	}

	/**
	 * @Author John
	 * @Description 滑动窗口
	 * @Date 2020/7/5 10:29 AM
	 * @Param
	 * @return
	 **/
	public boolean isSlideLimit() {
		return false;
	}

	/**
	 * @Author John
	 * @Description 漏桶
	 * @Date 2020/7/5 10:29 AM
	 * @Param
	 * @return
	 **/
	public boolean isLeakBucketLimit() {
		return false;
	}

	/**
	 * @Author John
	 * @Description 令牌桶
	 * @Date 2020/7/5 10:29 AM
	 * @Param
	 * @return
	 **/
	public boolean isTokenBucketLimit() {
		return false;
	}
}
