package fun.johntaylor.kunkka.controller.todo;

import com.google.gson.reflect.TypeToken;
import fun.johntaylor.kunkka.entity.todo.Todo;
import fun.johntaylor.kunkka.service.todo.TodoService;
import fun.johntaylor.kunkka.utils.error.ErrorCode;
import fun.johntaylor.kunkka.utils.json.JsonUtil;
import fun.johntaylor.kunkka.utils.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;


@RestController
@Slf4j
public class TodoController {
    @Autowired
    private TodoService todoService;

    @RequestMapping(value = "/add", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
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

    @RequestMapping(value = "/patch/add", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Mono<String> addPatch(
            @RequestParam(value = "minPriority", defaultValue = "1") Integer minPriority,
            @RequestParam("todoStr") String todoStr) {
        log.info("patch add todo and todo list");
        return Mono
                .defer(() -> Mono.just(JsonUtil.fromJson(todoStr, new TypeToken<List<Todo>>() {
                }.getType())))
                .publishOn(Schedulers.newElastic("patch-pool"))
                .map(v -> {
                    todoService.addPatch(minPriority, (List<Todo>) v);
                    return Result.success().toString();
                })
                .doOnError(e -> log.error(e.getMessage()))
                .onErrorReturn(Result.fail(ErrorCode.SYS_PARAMETER_ERROR).toString());
    }

    @RequestMapping(value = "/update", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Mono<String> update(@RequestParam(value = "id") Long id) {
        log.info("d");
        todoService.update(id);
        return Mono.just("hello");
    }
}
