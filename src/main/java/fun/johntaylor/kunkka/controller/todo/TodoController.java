package fun.johntaylor.kunkka.controller.todo;

import fun.johntaylor.kunkka.component.thread.pool.DbThreadPool;
import fun.johntaylor.kunkka.entity.todo.Todo;
import fun.johntaylor.kunkka.entity.todo.TodoGroup;
import fun.johntaylor.kunkka.entity.todo.request.AddPatchRequest;
import fun.johntaylor.kunkka.entity.user.User;
import fun.johntaylor.kunkka.entity.validation.Insert;
import fun.johntaylor.kunkka.service.todo.TodoService;
import fun.johntaylor.kunkka.utils.error.ErrorCode;
import fun.johntaylor.kunkka.utils.result.Result;
import fun.johntaylor.kunkka.utils.session.SessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * @Author John
 * @Description todo服务0
 * @Date 2020/6/22 8:08 PM
 **/
@RestController
@Slf4j
public class TodoController {
	@Autowired
	private TodoService todoService;

	@Autowired
	private DbThreadPool dbThreadPool;

	/**
	 * 添加任务，没有任务组，自动创建任务组
	 * @param request
	 * @param entity
	 * @return 任务组
	 */
	@PostMapping(value = "/api/todo/patch/add")
	public Mono<String> addPatch(ServerHttpRequest request,
			@Validated(value = {Insert.class}) @RequestBody AddPatchRequest entity) {
		return Mono.just(entity)
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> {
					if (v.getTodoList().size() == 0) {
						return Result.failWithMessage(ErrorCode.SYS_PARAMETER_ERROR, "必须指定一个或多个任务").toString();
					}
					User user = SessionUtil.getUser(request);
					// 初始化TodoList
					List<Todo> todoList = v.getTodoList();
					TodoGroup todoGroup = new TodoGroup();
					todoGroup.setId(todoList.get(0).getGroupId());
					todoGroup.setUid(user.getId());
					todoGroup.setMinPriority(v.getMinPriority());
					todoGroup.setMaxTime(v.getMaxTime());
					todoGroup.setCreateTime(System.currentTimeMillis());
					todoGroup.setUpdateTime(System.currentTimeMillis());
					todoGroup.setIsPrivate(false);
					return todoService.addPatch(todoGroup, todoList).toString();
				});
	}

	/**
	 * 查询任务组
	 * @param request
	 * @param offset
	 * @param count
	 * @param timeMillis
	 * @param sort
	 * @return 任务组列表
	 */
	@GetMapping(value = "/api/todo/group/list")
	public Mono<String> search(ServerHttpRequest request,
			@RequestParam(value = "offset", defaultValue = "0") Integer offset,
			@RequestParam(value = "count", defaultValue = "10") Integer count,
			@RequestParam(value = "timeMillis", defaultValue = "0") Long timeMillis,
			@RequestParam(value = "sort", defaultValue = "desc") String sort) {
		return Mono.just(SessionUtil.getUser(request))
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> todoService.searchTodoGroupList(v.getId(), offset, count, timeMillis, sort).toString());
	}

	/**
	 * 更新任务，如果改成待处理，必须重新进行计算
	 * @param request
	 * @param todo
	 * @return 任务组列表
	 */
	@PostMapping(value = "/api/todo/update")
	public Mono<String> update(ServerHttpRequest request,
			@Valid @RequestBody Todo todo) {
		return Mono.just(SessionUtil.getUser(request))
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> {
					if (Objects.isNull(todo.getEstimateTime())) {
						return Result.failWithMessage(ErrorCode.SYS_CUSTOMIZE_ERROR, "请填写估计时间").toString();
					}
					return Result.success(todoService.updateTodo(todo)).toString();
				});
	}
}
