package fun.johntaylor.kunkka.repository.mybatis.todo;

import fun.johntaylor.kunkka.entity.todo.TodoGroup;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Author John
 * @Description 任务组
 * @Date 2020/7/8 11:25 AM
 **/
@Repository
public interface TodoGroupMapper {
	String COLUMNS = "id, uid, value, finish_value as finishValue, total_time as totalTime, max_time as maxTime, min_priority as minPriority, create_time as createTime, update_time as updateTime, is_private as isPrivate, status";

	@Select("select " + COLUMNS + " from t_todo_group where id=#{id}")
	TodoGroup select(Long id);

	@Insert("insert into t_todo_group(id, uid, value, finish_value, total_time, max_time, min_priority, create_time, update_time, is_private, status) values(#{id}, #{uid}, #{value}, #{finishValue}, #{totalTime}, #{maxTime}, #{minPriority}, #{createTime}, #{updateTime}, #{isPrivate}, #{status})")
	@SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
	int insert(TodoGroup todoGroup);

	int update(TodoGroup todoGroup);

	int updateIdempotent(Map<String, TodoGroup> params);

	@Delete("delete from TodoGroup where id=#{id}")
	int delete(Long id);

	@Select("select " + COLUMNS + " from t_todo_group where uid=#{uid} and create_time>#{timeMillis} order by create_time ${sort} limit #{offset}, #{count}")
	List<TodoGroup> selectList(Long uid, Integer offset, Integer count, Long timeMillis, String sort);

	@Select("select count(1) from t_todo_group where uid=#{uid}")
	TodoGroup selectCountByUid(Long uid);
}
