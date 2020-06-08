package fun.johntaylor.kunkka.service.todo;

import fun.johntaylor.kunkka.dao.todo.TodoDao;
import fun.johntaylor.kunkka.entity.todo.Todo;
import fun.johntaylor.kunkka.entity.todo.TodoList;
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

    // 根据背包问题方案解决
    public void addPatch(Integer minPriority, List<Todo> todos) {
        if (todos.size() > 0) {
            TodoList todoList = new TodoList();
            todoList.setId(todos.get(0).getListId());
            todoList.setMinPriority(minPriority);
            todoList.setCreateTime(System.currentTimeMillis());
            todoList.setUpdateTime(System.currentTimeMillis());
            todoDao.addData(todoList, todos);
        }
    }

    public void update(Long id) {
        todoDao.updateData(id);
    }
}
