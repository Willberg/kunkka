package fun.johntaylor.kunkka.repository.mybatis.cipher;

import fun.johntaylor.kunkka.entity.cipher.Cipher;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Author John
 * @Description CipherMapper
 * @Date 2020/8/20 11:13 AM
 **/
@Repository
public interface CipherMapper {
	String COLUMNS = "id, uid, name, user_name as userName, password, salt, email, phone_number as phoneNumber, link, create_time as createTime, update_time as updateTime, status";

	/**
	 * 查询密码器
	 * @param id
	 * @return EncryptCipher
	 */
	@Select("select " + COLUMNS + " from t_cipher where id=#{id}")
	Cipher select(Long id);

	/**
	 * 插入密码器
	 * @param cipher
	 * @return row
	 */
	@Insert("insert into t_cipher(id, uid, name, user_name, password, salt, email, phone_number, link, create_time, update_time, status) values(#{id}, #{uid}, #{name}, #{userName}, #{password}, #{salt}, #{email}, #{phoneNumber}, #{link}, #{createTime}, #{updateTime}, #{status})")
	@SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
	int insert(Cipher cipher);

	/**
	 * 更新密码器
	 * @param cipher
	 * @return row
	 */
	int update(Cipher cipher);

	/**
	 * 幂等更新
	 * @param params
	 * @return row
	 */
	int updateIdempotent(Map<String, Cipher> params);

	/**
	 * 删除密码器
	 * @param id
	 * @return row
	 */
	@Delete("delete from EncryptCipher where id=#{id}")
	int delete(Long id);

	@Select("select " + COLUMNS + " from t_cipher where uid=#{uid}")
	List<Cipher> list(Long uid);
}
