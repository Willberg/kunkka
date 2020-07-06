package fun.johntaylor.kunkka.component.filter.impl;

import com.google.common.util.concurrent.RateLimiter;
import fun.johntaylor.kunkka.component.filter.BaseFilter;
import fun.johntaylor.kunkka.utils.cache.impl.LimitCache;
import fun.johntaylor.kunkka.utils.error.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author John
 * @Description 限流过滤器
 * @Date 2020/7/3 10:23 PM
 **/
@Component
@Order(1)
public class LimitFilter extends BaseFilter implements WebFilter {

	@Value("${limit.max}")
	private Long limitMax;

	@Autowired
	private RateLimiter rateLimiter;

	@Override
	public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
		if (isTokenBucketLimit()) {
			return setErrorResponse(serverWebExchange.getResponse(), ErrorCode.SYS_LIMIT_ERROR);
		}
		return webFilterChain.filter(serverWebExchange);
	}


	/**
	 * @Author John
	 * @Description 固定窗口
	 * @Date 2020/7/5 10:29 AM
	 * @Param
	 * @return
	 **/
	public synchronized boolean isFixedWindowLimit() {
		long key = System.currentTimeMillis() / 10000;
		AtomicLong reqCount = Optional.ofNullable(LimitCache.get(key, AtomicLong.class)).orElse(new AtomicLong(0));
		long totalCount = reqCount.addAndGet(1L);
		if (totalCount <= limitMax) {
			LimitCache.set(key, reqCount);
			return false;
		} else {
			return true;
		}
	}

	/**
	 * @Author John
	 * @Description 滑动窗口
	 * @Date 2020/7/5 10:29 AM
	 * @Param
	 * @return
	 **/
	public synchronized boolean isSlideLimit() {
		// 计算当前分片请求数
		long key = System.currentTimeMillis() / 1000;
		AtomicLong reqCount = Optional.ofNullable(LimitCache.get(key, AtomicLong.class)).orElse(new AtomicLong(0));
		long currentCount = reqCount.addAndGet(1L);

		// 前4个分片的总请求数
		long winIdx = System.currentTimeMillis() / 1000;
		long i = winIdx - 4;
		long totalCount = 0;
		for (; i < winIdx; i++) {
			totalCount += Optional.ofNullable(LimitCache.get(i, AtomicLong.class)).orElse(new AtomicLong(0)).get();
		}
		if (totalCount + currentCount > limitMax) {
			return true;
		} else {
			LimitCache.set(key, currentCount);
			return false;
		}
	}


	/**
	 * @Author John
	 * @Description 令牌桶， guava
	 * @Date 2020/7/5 10:29 AM
	 * @Param
	 * @return
	 **/
	public boolean isTokenBucketLimit() {
		return !rateLimiter.tryAcquire();
	}
}
