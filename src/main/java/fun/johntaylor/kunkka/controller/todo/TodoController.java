package fun.johntaylor.kunkka.controller.todo;

import fun.johntaylor.kunkka.component.thread.pool.DbThreadPool;
import fun.johntaylor.kunkka.entity.todo.Todo;
import fun.johntaylor.kunkka.entity.todo.TodoGroup;
import fun.johntaylor.kunkka.entity.todo.request.AddPatchRequest;
import fun.johntaylor.kunkka.entity.user.User;
import fun.johntaylor.kunkka.service.todo.TodoService;
import fun.johntaylor.kunkka.utils.error.ErrorCode;
import fun.johntaylor.kunkka.utils.result.Result;
import fun.johntaylor.kunkka.utils.session.SessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

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
			@Valid @RequestBody AddPatchRequest entity) {
		return Mono.just(entity)
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> {
					if (v.getTodoList().size() == 0) {
						return Result.failWithMessage(ErrorCode.SYS_PARAMETER_ERROR, "必须指定一个或多个任务").toString();
					}
					int maxTime = Optional.ofNullable(v.getMaxTime()).orElse(480);
					int minPriority = Optional.ofNullable(v.getMinPriority()).orElse(1);
					User user = SessionUtil.getUser(request);
					// 初始化TodoList
					List<Todo> todoList = v.getTodoList();
					TodoGroup todoGroup = new TodoGroup();
					todoGroup.setId(todoList.get(0).getGroupId());
					todoGroup.setUid(user.getId());
					todoGroup.setMinPriority(minPriority);
					todoGroup.setMaxTime(maxTime);
					todoGroup.setCreateTime(System.currentTimeMillis());
					todoGroup.setUpdateTime(System.currentTimeMillis());
					todoGroup.setIsPrivate(false);

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
			@RequestParam(value = "sort", defaultValue = "asc") String sort) {
		return Mono.just(SessionUtil.getUser(request))
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> {
					return todoService.searchTodoGroupList(v.getId(), offset, count, timeMillis, sort).toString();
				});
	}
}
