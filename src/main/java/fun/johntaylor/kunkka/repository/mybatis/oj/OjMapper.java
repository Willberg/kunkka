package fun.johntaylor.kunkka.repository.mybatis.oj;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.springframework.stereotype.Repository;

import fun.johntaylor.kunkka.entity.oj.Oj;

/**
 * @Author john
 * @Description OjMapper
 * @Date 2022/8/26 下午10:12
 */
@Repository
public interface OjMapper {
    String COLUMNS =
        "id, pid, uid, name, difficulty, oj_type as ojType, type, pre_time as preTime, use_time as useTime, standalone, study, link, create_time as createTime, update_time as updateTime, status";

    @Select("select " + COLUMNS + " from t_oj where id=#{id}")
    Oj select(Long id);

    @Insert("insert into t_oj(id, pid, uid, name, difficulty, oj_type, type, pre_time, use_time, standalone, study, link, create_time, update_time, status) values(#{id}, #{pid}, #{uid}, #{name}, #{difficulty}, #{ojType}, #{type}, #{preTime}, #{useTime}, #{standalone}, #{study}, #{link}, #{createTime}, #{updateTime}, #{status})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    int insert(Oj oj);

    int update(Oj oj);

    int updateIdempotent(Map<String, Oj> params);

    @Delete("delete from Oj where id=#{id}")
    int delete(Long id);

    List<Oj> searchListByUidTime(Long uid, Integer offset, Integer count, Long begin, Long end);

    int countByUidTime(Long uid, Long begin, Long end);

    @Select("select " + COLUMNS + " from t_oj where uid=#{uid} and pid = #{pid} and oj_type = #{ojType}")
    List<Oj> searchListByUidPidOjTye(Long uid, Long pid, Integer ojType);
}
