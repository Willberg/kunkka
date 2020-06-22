package fun.johntaylor.kunkka.controller.todo;

import fun.johntaylor.kunkka.component.thread.pool.DbThreadPool;
import fun.johntaylor.kunkka.entity.todo.Todo;
import fun.johntaylor.kunkka.entity.todo.request.AddPatchRequest;
import fun.johntaylor.kunkka.service.todo.TodoService;
import fun.johntaylor.kunkka.utils.error.ErrorCode;
import fun.johntaylor.kunkka.utils.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
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

	@GetMapping(value = "/api/todo/add", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<String> add(@RequestParam("task") String task,
			@RequestParam("value") Integer value,
			@RequestParam("estimateTime") Integer estimateTime,
			@RequestParam("listId") Long listId,
			@RequestParam("priority") Integer priority) {
		log.info("add todo and todo list");
		Todo todo = new Todo();
		todo.setTask(task);
		todo.setValue(value);
		todo.setEstimateTime(estimateTime);
		todo.setListId(listId);
		todo.setPriority(priority);
//        todoService.add();
		return Mono.error(new RuntimeException("test error"));
	}

	@PostMapping(value = "/api/todo/patch/add", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<String> addPatch(@Valid @RequestBody AddPatchRequest entity) {
		return Mono.just(entity)
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> {
					int maxTime = Optional.ofNullable(v.getMaxTime()).orElse(480);
					int minPriority = Optional.ofNullable(v.getMinPriority()).orElse(1);
					todoService.addPatch(maxTime, minPriority, v.getTodos());
					return Result.success().toString();
				})
				.doOnError(e -> log.error(e.getMessage()))
				.onErrorReturn(Result.fail(ErrorCode.SYS_PARAMETER_ERROR).toString());
	}
}
