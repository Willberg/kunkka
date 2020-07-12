package fun.johntaylor.kunkka.controller.open;

import fun.johntaylor.kunkka.component.encryption.Jwt;
import fun.johntaylor.kunkka.utils.error.ErrorCode;
import fun.johntaylor.kunkka.utils.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * @Author John
 * @Description 开放用户服务
 * @Date 2020/6/22 8:08 PM
 **/
@RestController
@Slf4j
public class OpenTokenController {

	@Autowired
	private Jwt jwt;


	/**
	 * @Author John
	 * @Description 刷新token
	 * @Date 2020/7/12 10:57 AM
	 * @Param
	 * @return
	 **/
	@GetMapping(value = "/api/open/token/refresh")
	public Mono<String> refresh(ServerHttpRequest request) {
		return Mono.just(jwt.getAuthId(request))
				.map(v -> {
					if (Objects.isNull(v)) {
						return Result.fail(ErrorCode.USER_AUTHENTICATION_ERROR).toString();
					}
					return Result.success(jwt.createToken(v)).toString();
				});
	}
}
