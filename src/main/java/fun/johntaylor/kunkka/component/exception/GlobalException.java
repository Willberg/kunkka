package fun.johntaylor.kunkka.component.exception;

import fun.johntaylor.kunkka.utils.error.ErrorCode;
import fun.johntaylor.kunkka.utils.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * @Author John
 * @Description 全局异常处理
 * @Date 2020/6/22 6:14 PM
 **/
@RestControllerAdvice
@Slf4j
public class GlobalException {

	@ExceptionHandler(Throwable.class)
	public void testExceptions(Throwable t) {
		log.error("exception: {}", t);
	}

	/**
	 * @Author John
	 * @Description 请求参数异常处理
	 * @Date 2020/6/21 3:55 PM
	 **/
	@ExceptionHandler(WebExchangeBindException.class)
	public Mono<String> bindingResultExceptions(WebExchangeBindException ex) {
		log.error("object: {}, message: {}, exception: {}",
				Optional.ofNullable(ex.getBindingResult().getTarget()).orElse(ex.getBindingResult()).getClass().getName(),
				ex.getMessage(),
				ex);

		List<String> message = new LinkedList<>();
		return Flux
				.fromIterable(ex.getBindingResult().getAllErrors())
				.reduce(message, (list, e) -> {
					message.add(e.getDefaultMessage());
					return list;
				})
				.map(data -> Result.failWithMessageData(ErrorCode.SYS_PARAMETER_ERROR, data).toString());
	}

}