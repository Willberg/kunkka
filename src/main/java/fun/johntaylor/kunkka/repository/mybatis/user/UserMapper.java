package fun.johntaylor.kunkka.repository.mybatis.user;

import fun.johntaylor.kunkka.entity.user.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface UserMapper {
	String COLUMNS = "id, user_name as userName, password, phone_number as phoneNumber, email, alias, create_time as createTime, update_time as updateTime, type, status";

	@Select("select " + COLUMNS + " from t_user where id=#{id}")
	User select(Long id);

	@Insert("insert into t_user(id, user_name, password, phone_number, email, alias, create_time, update_time, type, status) values(#{id}, #{userName}, #{password}, #{phoneNumber}, #{email}, #{alias}, #{createTime}, #{updateTime}, #{type}, #{status})")
	@SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
	int insert(User user);

	int update(User user);

	int updateIdempotent(Map<String, User> params);

	@Delete("delete from User where id=#{id}")
	int delete(Long id);
}
