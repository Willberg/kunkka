package fun.johntaylor.kunkka.service.funds.impl;

import fun.johntaylor.kunkka.entity.funds.Funds;
import fun.johntaylor.kunkka.repository.mybatis.funds.FundsMapper;
import fun.johntaylor.kunkka.service.funds.FundsService;
import fun.johntaylor.kunkka.utils.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @Author John
 * @Description 资金Service
 * @Date 2020/8/13 8:36 PM
 **/
@Service
@Slf4j
public class FundsServiceImpl implements FundsService {
	@Autowired
	private FundsMapper fundsMapper;

	@Override
	public Result<Funds> add(Funds funds) {
		fundsMapper.insert(funds);
		return Result.success(funds);
	}

	@Override
	public Result<Funds> update(Funds funds) {
		Funds old = fundsMapper.select(funds.getId());
		if (Objects.isNull(old) || !funds.getUid().equals(old.getUid())) {
			return Result.failWithCustomMessage("无权操作");
		}
		fundsMapper.update(funds);
		return Result.success(funds);
	}

	@Override
	public List<Funds> listFunds(Integer type, Long uid, Long startTime, Long endTime) {
		return fundsMapper.searchListByType(type, uid, startTime, endTime);
	}

	@Override
	public List<Funds> list(Long uid, Long startTime, Long endTime) {
		return fundsMapper.searchList(uid, startTime, endTime);
	}

	@Override
	public Funds search(Long id) {
		return fundsMapper.select(id);
	}
}
