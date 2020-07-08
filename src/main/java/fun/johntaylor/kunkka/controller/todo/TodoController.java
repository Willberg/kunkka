package fun.johntaylor.kunkka.controller.todo;

import fun.johntaylor.kunkka.component.encryption.Jwt;
import fun.johntaylor.kunkka.component.thread.pool.DbThreadPool;
import fun.johntaylor.kunkka.entity.todo.Todo;
import fun.johntaylor.kunkka.entity.todo.TodoGroup;
import fun.johntaylor.kunkka.entity.todo.request.AddPatchRequest;
import fun.johntaylor.kunkka.entity.user.User;
import fun.johntaylor.kunkka.service.todo.TodoService;
import fun.johntaylor.kunkka.utils.error.ErrorCode;
import fun.johntaylor.kunkka.utils.json.JsonUtil;
import fun.johntaylor.kunkka.utils.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @Author John
 * @Description
 * @Date 2020/6/22 8:08 PM
 **/
@RestController
@Slf4j
public class TodoController {
	@Autowired
	private TodoService todoService;

	@Autowired
	private DbThreadPool dbThreadPool;

	@Autowired
	private Jwt jwt;

	@PostMapping(value = "/api/todo/add")
	public Mono<String> add(@Valid @RequestBody Todo todo) {
		log.info("add: " + JsonUtil.toJson(todo));
		return Mono.just(todo)
				.publishOn(dbThreadPool.daoInstance())
				.map(t -> {
					if (Objects.isNull(todo.getGroupId())) {
						return Result.failWithMessage(ErrorCode.SYS_PARAMETER_ERROR, "必须指定任务组").toString();
					}
					todo.setCreateTime(System.currentTimeMillis());
					todo.setUpdateTime(System.currentTimeMillis());
					return todoService.addTodo(todo).toString();
				});
	}

	@PostMapping(value = "/api/todo/patch/add")
	public Mono<String> addPatch(ServerHttpRequest request,
			@Valid @RequestBody AddPatchRequest entity) {
		return Mono.just(entity)
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> {
					if (v.getTodoList().size() == 0) {
						return Result.failWithMessage(ErrorCode.SYS_PARAMETER_ERROR, "必须指定一个或多个任务").toString();
					}
					int maxTime = Optional.ofNullable(v.getMaxTime()).orElse(480);
					int minPriority = Optional.ofNullable(v.getMinPriority()).orElse(1);
					User user = jwt.getUser(request);
					// 初始化TodoList
					List<Todo> todoList = v.getTodoList();
					TodoGroup todoGroup = new TodoGroup();
					todoGroup.setId(todoList.get(0).getGroupId());
					todoGroup.setUid(user.getId());
					todoGroup.setMinPriority(minPriority);
					todoGroup.setMaxTime(maxTime);
					int totalTime = 0;
					for (Todo t : todoList) {
						totalTime += t.getEstimateTime();
					}
					todoGroup.setTotalTime(totalTime);
					todoGroup.setCreateTime(System.currentTimeMillis());
					todoGroup.setUpdateTime(System.currentTimeMillis());
					todoGroup.setStatus(TodoGroup.S_FINISHED);

					// 按优先级给todos排序
					todoList.sort((o1, o2) -> {
						if (o1.getPriority() < o2.getPriority()) {
							return 1;
						} else {
							return -1;
						}
					});
					return todoService.addPatch(todoGroup, todoList).toString();
				});
	}

	@GetMapping(value = "/api/todo/list")
	public Mono<String> search(ServerHttpRequest request,
			@RequestParam(value = "offset", defaultValue = "0") Integer offset,
			@RequestParam(value = "count", defaultValue = "10") Integer count) {
		return Mono.just(jwt.getUser(request))
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> {
					return Result.success().toString();
				});
	}
}
