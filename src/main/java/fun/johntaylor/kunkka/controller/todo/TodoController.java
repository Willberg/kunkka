package fun.johntaylor.kunkka.controller.todo;

import com.google.gson.reflect.TypeToken;
import fun.johntaylor.kunkka.entity.todo.Todo;
import fun.johntaylor.kunkka.service.todo.TodoService;
import fun.johntaylor.kunkka.utils.json.JsonUtil;
import fun.johntaylor.kunkka.utils.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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
        return Mono.just("hello");
    }

    @RequestMapping(value = "/patch/add", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Mono<String> addPatch(@RequestParam("todoStr") String todoStr) {
        log.info("patch add todo and todo list");
        try {
            List<Todo> todos = JsonUtil.fromJson(todoStr, new TypeToken<List<Todo>>() {
            }.getType());
            todoService.addPatch(todos);
        } catch (Exception e) {
            log.error(e.getMessage());

        }

        return Mono.just("hello");
    }

    @RequestMapping(value = "/update", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Mono<String> update(@RequestParam(value = "id") Long id) {
        log.info("d");
        todoService.update(id);
        return Mono.just("hello");
    }
}
