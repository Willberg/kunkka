package fun.johntaylor.kunkka.controller.user;

import fun.johntaylor.kunkka.component.thread.pool.DbThreadPool;
import fun.johntaylor.kunkka.entity.user.User;
import fun.johntaylor.kunkka.service.user.UserService;
import fun.johntaylor.kunkka.utils.error.ErrorCode;
import fun.johntaylor.kunkka.utils.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Objects;

/**
 * @Author John
 * @Descriptionn 用户功能请求
 * @Date 2020/6/22 9:45 PM
 **/
@RestController
@Slf4j
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private DbThreadPool dbThreadPool;


	/**
	 * @Author John
	 * @Description
	 * @Date 2020/6/22 9:47 PM
	 * @Param
	 * @return
	 **/
	@PostMapping(value = "/api/user/register", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<String> register(@Valid @RequestBody User reqUser) {
		return Mono.just(reqUser)
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> {
					if (Objects.isNull(reqUser.getUserName()) && Objects.isNull(reqUser.getPhoneNumber()) && Objects.isNull(reqUser.getEmail())) {
						return Result.fail(ErrorCode.SYS_PARAMETER_ERROR).toString();
					}
					Result result = userService.register(v);
					return result.toString();
				});
	}

	/**
	 * @Author John
	 * @Description
	 * @Date 2020/6/22 9:47 PM
	 * @Param
	 * @return
	 **/
	@PostMapping(value = "/api/user/login", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<String> login(@Valid @RequestBody User reqUser) {
		return Mono.just(reqUser)
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> {
					Result result = userService.login(v);
					return result.toString();
				});
	}

	/**
	 * @Author John
	 * @Description
	 * @Date 2020/6/22 9:47 PM
	 * @Param
	 * @return
	 **/
	@GetMapping(value = "/api/user/logout", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<String> logout(@Valid @RequestBody User reqUser) {
		return Mono.just(reqUser)
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> {

					return Result.success().toString();
				})
				.doOnError(e -> log.error(e.getMessage()))
				.onErrorReturn(Result.fail(ErrorCode.SYS_PARAMETER_ERROR).toString());
	}
}
