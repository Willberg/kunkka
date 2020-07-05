package fun.johntaylor.kunkka.controller.user;

import fun.johntaylor.kunkka.component.encryption.Jwt;
import fun.johntaylor.kunkka.component.thread.pool.DbThreadPool;
import fun.johntaylor.kunkka.entity.encrypt.user.EncryptUser;
import fun.johntaylor.kunkka.entity.user.User;
import fun.johntaylor.kunkka.service.user.UserService;
import fun.johntaylor.kunkka.utils.error.ErrorCode;
import fun.johntaylor.kunkka.utils.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
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

	@Autowired
	private Jwt jwt;

	/**
	 * @Author John
	 * @Description 注册
	 * @Date 2020/6/22 9:47 PM
	 * @Param
	 * @return
	 **/
	@PostMapping(value = "/api/user/register")
	public Mono<String> register(ServerHttpResponse response,
			@Valid @RequestBody User reqUser) {
		return Mono.just(reqUser)
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> {
					if (Objects.isNull(reqUser.getUserName()) && Objects.isNull(reqUser.getPhoneNumber()) && Objects.isNull(reqUser.getEmail())) {
						return Result.fail(ErrorCode.SYS_PARAMETER_ERROR).toString();
					}
					Result<EncryptUser> result = userService.register(v);
					jwt.setTokenHeader(response, result);
					return result.toString();
				});
	}

	/**
	 * @Author John
	 * @Description 登录
	 * @Date 2020/6/22 9:47 PM
	 * @Param
	 * @return
	 **/
	@PostMapping(value = "/api/user/login")
	public Mono<String> login(ServerHttpResponse response,
			@Valid @RequestBody User reqUser) {
		return Mono.just(reqUser)
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> {
					Result<EncryptUser> result = userService.login(v);
					jwt.setTokenHeader(response, result);
					return result.toString();
				});
	}

	/**
	 * @Author John
	 * @Description 刷新jwt
	 * @Date 2020/6/22 9:47 PM
	 * @Param
	 * @return
	 **/
	@PostMapping(value = "/api/user/jwt/refresh")
	public Mono<String> refresh(ServerHttpRequest request) {
		return Mono.just(jwt.getUser(request))
				.map(user -> Result.success(jwt.createToken(user.getId())).toString());
	}
}
