package fun.johntaylor.kunkka.repository.mybatis.todo;

import fun.johntaylor.kunkka.entity.todo.TodoGroup;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * @Author John
 * @Description 任务组
 * @Date 2020/7/8 11:25 AM
 **/
@Repository
public interface TodoGroupMapper {
	String COLUMNS = "id, uid, value, finish_value as finishValue, total_time as totalTime, max_time as maxTime, min_priority as minPriority, create_time as createTime, update_time as updateTime, status";

	@Select("select " + COLUMNS + " from t_todo_group where id=#{id}")
	TodoGroup select(Long id);

	@Insert("insert into t_todo_group(id, uid, value, finish_value, total_time, max_time, min_priority, create_time, update_time, status) values(#{id}, #{uid}, #{value}, #{finishValue}, #{totalTime}, #{maxTime}, #{minPriority}, #{createTime}, #{updateTime}, #{status})")
	@SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
	int insert(TodoGroup todoGroup);

	int update(TodoGroup todoGroup);

	int updateIdempotent(Map<String, TodoGroup> params);

	@Delete("delete from TodoGroup where id=#{id}")
	int delete(Long id);

	@Insert("insert into t_todo_group(id, uid, value, finish_value, total_time, max_time, min_priority, create_time, update_time, status) values(#{id}, #{uid}, #{value}, #{finishValue}, #{totalTime}, #{maxTime}, #{minPriority}, #{createTime}, #{updateTime}, #{status}) " +
			"on duplicate key update update_time = #{updateTime}, status = #{status}")
	@SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
	int insertWithUpdate(TodoGroup todoGroup);
}
