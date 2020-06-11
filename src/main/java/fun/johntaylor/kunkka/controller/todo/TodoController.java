package fun.johntaylor.kunkka.controller.todo;

import com.google.gson.reflect.TypeToken;
import fun.johntaylor.kunkka.component.ThreadPoolComponent;
import fun.johntaylor.kunkka.entity.todo.Todo;
import fun.johntaylor.kunkka.service.todo.TodoService;
import fun.johntaylor.kunkka.utils.error.ErrorCode;
import fun.johntaylor.kunkka.utils.json.JsonUtil;
import fun.johntaylor.kunkka.utils.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;


@RestController
@Slf4j
public class TodoController {
    @Autowired
    private TodoService todoService;

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
    public Mono<String> addPatch(
            Integer maxTime,
            Integer minPriority,
            String todoStr) {
        return Mono
                .defer(() -> Mono.just(JsonUtil.fromJson(todoStr, new TypeToken<List<Todo>>() {
                }.getType())))
                .publishOn(ThreadPoolComponent.daoThreadPool())
                .map(v -> {
                    System.out.println("todo:" + ((List<Todo>) v).size());
                    int validMaxTime = Optional.ofNullable(maxTime).orElse(480);
                    int validMinPriority = Optional.ofNullable(minPriority).orElse(1);
                    todoService.addPatch(validMaxTime, validMinPriority, (List<Todo>) v);
                    return Result.success().toString();
                })
                .doOnError(e -> log.error(e.getMessage()))
                .onErrorReturn(Result.fail(ErrorCode.SYS_PARAMETER_ERROR).toString());
    }

    @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> update(Long id) {
        log.info("d");
//        todoService.update(id);
        return Mono.just("hello");
    }
}
