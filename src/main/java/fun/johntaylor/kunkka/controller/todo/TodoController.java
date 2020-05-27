package fun.johntaylor.kunkka.controller.todo;

import fun.johntaylor.kunkka.service.todo.TodoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@Slf4j
public class TodoController {
    @Autowired
    private TodoService todoService;

    @RequestMapping(value = "/add", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Mono<String> add() {
        log.info("d");
        todoService.add();
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
