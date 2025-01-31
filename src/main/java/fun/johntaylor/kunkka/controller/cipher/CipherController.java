package fun.johntaylor.kunkka.controller.cipher;

import fun.johntaylor.kunkka.component.redis.session.Session;
import fun.johntaylor.kunkka.component.thread.pool.DbThreadPool;
import fun.johntaylor.kunkka.entity.cipher.Cipher;
import fun.johntaylor.kunkka.entity.encrypt.cipher.EncryptCipher;
import fun.johntaylor.kunkka.entity.validation.Insert;
import fun.johntaylor.kunkka.entity.validation.Update;
import fun.johntaylor.kunkka.service.cipher.CipherService;
import fun.johntaylor.kunkka.utils.encrypt.EncryptUtil;
import fun.johntaylor.kunkka.utils.general.CopyUtil;
import fun.johntaylor.kunkka.utils.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author John
 * @Description CipherController
 * @Date 2020/8/20 2:29 PM
 **/
@RestController
public class CipherController {
	@Autowired
	private DbThreadPool dbThreadPool;

	@Autowired
	private CipherService cipherService;

	@Autowired
	private Session session;

	@PostMapping(value = "/api/cipher/add")
	public Mono<String> add(ServerHttpRequest request,
			@Validated(value = {Insert.class}) @RequestBody Cipher reqCipher) {
		return Mono.just(session.getUser(request))
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> {
					Cipher cipher = new Cipher();
					cipher.setUid(v.getId());
					cipher.setName(reqCipher.getName());
					cipher.setUserName(Optional.ofNullable(reqCipher.getUserName()).orElse(EncryptUtil.generateRandomString(false)));
					cipher.setPassword(EncryptUtil.generateRandomString(true));
					cipher.setSalt(EncryptUtil.genrateSalt());
					cipher.setEmail(reqCipher.getEmail());
					cipher.setPhoneNumber(reqCipher.getPhoneNumber());
					cipher.setLink(reqCipher.getLink());
					cipher.setCreateTime(System.currentTimeMillis());
					cipher.setUpdateTime(System.currentTimeMillis());
					cipher.setStatus(Cipher.S_NORMAL);
					return cipherService.add(cipher).toString();
				});
	}

	@PostMapping(value = "/api/cipher/update")
	public Mono<String> update(ServerHttpRequest request,
			@Validated(value = {Update.class}) @RequestBody Cipher reqCipher) {
		return Mono.just(session.getUser(request))
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> {
					Cipher cipher = new Cipher();
					cipher.setId(reqCipher.getId());
					cipher.setUid(v.getId());
					cipher.setName(reqCipher.getName());
					cipher.setUserName(reqCipher.getUserName());
					if (Objects.nonNull(reqCipher.getPassword())) {
						cipher.setPassword(EncryptUtil.generateRandomString(true));
						cipher.setSalt(EncryptUtil.genrateSalt());
					}
					cipher.setEmail(reqCipher.getEmail());
					cipher.setPhoneNumber(reqCipher.getPhoneNumber());
					cipher.setLink(reqCipher.getLink());
					cipher.setUpdateTime(System.currentTimeMillis());
					cipher.setStatus(reqCipher.getStatus());
					return cipherService.update(cipher).toString();
				});
	}

	@GetMapping(value = "/api/cipher/search")
	public Mono<String> search(ServerHttpRequest request,
			@RequestParam(value = "name") String name) {
		return Mono.just(session.getUser(request))
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> {
					List<Cipher> cipherList = cipherService.list(v.getId());
					List<EncryptCipher> encryptCipherList = cipherList.stream()
							.filter(c -> {
								if (StringUtils.isEmpty(name)) {
									return true;
								}
								return c.getName().contains(name);
							})
							.map(c -> {
								EncryptCipher encryptCipher = CopyUtil.copyWithSet(c, new EncryptCipher());
								encryptCipher.setPassword(EncryptUtil.encryptPassword(c.getPassword(), c.getSalt()));
								return encryptCipher;
							})
							.collect(Collectors.toList());
					return Result.success(encryptCipherList).toString();
				});
	}
}
