package fun.johntaylor.kunkka.mapper.todo;

import fun.johntaylor.kunkka.entity.todo.TodoList;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;

import java.util.Map;

public interface TodoListMapper {
    String COLUMNS = "id, value, finish_value as finishValue, total_time as totalTime, max_time as maxTime, min_priority as minPriority, create_time as createTime, update_time as updateTime, status";

    @Select("select " + COLUMNS + " from t_todo_list where id=#{id}")
    TodoList select(Long id);

    @Insert("insert into t_todo_list(id, value, finish_value, total_time, max_time, min_priority, create_time, update_time, status) values(#{id}, #{value}, #{finishValue}, #{totalTime}, #{maxTime}, #{minPriority}, #{createTime}, #{updateTime}, #{status})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(TodoList todoList);

    int update(TodoList todoList);

    int updateIdempotent(Map<String, TodoList> params);

    @Delete("delete from TodoList where id=#{id}")
    int delete(Long id);

    @Insert("insert into t_todo_list(id, value, finish_value, total_time, max_time, min_priority, create_time, update_time, status) values(#{id}, #{value}, #{finishValue}, #{totalTime}, #{maxTime}, #{minPriority}, #{createTime}, #{updateTime}, #{status}) " +
            "on duplicate key update update_time = #{updateTime}, status = #{status}")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insertWithUpdate(TodoList todoList);
}
