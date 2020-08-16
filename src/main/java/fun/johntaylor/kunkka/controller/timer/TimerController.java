package fun.johntaylor.kunkka.controller.timer;

import fun.johntaylor.kunkka.component.thread.pool.DbThreadPool;
import fun.johntaylor.kunkka.entity.timer.Timer;
import fun.johntaylor.kunkka.entity.validation.Insert;
import fun.johntaylor.kunkka.entity.validation.Update;
import fun.johntaylor.kunkka.service.timer.TimerService;
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

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * @Author John
 * @Description 计时器
 * @Date 2020/6/22 8:08 PM
 **/
@RestController
@Slf4j
public class TimerController {
	@Autowired
	private DbThreadPool dbThreadPool;

	@Autowired
	private TimerService timerService;

	/**
	 * 添加计时
	 * @param request
	 * @return
	 */
	@PostMapping(value = "/api/timer/add")
	public Mono<String> add(ServerHttpRequest request,
			@Validated(value = {Insert.class}) @RequestBody Timer reqTimer) {
		return Mono.just(SessionUtil.getUser(request))
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> {
					Timer t = new Timer();
					t.setUid(v.getId());
					t.setCreateTime(System.currentTimeMillis());
					t.setUpdateTime(System.currentTimeMillis());
					t.setType(reqTimer.getType());
					t.setRelatedId(reqTimer.getRelatedId());
					t.setStatus(reqTimer.getStatus());
					return timerService.add(t).toString();
				});
	}

	/**
	 * 更新计时
	 * @param request
	 * @return
	 */
	@PostMapping(value = "/api/timer/update")
	public Mono<String> update(ServerHttpRequest request,
			@Validated(value = {Update.class}) @RequestBody Timer reqTimer) {
		return Mono.just(SessionUtil.getUser(request))
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> {
					reqTimer.setUid(v.getId());
					reqTimer.setUpdateTime(System.currentTimeMillis());
					return timerService.update(reqTimer).toString();
				});
	}

	/**
	 * 查询计时
	 * @param request
	 * @param selectedMonth
	 * @return Mono
	 */
	@GetMapping(value = "/api/timer/list")
	public Mono<String> select(ServerHttpRequest request,
			@RequestParam(value = "selectedMonth") String selectedMonth) {
		return Mono.just(SessionUtil.getUser(request))
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> {
					LocalDate startLocalDate = TimeUtil.getLocalDate(selectedMonth, "yyyy-MM");
					if (Objects.isNull(startLocalDate)) {
						return Result.failWithMessage(ErrorCode.SYS_PARAMETER_ERROR, "日期错误").toString();
					}
					startLocalDate = startLocalDate.atStartOfDay().toLocalDate();
					long startTime = TimeUtil.getTimestampByLocalDate(startLocalDate);
					long endTime = TimeUtil.getTimestampByLocalDate(startLocalDate.plus(1, ChronoUnit.MONTHS));
					List<Timer> timerList = timerService.list(v.getId(), startTime, endTime);
					//去掉最后一个开始但还未关闭的计时器
					if (timerList.size() > 0 && !Timer.S_CLOSED.equals(timerList.get(timerList.size() - 1).getStatus())) {
						timerList.remove(timerList.size() - 1);
					}

					long start = 0;
					int days = startLocalDate.plus(1, ChronoUnit.MONTHS).getDayOfYear() - startLocalDate.getDayOfYear();
					Map<String, Map<Integer, Long>> map = new HashMap<>(days);
					for (Timer t : timerList) {
						String dateStr = TimeUtil.getDateStrByTimestamp(t.getCreateTime(), "yyyy-MM-dd");
						Map<Integer, Long> detail = map.get(dateStr);
						if (Objects.isNull(detail)) {
							detail = new HashMap<>(6);
							detail.put(Timer.T_WORK, 0L);
							detail.put(Timer.T_EAT, 0L);
							detail.put(Timer.T_ENTERTAINMENT, 0L);
							detail.put(Timer.T_SLEEP, 0L);
							detail.put(Timer.T_STUDY, 0L);
							map.put(dateStr, detail);
						}

						if (Timer.S_OPEN.equals(t.getStatus())) {
							start = t.getCreateTime() / 1000;
						} else {
							long diff = t.getCreateTime() / 1000 - start;
							long old = detail.get(t.getType());
							detail.put(t.getType(), old + diff);
						}
					}


					long total = 24 * 60 * 60;
					long defaultSleepTime = 75 * 6 * 60;
					long defaultEatTime = 25 * 6 * 60;
					map.keySet().forEach(k -> {
						Map<Integer, Long> detail = map.get(k);
						if (detail.get(Timer.T_EAT) == 0) {
							detail.put(Timer.T_EAT, defaultEatTime);
						}

						if (detail.get(Timer.T_SLEEP) == 0) {
							detail.put(Timer.T_SLEEP, defaultSleepTime);
						}

						long useTime = 0;
						for (Long dv : detail.values()) {
							useTime += dv;
						}
						detail.put(Timer.T_UNKNOWN, total - useTime);
					});

					Map<String, Map<Integer, Long>> retMap = new LinkedHashMap<>(days);
					for (int day = 0; day < days; day++) {
						String dateStr = TimeUtil.getDateStrByLocalDate(startLocalDate.plusDays(day), "yyyy-MM-dd");
						Map<Integer, Long> detail = map.get(dateStr);
						if (Objects.isNull(detail)) {
							detail = new HashMap<>(6);
							detail.put(Timer.T_WORK, 0L);
							detail.put(Timer.T_EAT, defaultEatTime);
							detail.put(Timer.T_ENTERTAINMENT, 0L);
							detail.put(Timer.T_SLEEP, defaultSleepTime);
							detail.put(Timer.T_STUDY, 0L);
							detail.put(Timer.T_UNKNOWN, total - (defaultEatTime + defaultSleepTime));
						}
						retMap.put(dateStr, detail);
					}
					return Result.success(retMap).toString();
				});
	}

	/**
	 * 查询计时
	 * @param request
	 * @return
	 */
	@GetMapping(value = "/api/timer/last/one")
	public Mono<String> selectLastOne(ServerHttpRequest request) {
		return Mono.just(SessionUtil.getUser(request))
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> timerService.searchLastOne(v.getId()).toString());
	}

	/**
	 * 查询计时
	 * @param request
	 * @return
	 */
	@GetMapping(value = "/api/timer/select")
	public Mono<String> select(ServerHttpRequest request,
			@RequestParam(value = "id") Long id) {
		return Mono.just(SessionUtil.getUser(request))
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> {
					Timer t = timerService.selectById(id);
					if (!v.getId().equals(t.getUid())) {
						return Result.failWithCustomMessage("无权查询").toString();
					}
					return Result.success(t).toString();
				});
	}
}
