package fun.johntaylor.kunkka.controller.user;

import fun.johntaylor.kunkka.component.encryption.Jwt;
import fun.johntaylor.kunkka.component.redis.session.Session;
import fun.johntaylor.kunkka.component.thread.pool.DbThreadPool;
import fun.johntaylor.kunkka.entity.encrypt.user.EncryptUser;
import fun.johntaylor.kunkka.entity.todo.TodoGroup;
import fun.johntaylor.kunkka.entity.user.User;
import fun.johntaylor.kunkka.entity.user.request.UpdatePassword;
import fun.johntaylor.kunkka.repository.mybatis.todo.TodoGroupMapper;
import fun.johntaylor.kunkka.service.user.UserService;
import fun.johntaylor.kunkka.utils.error.ErrorCode;
import fun.johntaylor.kunkka.utils.json.JsonUtil;
import fun.johntaylor.kunkka.utils.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
	@Value("${env}")
	private String env;

	@Autowired
	private UserService userService;

	@Autowired
	private DbThreadPool dbThreadPool;

	@Autowired
	private Jwt jwt;

	@Autowired
	private TodoGroupMapper todoGroupMapper;

	@Autowired
	private Session session;

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
						return Result.failWithMessage(ErrorCode.SYS_PARAMETER_ERROR, "注册信息不全").toString();
					}
					Result<EncryptUser> result = userService.register(v);
					session.setCookie(env, response, result);
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
					session.setCookie(env, response, result);
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
					session.clearSession(request);
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
		return Mono.just(session.getUser(request))
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> {
					TodoGroup todoGroup = todoGroupMapper.select(groupId);
					if (Objects.isNull(todoGroup) || !Objects.equals(todoGroup.getUid(), v.getId())) {
						return Result.fail(ErrorCode.USER_ILLEGAL_OPERATION).toString();
					}
					return Result.success(String.format("%s?token=%s", url, jwt.createToken(groupId))).toString();
				});
	}

	/**
	 * @Author John
	 * @Description 登录
	 * @Date 2020/6/22 9:47 PM
	 * @Param request
	 * @return Mono<String>
	 **/
	@GetMapping(value = "/api/user/profile")
	public Mono<String> getUser(ServerHttpRequest request) {
		return Mono.just(session.getUser(request))
				.map(v -> userService.getProfile(v).toString());
	}

	@PostMapping(value = "/api/user/password/change")
	public Mono<String> changePassword(ServerHttpRequest request,
			@Valid @RequestBody UpdatePassword req) {
		return Mono.just(session.getUser(request))
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> {
					if (req.getNewPassword().equals(req.getOldPassword())) {
						return Result.failWithMessage(ErrorCode.SYS_PARAMETER_ERROR, "新密码不能与旧密码相同").toString();
					}
					return userService.changePassword(v.getId(), req.getOldPassword(), req.getNewPassword()).toString();
				});
	}
}
