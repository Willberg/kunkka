package fun.johntaylor.kunkka.controller.todo;

import fun.johntaylor.kunkka.component.thread.pool.DbThreadPool;
import fun.johntaylor.kunkka.entity.todo.Todo;
import fun.johntaylor.kunkka.entity.todo.request.AddPatchRequest;
import fun.johntaylor.kunkka.entity.todo.request.Test;
import fun.johntaylor.kunkka.service.todo.TodoService;
import fun.johntaylor.kunkka.utils.error.ErrorCode;
import fun.johntaylor.kunkka.utils.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Optional;


@RestController
@Slf4j
public class TodoController {
	@Autowired
	private TodoService todoService;

	@Autowired
	private DbThreadPool dbThreadPool;

	@GetMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
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

	@PostMapping(value = "/patch/add", produces = MediaType.APPLICATION_JSON_VALUE)
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

	@PostMapping(value = "/test", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<String> test(@Valid @RequestBody Test test) {
		log.info("d");
//        todoService.update(id);
		if (test.getId().equals(1)) {
			throw new NumberFormatException("test");
		}
		return Mono.just(test)
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> {
					System.out.println(String.format("thread name: %s, pool: %s, id: %s", Thread.currentThread().getName(), Thread.currentThread().getThreadGroup(), Thread.currentThread().getId()));
					System.out.println(v.getId());
					System.out.println(Optional.ofNullable(v.getTest2List()).orElse(new ArrayList<>()).size());
//					todoService.test();
					return v;
				})
				.thenReturn(Result.success().toString());
	}

	@PostMapping(value = "/test2", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<String> test2(Test test) {
		log.info("d");
//        todoService.update(id);

		return Mono.just(test)
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> {
					System.out.println(String.format("thread name: %s, pool: %s, id: %s", Thread.currentThread().getName(), Thread.currentThread().getThreadGroup(), Thread.currentThread().getId()));
					System.out.println(v.getId());
					return v;
				})
				.thenReturn(Result.success().toString());
	}

	@GetMapping(value = "/test3", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<String> test3(Test test) {
		log.info("d");
//        todoService.update(id);

		return Mono.just(test)
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> {
					System.out.println(String.format("thread name: %s, pool: %s, id: %s", Thread.currentThread().getName(), Thread.currentThread().getThreadGroup(), Thread.currentThread().getId()));
					System.out.println(v.getId());
					return v;
				})
				.thenReturn(Result.success().toString());
	}

	@GetMapping(value = "/test4", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<String> test4(@RequestBody Mono<Test> test) {
		log.info("d");
//        todoService.update(id);

		return test
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> {
					System.out.println(String.format("thread name: %s, pool: %s, id: %s", Thread.currentThread().getName(), Thread.currentThread().getThreadGroup(), Thread.currentThread().getId()));
					System.out.println(v.getId());
					return v;
				})
				.thenReturn(Result.success().toString());
	}

	@PostMapping(value = "/test5", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<String> test5(@RequestBody Mono<Test> test) {
		log.info("d");
//        todoService.update(id);

		return test
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> {
					System.out.println(String.format("thread name: %s, pool: %s, id: %s", Thread.currentThread().getName(), Thread.currentThread().getThreadGroup(), Thread.currentThread().getId()));
					System.out.println(v.getId());
					return v;
				})
				.thenReturn(Result.success().toString());
	}
}
