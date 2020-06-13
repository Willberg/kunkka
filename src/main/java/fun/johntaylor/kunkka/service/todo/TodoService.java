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

    public void addPatch(Integer maxTime, Integer minPriority, List<Todo> todos) {
        if (todos.size() == 0) {
            return;
        }

        // 初始化TodoList
        TodoList todoList = new TodoList();
        todoList.setId(todos.get(0).getListId());
        todoList.setMinPriority(minPriority);
        todoList.setMaxTime(maxTime);
        int totalTime = 0;
        for (Todo t : todos) {
            totalTime += t.getEstimateTime();
        }
        todoList.setTotalTime(totalTime);
        todoList.setCreateTime(System.currentTimeMillis());
        todoList.setUpdateTime(System.currentTimeMillis());
        todoList.setStatus(TodoList.S_FINISHED);
        todoDao.addData(todoList, todos);
    }

    public void update(Long id) {
        todoDao.updateData(id);
    }
}
