package fun.johntaylor.kunkka.dao.todo;

import fun.johntaylor.kunkka.entity.todo.Todo;
import fun.johntaylor.kunkka.entity.todo.TodoList;
import fun.johntaylor.kunkka.mapper.todo.TodoListMapper;
import fun.johntaylor.kunkka.mapper.todo.TodoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public class TodoDao {

    @Autowired
    private TodoListMapper todoListMapper;

    @Autowired
    private TodoMapper todoMapper;

    @Transactional
    public void addData(TodoList todoList, List<Todo> todos) {
        todoListMapper.insertWithUpdate(todoList);
        todos.forEach(t -> todoMapper.insert(t));
    }

    @Transactional
    public void updateData(Long id) {
        Todo todo = todoMapper.select(id);
        todo.setValue(id.intValue());
        todo.setUpdateTime(System.currentTimeMillis());
        todoMapper.update(todo);

        TodoList todoList = new TodoList();
        todoList.setId(todo.getListId());
        todoList.setUpdateTime(System.currentTimeMillis());
        todoList.setValue(50);
        todoListMapper.update(todoList);
    }
}
