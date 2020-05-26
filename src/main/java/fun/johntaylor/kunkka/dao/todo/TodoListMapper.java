package fun.johntaylor.kunkka.dao.todo;

import fun.johntaylor.kunkka.entity.todo.TodoList;
import org.apache.ibatis.annotations.*;

import java.util.Map;

@Mapper
public interface TodoListMapper {
    String COLUMNS = "id, value, finish_value as finishValue, total_time as totalTime, create_time as createTime, update_time as updateTime, status";

    @Select("select " + COLUMNS + " from t_todo_list where id=#{id}")
    TodoList select(Long id);

    @Insert("insert into t_todo_list(id, value, finish_value, total_time, create_time, update_time, status) values(#{id}, #{value}, #{finishValue}, #{totalTime}, #{createTime}, #{updateTime}, #{status})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(TodoList todoList);

    int update(TodoList todoList);

    int updateIdempotent(Map<String, TodoList> params);

    @Delete("delete from TodoList where id=#{id}")
    int delete(Long id);
}
