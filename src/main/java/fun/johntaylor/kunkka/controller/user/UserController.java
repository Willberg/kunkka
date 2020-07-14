package fun.johntaylor.kunkka.controller.user;

import fun.johntaylor.kunkka.component.encryption.Jwt;
import fun.johntaylor.kunkka.component.thread.pool.DbThreadPool;
import fun.johntaylor.kunkka.entity.encrypt.user.EncryptUser;
import fun.johntaylor.kunkka.entity.todo.TodoGroup;
import fun.johntaylor.kunkka.entity.user.User;
import fun.johntaylor.kunkka.repository.mybatis.todo.TodoGroupMapper;
import fun.johntaylor.kunkka.service.user.UserService;
import fun.johntaylor.kunkka.utils.error.ErrorCode;
import fun.johntaylor.kunkka.utils.json.JsonUtil;
import fun.johntaylor.kunkka.utils.result.Result;
import fun.johntaylor.kunkka.utils.session.SessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
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

	@Autowired
	private TodoGroupMapper todoGroupMapper;

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
					SessionUtil.setCookie(response, result);
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
	public Mono<String> login(ServerHttpRequest request,
			ServerHttpResponse response,
			@Valid @RequestBody User reqUser) {
		log.info("login: {}, port: {}", JsonUtil.toJson(reqUser), request.getLocalAddress().getPort());
		return Mono.just(reqUser)
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> {
					Result<EncryptUser> result = userService.login(v);
					SessionUtil.setCookie(response, result);
					return result.toString();
				});
	}

	/**
	 * @Author John
	 * @Description 退出
	 * @Date 2020/6/22 9:47 PM
	 * @Param
	 * @return
	 **/
	@GetMapping(value = "/api/user/logout")
	public Mono<String> logout(ServerHttpRequest request) {
		return Mono.just(Result.success())
				.map(result -> {
					SessionUtil.clearSession(request);
					return result.toString();
				});
	}


	/**
	 * @Author John
	 * @Description 生成用户授权todo链接
	 * @Date 2020/7/11 10:44 PM
	 * @Param
	 * @return
	 **/
	@GetMapping(value = "/api/user/open/todo/url/create")
	public Mono<String> createUrl(ServerHttpRequest request,
			@RequestParam(value = "url") String url,
			@RequestParam(value = "groupId") Long groupId) {
		return Mono.just(SessionUtil.getUser(request))
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> {
					TodoGroup todoGroup = todoGroupMapper.select(groupId);
					if (Objects.isNull(todoGroup) || !Objects.equals(todoGroup.getUid(), v.getId())) {
						return Result.fail(ErrorCode.USER_ILLEGAL_OPERATION).toString();
					}
					return Result.success(String.format("%s?token=%s", url, jwt.createToken(groupId))).toString();
				});
	}
}
