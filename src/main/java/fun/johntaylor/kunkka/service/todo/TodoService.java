package fun.johntaylor.kunkka.service.todo;

import fun.johntaylor.kunkka.dao.todo.TodoDao;
import fun.johntaylor.kunkka.entity.todo.Todo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoService {

    @Autowired
    private TodoDao todoDao;

    public void add(Todo todo) {
//        todoDao.addData(todo);
    }

    public void addPatch(List<Todo> todos) {
        todoDao.addData(todos);
    }

    public void update(Long id) {
        todoDao.updateData(id);
    }
}
