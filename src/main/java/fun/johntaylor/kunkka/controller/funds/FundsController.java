package fun.johntaylor.kunkka.controller.funds;

import fun.johntaylor.kunkka.component.thread.pool.DbThreadPool;
import fun.johntaylor.kunkka.entity.funds.Funds;
import fun.johntaylor.kunkka.entity.validation.Insert;
import fun.johntaylor.kunkka.entity.validation.Update;
import fun.johntaylor.kunkka.service.funds.FundsService;
import fun.johntaylor.kunkka.utils.error.ErrorCode;
import fun.johntaylor.kunkka.utils.result.Result;
import fun.johntaylor.kunkka.utils.session.SessionUtil;
import fun.johntaylor.kunkka.utils.time.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.*;

/**
 * @Author John
 * @Description 资金controller
 * @Date 2020/8/13 8:35 PM
 **/
@RestController
@Slf4j
public class FundsController {
	@Autowired
	private DbThreadPool dbThreadPool;

	@Autowired
	private FundsService fundsService;

	/**
	 * 添加资金记录
	 * @param request
	 * @return Mono
	 */
	@PostMapping(value = "/api/funds/add")
	public Mono<String> add(ServerHttpRequest request,
			@Validated(value = {Insert.class}) @RequestBody Funds reqFunds) {
		return Mono.just(SessionUtil.getUser(request))
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> {
					if (reqFunds.getAmount() <= 0) {
						return Result.failWithMessage(ErrorCode.SYS_PARAMETER_ERROR, "金额必须大于0").toString();
					}
					Funds funds = new Funds();
					funds.setUid(v.getId());
					funds.setAmount(reqFunds.getAmount());
					funds.setCreateTime(System.currentTimeMillis());
					funds.setUpdateTime(System.currentTimeMillis());
					funds.setCategory(reqFunds.getCategory());
					funds.setType(reqFunds.getType());
					funds.setStatus(Funds.S_NORMAL);
					return fundsService.add(funds).toString();
				});
	}

	/**
	 * 更新资金记录
	 * @param request
	 * @return Mono
	 */
	@PostMapping(value = "/api/funds/update")
	public Mono<String> update(ServerHttpRequest request,
			@Validated(value = {Update.class}) @RequestBody Funds reqFunds) {
		return Mono.just(SessionUtil.getUser(request))
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> {
					if (Optional.ofNullable(reqFunds.getAmount()).orElse(0D) <= 0) {
						return Result.failWithMessage(ErrorCode.SYS_PARAMETER_ERROR, "金额必须大于0").toString();
					}
					Funds funds = new Funds();
					funds.setId(reqFunds.getId());
					funds.setUid(v.getId());
					funds.setAmount(reqFunds.getAmount());
					funds.setUpdateTime(System.currentTimeMillis());
					funds.setCategory(reqFunds.getCategory());
					funds.setType(reqFunds.getType());
					funds.setStatus(reqFunds.getStatus());
					return fundsService.update(funds).toString();
				});
	}

	/**
	 * 获取月支出流水
	 * @param request
	 * @param selectedMonth
	 * @return Mono
	 */
	@GetMapping(value = "/api/funds/disbursement/list")
	public Mono<String> listDisbursement(ServerHttpRequest request,
			@RequestParam(value = "selectedMonth") String selectedMonth) {
		return Mono.just(SessionUtil.getUser(request))
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> {
					Long[] timestamps = TimeUtil.getStartEndTimestamp(1, selectedMonth, "yyyy-MM");
					if (Objects.isNull(timestamps)) {
						return Result.failWithMessage(ErrorCode.SYS_PARAMETER_ERROR, "日期错误").toString();
					}

					List<Funds> fundsList = fundsService.listFunds(Funds.T_DISBURSEMENT, v.getId(), timestamps[0], timestamps[1]);
					Map<String, Double> map = new HashMap<>(9);
					Double total = 0D;
					for (Funds f : fundsList) {
						total += f.getAmount();
						Double amount = Optional.ofNullable(map.get(String.valueOf(f.getCategory()))).orElse(0D);
						amount += f.getAmount();
						map.put(String.valueOf(f.getCategory()), amount);
					}
					map.put("total", total);

					return Result.success(map).toString();
				});
	}

	/**
	 * 获取资金流水
	 * @param request
	 * @param selectedMonth
	 * @return Mono
	 */
	@GetMapping(value = "/api/funds/list")
	public Mono<String> list(ServerHttpRequest request,
			@RequestParam(value = "selectedMonth") String selectedMonth) {
		return Mono.just(SessionUtil.getUser(request))
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> {
					Long[] timestamps = TimeUtil.getStartEndTimestamp(1, selectedMonth, "yyyy-MM");
					if (Objects.isNull(timestamps)) {
						return Result.failWithMessage(ErrorCode.SYS_PARAMETER_ERROR, "日期错误").toString();
					}

					Map<String, Double> retMap = new HashMap<>(31);
					List<Funds> fundsList = fundsService.list(v.getId(), timestamps[0], timestamps[1]);
					for (Funds f : fundsList) {
						String dateStr = TimeUtil.getDateStrByTimestamp(f.getCreateTime(), "yyyy-MM-dd");
						Double amount = Optional.ofNullable(retMap.get(dateStr)).orElse(0D);
						if (Funds.T_DISBURSEMENT.equals(f.getType())) {
							amount -= f.getAmount();
						} else {
							amount += f.getAmount();
						}
						retMap.put(dateStr, amount);
					}

					return Result.success(retMap).toString();
				});
	}


	/**
	 * 查询资金记录
	 * @param request
	 * @return
	 */
	@GetMapping(value = "/api/funds/select")
	public Mono<String> select(ServerHttpRequest request,
			@RequestParam(value = "id") Long id) {
		return Mono.just(SessionUtil.getUser(request))
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> {
					Funds funds = fundsService.search(id);
					if (!v.getId().equals(funds.getUid())) {
						return Result.failWithCustomMessage("无权操作").toString();
					}
					return Result.success().toString();
				});
	}
}
