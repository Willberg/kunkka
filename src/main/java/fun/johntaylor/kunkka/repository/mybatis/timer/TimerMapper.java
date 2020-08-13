package fun.johntaylor.kunkka.repository.mybatis.timer;

import fun.johntaylor.kunkka.entity.timer.Timer;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Author John
 * @Description 计时器
 * @Date 2020/7/8 11:25 AM
 **/
@Repository
public interface TimerMapper {
	String COLUMNS = "id, uid, create_time as createTime, type, related_id as relatedId, status";

	@Select("select " + COLUMNS + " from t_timer where id=#{id}")
	Timer select(Long id);

	@Insert("insert into t_timer(id, uid, create_time, type, related_id, status) values(#{id}, #{uid}, #{createTime}, #{type}, #{relatedId}, #{status})")
	@SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
	int insert(Timer timer);

	int update(Timer timer);

	int updateIdempotent(Map<String, Timer> params);

	@Delete("delete from Timer where id=#{id}")
	int delete(Long id);

	@Select("select " + COLUMNS + " from t_timer where uid=#{uid} and create_time >=#{startTime} and create_time<#{endTime} order by id asc")
	List<Timer> list(Long uid, Long startTime, Long endTime);

	@Select("select " + COLUMNS + " from t_timer where uid=#{uid} order by id desc limit 1")
	Timer searchLastOne(Long uid);

	@Select("select " + COLUMNS + " from t_timer where id=#{relatedId}")
	Timer searchRelatedOne(Long relatedId);
}
