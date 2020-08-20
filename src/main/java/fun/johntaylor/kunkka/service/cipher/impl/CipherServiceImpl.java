package fun.johntaylor.kunkka.service.cipher.impl;

import fun.johntaylor.kunkka.entity.cipher.Cipher;
import fun.johntaylor.kunkka.entity.encrypt.cipher.EncryptCipher;
import fun.johntaylor.kunkka.repository.mybatis.cipher.CipherMapper;
import fun.johntaylor.kunkka.service.cipher.CipherService;
import fun.johntaylor.kunkka.utils.encrypt.EncryptUtil;
import fun.johntaylor.kunkka.utils.general.CopyUtil;
import fun.johntaylor.kunkka.utils.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author John
 * @Description CipherServiceImpl
 * @Date 2020/8/20 11:19 AM
 **/
@Service
public class CipherServiceImpl implements CipherService {
	@Autowired
	private CipherMapper cipherMapper;

	@Override
	public Result<EncryptCipher> add(Cipher cipher) {
		cipherMapper.insert(cipher);
		EncryptCipher encryptCipher = CopyUtil.copyWithSet(cipher, new EncryptCipher());
		encryptCipher.setPassword(EncryptUtil.encryptPassword(cipher.getPassword(), cipher.getSalt()));
		return Result.success(encryptCipher);
	}

	@Override
	public Result<EncryptCipher> update(Cipher cipher) {
		cipherMapper.update(cipher);
		Cipher retCipher = cipherMapper.select(cipher.getId());
		EncryptCipher encryptCipher = CopyUtil.copyWithSet(retCipher, new EncryptCipher());
		encryptCipher.setPassword(EncryptUtil.encryptPassword(retCipher.getPassword(), retCipher.getSalt()));
		return Result.success(encryptCipher);
	}

	@Override
	public List<Cipher> list(Long uid) {
		return cipherMapper.list(uid);
	}
}
