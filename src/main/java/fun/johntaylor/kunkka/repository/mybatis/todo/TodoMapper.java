package fun.johntaylor.kunkka.repository.mybatis.todo;

import fun.johntaylor.kunkka.entity.todo.Todo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface TodoMapper {
	String COLUMNS = "id, task, value, estimate_time as estimateTime, reality_time as realityTime, list_id as listId, create_time as createTime, update_time as updateTime, priority, status";

	@Select("select " + COLUMNS + " from t_todo where id=#{id}")
	Todo select(Long id);

	@Insert("insert into t_todo(id, task, value, estimate_time, reality_time, list_id, create_time, update_time, priority, status) values(#{id}, #{task}, #{value}, #{estimateTime}, #{realityTime}, #{listId}, #{createTime}, #{updateTime}, #{priority}, #{status})")
	@SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
	int insert(Todo todo);

	int update(Todo todo);

	int updateIdempotent(Map<String, Todo> params);

	@Delete("delete from Todo where id=#{id}")
	int delete(Long id);

	@Select("select " + COLUMNS + " from t_todo where list_id=#{listId} for update")
	List<Todo> selectByListId(Long listId);

	@Insert("insert into t_todo(id, task, value, estimate_time, reality_time, list_id, create_time, update_time, priority, status) values(#{id}, #{task}, #{value}, #{estimateTime}, #{realityTime}, #{listId}, #{createTime}, #{updateTime}, #{priority}, #{status}) " +
			"on duplicate key update update_time = #{updateTime}, status = #{status}")
	@SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
	int insertWithUpdateStatus(Todo todo);
}
