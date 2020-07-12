package fun.johntaylor.kunkka.controller.open;

import fun.johntaylor.kunkka.component.encryption.Jwt;
import fun.johntaylor.kunkka.component.thread.pool.DbThreadPool;
import fun.johntaylor.kunkka.entity.todo.Todo;
import fun.johntaylor.kunkka.entity.validation.Insert;
import fun.johntaylor.kunkka.service.todo.TodoService;
import fun.johntaylor.kunkka.utils.error.ErrorCode;
import fun.johntaylor.kunkka.utils.json.JsonUtil;
import fun.johntaylor.kunkka.utils.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Objects;

/**
 * @Author John
 * @Description 开放todo服务
 * @Date 2020/6/22 8:08 PM
 **/
@RestController
@Slf4j
public class OpenTodoController {
	@Autowired
	private TodoService todoService;

	@Autowired
	private DbThreadPool dbThreadPool;

	@Autowired
	private Jwt jwt;


	/**
	 * @Author John
	 * @Description 添加单个任务
	 * @Date 2020/7/11 4:54 PM
	 * @Param
	 * @return
	 **/
	@PostMapping(value = "/api/open/todo/add")
	public Mono<String> add(@Validated(value = Insert.class) @RequestBody Todo todo) {
		log.info("add: " + JsonUtil.toJson(todo));
		return Mono.just(todo)
				.publishOn(dbThreadPool.daoInstance())
				.map(t -> {
					todo.setCreateTime(System.currentTimeMillis());
					todo.setUpdateTime(System.currentTimeMillis());
					todo.setStatus(Todo.S_INITIAL);
					return todoService.openAddTodo(todo).toString();
				});
	}

	/**
	 * @Author John
	 * @Description 修改单个任务
	 * @Date 2020/7/11 4:54 PM
	 * @Param
	 * @return
	 **/
	@PostMapping(value = "/api/open/todo/update")
	public Mono<String> update(@Valid @RequestBody Todo todo) {
		log.info("update: " + JsonUtil.toJson(todo));
		return Mono.just(todo)
				.publishOn(dbThreadPool.daoInstance())
				.map(t -> {
					if (Objects.isNull(todo.getGroupId())) {
						return Result.failWithMessage(ErrorCode.SYS_PARAMETER_ERROR, "必须指定任务组").toString();
					}

					todo.setUpdateTime(System.currentTimeMillis());
					todo.setStatus(Todo.S_INITIAL);
					return todoService.openUpdateTodo(todo).toString();
				});
	}


	/**
	 * @Author John
	 * @Description 查看任务列表
	 * @Date 2020/7/11 4:54 PM
	 * @Param
	 * @return
	 **/
	@GetMapping(value = "/api/open/todo/list")
	public Mono<String> list(ServerHttpRequest request) {
		return Mono.just(jwt.getAuthId(request))
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> todoService.searchTodoListByGroupId(v).toString());
	}
}
