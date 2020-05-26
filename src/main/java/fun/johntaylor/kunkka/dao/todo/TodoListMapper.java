package fun.johntaylor.kunkka.dao.todo;

import fun.johntaylor.kunkka.entity.todo.TodoList;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TodoListMapper {
    List<TodoList> findAll();

    @Insert("insert into t_todo_list")
    int insert(TodoList todoList);
}
