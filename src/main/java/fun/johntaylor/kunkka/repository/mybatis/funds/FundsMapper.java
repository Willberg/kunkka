package fun.johntaylor.kunkka.repository.mybatis.funds;

import fun.johntaylor.kunkka.entity.funds.Funds;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface FundsMapper {
	String COLUMNS = "id, uid, amount, create_time as createTime, update_time as updateTime, category, type, status";

	@Select("select " + COLUMNS + " from t_funds where id=#{id}")
	Funds select(Long id);

	@Insert("insert into t_funds(id, uid, amount, create_time, update_time, category, type, status) values(#{id}, #{uid}, #{amount}, #{createTime}, #{updateTime}, #{category}, #{type}, #{status})")
	@SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
	int insert(Funds funds);

	int update(Funds funds);

	int updateIdempotent(Map<String, Funds> params);

	@Delete("delete from Funds where id=#{id}")
	int delete(Long id);
}
