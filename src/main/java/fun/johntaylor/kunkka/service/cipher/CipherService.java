package fun.johntaylor.kunkka.service.cipher;

import fun.johntaylor.kunkka.entity.cipher.Cipher;
import fun.johntaylor.kunkka.entity.encrypt.cipher.EncryptCipher;
import fun.johntaylor.kunkka.utils.result.Result;

import java.util.List;

/**
 * @Author John
 * @Description CipherService
 * @Date 2020/8/20 11:18 AM
 **/
public interface CipherService {
	/**
	 * 返回插入的Cipher
	 * @param cipher
	 * @return Result<Cipher>
	 */
	Result<EncryptCipher> add(Cipher cipher);

	/**
	 * 返回更新的Cipher
	 * @param cipher
	 * @return Result<EncryptCipher>
	 */
	Result<EncryptCipher> update(Cipher cipher);

	/**
	 * 返回用户所有Cipher
	 * @param uid
	 * @return List<Cipher>
	 */
	List<Cipher> list(Long uid);
}
