package fun.johntaylor.kunkka.controller.todo;

import fun.johntaylor.kunkka.component.redis.session.Session;
import fun.johntaylor.kunkka.component.thread.pool.DbThreadPool;
import fun.johntaylor.kunkka.entity.todo.Todo;
import fun.johntaylor.kunkka.entity.todo.TodoGroup;
import fun.johntaylor.kunkka.entity.todo.request.AddPatchRequest;
import fun.johntaylor.kunkka.entity.todo.request.TodoGroupRequest;
import fun.johntaylor.kunkka.entity.user.User;
import fun.johntaylor.kunkka.entity.validation.todo.InsertPatchTodo;
import fun.johntaylor.kunkka.repository.mybatis.todo.TodoGroupMapper;
import fun.johntaylor.kunkka.service.todo.TodoService;
import fun.johntaylor.kunkka.utils.error.ErrorCode;
import fun.johntaylor.kunkka.utils.result.Result;
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

	@Autowired
	private TodoGroupMapper todoGroupMapper;

	@Autowired
	private Session session;

	/**
	 * 添加任务，没有任务组，自动创建任务组
	 * @param request
	 * @param entity
	 * @return 任务组
	 */
	@PostMapping(value = "/api/todo/patch/add")
	public Mono<String> addPatch(ServerHttpRequest request,
			@Validated(value = {InsertPatchTodo.class}) @RequestBody AddPatchRequest entity) {
		return Mono.just(entity)
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> {
					if (v.getTodoList().size() == 0) {
						return Result.failWithMessage(ErrorCode.SYS_PARAMETER_ERROR, "必须指定一个或多个任务").toString();
					}
					User user = session.getUser(request);
					// 初始化TodoList
					List<Todo> todoList = v.getTodoList();
					TodoGroup todoGroup = new TodoGroup();
					todoGroup.setId(entity.getGroupId());
					// 用于校验存在任务组时，是否是给自己的任务组添加任务
					todoGroup.setUid(user.getId());
					if (Objects.isNull(entity.getGroupId())) {
						todoGroup.setMinPriority(v.getMinPriority());
						todoGroup.setMaxTime(v.getMaxTime());
						todoGroup.setCreateTime(System.currentTimeMillis());
						todoGroup.setIsPrivate(false);
					}
					todoGroup.setUpdateTime(System.currentTimeMillis());
					return todoService.addPatch(todoGroup, todoList).toString();
				});
	}

	/**
	 * 查询任务组列表
	 * @param request
	 * @param offset
	 * @param count
	 * @param startTime
	 * @param sort
	 * @return 任务组列表
	 */
	@GetMapping(value = "/api/todo/group/list")
	public Mono<String> search(ServerHttpRequest request,
			@RequestParam(value = "offset", defaultValue = "0") Integer offset,
			@RequestParam(value = "count", defaultValue = "10") Integer count,
			@RequestParam(value = "startTime", defaultValue = "0") Long startTime,
			@RequestParam(value = "endTime", defaultValue = "0") Long endTime,
			@RequestParam(value = "sort", defaultValue = "desc") String sort,
			@RequestParam(value = "status", defaultValue = "0") Integer status) {
		return Mono.just(session.getUser(request))
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> {
					Long endTimeVal = endTime == 0 ? System.currentTimeMillis() + 24 * 3600 : endTime;
					Integer statusVal = status == 0 ? null : status;
					return todoService.searchTodoGroupList(v.getId(), offset, count, statusVal, startTime, endTimeVal, sort).toString();
				});
	}

	/**
	 * 查询任务列表
	 * @param request
	 * @param groupId
	 * @return 任务组列表
	 */
	@GetMapping(value = "/api/todo/list")
	public Mono<String> todoList(ServerHttpRequest request,
			@RequestParam(value = "groupId") Long groupId) {
		return Mono.just(session.getUser(request))
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> todoService.searchTodoListByUidGroupId(v.getId(), groupId).toString());
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
		return Mono.just(todo)
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> {
					if (Todo.S_INITIAL.equals(v.getStatus())) {
						return Result.failWithCustomMessage("不能改成初始状态").toString();
					}
					return todoService.updateTodo(v).toString();
				});
	}

	/**
	 * 更新任务组
	 * @param todoGroupRequest
	 * @return 任务组列表
	 */
	@PostMapping(value = "/api/todo/group/update")
	public Mono<String> updateTodoGroup(@Valid @RequestBody TodoGroupRequest todoGroupRequest) {
		return Mono.just(todoGroupRequest)
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> {
					TodoGroup todoGroup = new TodoGroup();
					todoGroup.setId(v.getId());
					todoGroup.setMaxTime(v.getMaxTime());
					todoGroup.setMinPriority(v.getMinPriority());
					todoGroup.setIsPrivate(v.getIsPrivate());
					return todoService.updateTodoGroup(todoGroup).toString();
				});
	}

	/**
	 * 获取任务组数量
	 * @param request
	 * @return 任务组数量
	 */
	@GetMapping(value = "/api/todo/group/total")
	public Mono<String> countTodoGroup(ServerHttpRequest request,
			@RequestParam(value = "startTime", defaultValue = "0") Long startTime,
			@RequestParam(value = "endTime") Long endTime,
			@RequestParam(value = "status") Integer status) {
		return Mono.just(session.getUser(request))
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> Result.success(todoGroupMapper.selectCountByUid(v.getId(), status, startTime, endTime)).toString());
	}
}
