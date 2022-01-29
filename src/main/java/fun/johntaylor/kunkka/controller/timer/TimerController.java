package fun.johntaylor.kunkka.controller.timer;

import fun.johntaylor.kunkka.component.redis.session.Session;
import fun.johntaylor.kunkka.component.thread.pool.DbThreadPool;
import fun.johntaylor.kunkka.entity.timer.Timer;
import fun.johntaylor.kunkka.entity.validation.Insert;
import fun.johntaylor.kunkka.entity.validation.Update;
import fun.johntaylor.kunkka.service.timer.TimerService;
import fun.johntaylor.kunkka.utils.error.ErrorCode;
import fun.johntaylor.kunkka.utils.result.Result;
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
	/**
	 * 每天总时长
	 */
	private static final long TOTAL_TIME = 24 * 60 * 60;

	@Autowired
	private DbThreadPool dbThreadPool;

	@Autowired
	private TimerService timerService;

	@Autowired
	private Session session;

	/**
	 * 添加计时
	 * @param request
	 * @return
	 */
	@PostMapping(value = "/api/timer/add")
	public Mono<String> add(ServerHttpRequest request,
			@Validated(value = {Insert.class}) @RequestBody Timer reqTimer) {
		return Mono.just(session.getUser(request))
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> {
					Timer t = new Timer();
					t.setUid(v.getId());
					t.setCreateTime(System.currentTimeMillis());
					t.setUpdateTime(System.currentTimeMillis());
					t.setType(reqTimer.getType());
					t.setRelatedId(reqTimer.getRelatedId());
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
		return Mono.just(session.getUser(request))
				.publishOn(dbThreadPool.daoInstance())
				.map(v -> {
					Timer t = new Timer();
					t.setId(reqTimer.getId());
					t.setUid(v.getId());
					t.setCreateTime(reqTimer.getCreateTime());
					t.setUpdateTime(System.currentTimeMillis());
					t.setType(reqTimer.getType());
					return timerService.update(t).toString();
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
		return Mono.just(session.getUser(request))
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
					// 第一个计时器如果是关闭状态，表示该计时器的开始于上个月，则应该添加一个该月1号的计时器
					if (timerList.size() > 0 && Timer.S_CLOSED.equals(timerList.get(0).getStatus())) {
						Timer t = new Timer();
						LocalDate localDate = TimeUtil.getLocalDateByTimestamp(timerList.get(0).getCreateTime());
						t.setCreateTime(TimeUtil.getTimestampByLocalDate(localDate.withDayOfMonth(1)));
						t.setType(timerList.get(0).getType());
						t.setStatus(Timer.S_OPEN);
						timerList.add(0, t);
					}

					//最后一个计时器如果是开始状态，表示该计时器结束于下一个月，则应该添加该月结束日最后时刻的计时器
					if (timerList.size() > 0 && Timer.S_OPEN.equals(timerList.get(timerList.size() - 1).getStatus())) {
						Timer lastTimer = timerList.get(timerList.size() - 1);

						Timer t = new Timer();
						LocalDate localDate = TimeUtil.getLocalDateByTimestamp(lastTimer.getCreateTime());
						// 减1000，使日期为本月最后一秒
						t.setCreateTime(TimeUtil.getTimestampByLocalDate(localDate.withDayOfMonth(1).plus(1, ChronoUnit.MONTHS)) - 1000);
						t.setType(lastTimer.getType());
						t.setStatus(Timer.S_CLOSED);
						timerList.add(timerList.size(), t);
					}

					// 统计各种计时器的时间
					long start = 0;
					String lastDateStr = "";
					int days;
					if (startLocalDate.getMonthValue() == TimeUtil.DECEMBER) {
						days = 31;
					} else {
						days = startLocalDate.plus(1, ChronoUnit.MONTHS).getDayOfYear() - startLocalDate.getDayOfYear();
					}
					// 如果selectedMonth是当月，只需要计算到当前日即可
					LocalDate now = LocalDate.now();
					String nowYearMonth = TimeUtil.getDateStrByLocalDate(now, "yyyy-MM");
					if (nowYearMonth.equals(selectedMonth)) {
						days = now.getDayOfYear() - startLocalDate.getDayOfYear() + 1;
					}

					Map<String, Map<Integer, Long>> map = new HashMap<>(days);
					for (Timer t : timerList) {
						String dateStr = TimeUtil.getDateStrByTimestamp(t.getCreateTime(), "yyyy-MM-dd");
						Map<Integer, Long> detail = initDetailMap(dateStr, map);

						if (Timer.S_OPEN.equals(t.getStatus())) {
							lastDateStr = dateStr;
							start = t.getCreateTime() / 1000;
						} else {
							String endDateStr = TimeUtil.getDateStrByTimestamp(t.getCreateTime(), "yyyy-MM-dd");
							if (lastDateStr.equals(endDateStr)) {
								long diff = t.getCreateTime() / 1000 - start;
								long old = detail.get(t.getType());
								detail.put(t.getType(), old + diff);
							} else {
								// 结束时间与计时器开始时间不在同一天，应该拆分为多天分别计算，以两天的24时分割
								LocalDate sLocalDate = TimeUtil.getLocalDate(lastDateStr, "yyyy-MM-dd");
								LocalDate eLocalDate = TimeUtil.getLocalDateByTimestamp(t.getCreateTime());
								int seDays = eLocalDate.getDayOfYear() - sLocalDate.getDayOfYear();
								for (int i = 0; i < seDays; i++) {
									detail = initDetailMap(lastDateStr, map);
									LocalDate localDate = sLocalDate.plus(i + 1, ChronoUnit.DAYS);
									long end = TimeUtil.getTimestampByLocalDate(localDate) / 1000;
									long diff = end - start;
									long old = detail.get(t.getType());
									detail.put(t.getType(), old + diff);

									lastDateStr = TimeUtil.getDateStrByLocalDate(localDate, "yyyy-MM-dd");
									start = end;
								}

								detail = initDetailMap(endDateStr, map);
								long old = detail.get(t.getType());
								long diff = t.getCreateTime() / 1000 - start;
								detail.put(t.getType(), old + diff);
							}
						}
					}

					// 将剩余时间进行分配
					calMapRestTime(map);

					// 构建返回值
					Map<String, Map<Integer, Long>> retMap = new LinkedHashMap<>(days);
					for (int day = 0; day < days; day++) {
						String dateStr = TimeUtil.getDateStrByLocalDate(startLocalDate.plusDays(day), "yyyy-MM-dd");
						Map<Integer, Long> detail = map.get(dateStr);
						if (Objects.isNull(detail)) {
							detail = initDetailMap();
						}
						retMap.put(dateStr, detail);
					}
					return Result.success(retMap).toString();
				});
	}

	/**
	 * 将剩余时间进行分配
	 * @param total
	 * @param defaultSleepTime
	 * @param defaultEatTime
	 * @param map
	 */
	private void calMapRestTime(long total, long defaultSleepTime, long defaultEatTime, Map<String, Map<Integer, Long>> map) {
		map.keySet().forEach(k -> {
			Map<Integer, Long> detail = map.get(k);

			long useTime = 0;
			for (Long dv : detail.values()) {
				useTime += dv;
			}

			long restTime = total - useTime;
			long sleepTime = detail.get(Timer.T_SLEEP);
			long eatTime = detail.get(Timer.T_EAT);
			if (sleepTime != 0 && eatTime != 0) {
				detail.put(Timer.T_UNKNOWN, restTime - sleepTime - eatTime);
			} else if (sleepTime == 0 && eatTime != 0) {
				restTime -= eatTime;
				if (restTime >= defaultSleepTime) {
					sleepTime = defaultSleepTime;
				} else {
					sleepTime = restTime;
				}
				detail.put(Timer.T_SLEEP, sleepTime);
				detail.put(Timer.T_UNKNOWN, restTime - sleepTime);
			} else if (sleepTime != 0) {
				restTime -= sleepTime;
				if (restTime >= defaultEatTime) {
					eatTime = defaultEatTime;
				} else {
					eatTime = restTime;
				}
				detail.put(Timer.T_EAT, eatTime);
				detail.put(Timer.T_UNKNOWN, restTime - eatTime);
			} else {
				if (restTime >= defaultSleepTime + defaultEatTime) {
					detail.put(Timer.T_EAT, defaultEatTime);
					detail.put(Timer.T_SLEEP, defaultSleepTime);
					detail.put(Timer.T_UNKNOWN, total - (defaultEatTime + defaultSleepTime));
				} else {
					sleepTime = Double.valueOf(restTime * 0.75).longValue();
					detail.put(Timer.T_SLEEP, sleepTime);
					eatTime = Double.valueOf(restTime * 0.25).longValue();
					detail.put(Timer.T_EAT, eatTime);
					detail.put(Timer.T_UNKNOWN, total - (sleepTime + eatTime));
				}
			}
		});
	}

	/**
	 * 将剩余时间进行分配
	 * @param map
	 */
	private void calMapRestTime(Map<String, Map<Integer, Long>> map) {
		map.keySet().forEach(k -> {
			Map<Integer, Long> detail = map.get(k);

			long useTime = 0;
			// 将未知时间归零
			detail.put(Timer.T_UNKNOWN, 0L);
			for (Long dv : detail.values()) {
				useTime += dv;
			}

			long restTime = TOTAL_TIME - useTime;
			detail.put(Timer.T_UNKNOWN, restTime);
		});
	}

	/**
	 * 初始化detailMap
	 * @param dateStr
	 * @param map
	 * @return detailMap
	 */
	private Map<Integer, Long> initDetailMap(String dateStr, Map<String, Map<Integer, Long>> map) {
		Map<Integer, Long> detail = map.get(dateStr);
		if (Objects.isNull(detail)) {
			detail = initDetailMap();
			map.put(dateStr, detail);
		}
		return detail;
	}

	/**
	 * 初始化detailMap
	 * @return detailMap
	 */
	private Map<Integer, Long> initDetailMap() {
		Map<Integer, Long> detail = new HashMap<>(9);
		detail.put(Timer.T_WORK, 0L);
		detail.put(Timer.T_EAT, 0L);
		detail.put(Timer.T_ENTERTAINMENT, 0L);
		detail.put(Timer.T_SLEEP, 0L);
		detail.put(Timer.T_STUDY, 0L);
		detail.put(Timer.T_READ, 0L);
		detail.put(Timer.T_LEISURE, 0L);
		detail.put(Timer.T_PROJECT, 0L);
		detail.put(Timer.T_UNKNOWN, TOTAL_TIME);
		return detail;
	}

	/**
	 * 查询计时
	 * @param request
	 * @return
	 */
	@GetMapping(value = "/api/timer/last/one")
	public Mono<String> selectLastOne(ServerHttpRequest request) {
		return Mono.just(session.getUser(request))
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
		return Mono.just(session.getUser(request))
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
