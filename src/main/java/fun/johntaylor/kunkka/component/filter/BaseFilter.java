package fun.johntaylor.kunkka.component.filter;

import fun.johntaylor.kunkka.utils.error.ErrorCode;
import fun.johntaylor.kunkka.utils.result.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

/**
 * @Author John
 * @Description 基本操作
 * @Date 2020/7/6 12:22 PM
 **/
public class BaseFilter {
	protected Mono<Void> setErrorResponse(ServerHttpResponse response, String code) {
		Result result = Result.fail(code);
		return response.writeWith(Mono.just(response.bufferFactory().wrap(result.toString().getBytes())));
	}

	protected Mono<Void> setForbiddenResponse(ServerHttpResponse response) {
		response.setStatusCode(HttpStatus.FORBIDDEN);
		Result result = Result.fail(ErrorCode.SYS_FORBIDDEN_ERROR);
		return response.writeWith(Mono.just(response.bufferFactory().wrap(result.toString().getBytes())));
	}
}
