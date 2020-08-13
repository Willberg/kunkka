package fun.johntaylor.kunkka.service.timer;

import fun.johntaylor.kunkka.entity.timer.Timer;
import fun.johntaylor.kunkka.utils.result.Result;

import java.util.List;

/**
 * @Author John
 * @Description 计时器服务
 * @Date 2020/8/12 7:55 PM
 **/
public interface TimerService {
	/**
	 * 添加计时器
	 * @param timer
	 * @return Result<Timer>
	 */
	Result<Timer> add(Timer timer);

	/**
	 * 更新计时器
	 * @param timer
	 * @return Result<Timer>
	 */
	Result<Timer> update(Timer timer);

	/**
	 * 查询计时器
	 * @param uid
	 * @param startTime
	 * @param endTime
	 * @return List<Timer>
	 */
	List<Timer> list(Long uid, Long startTime, Long endTime);

	/**
	 * 查询最近的计时器
	 * @param uid
	 * @return Result<Timer>
	 */
	Result<Timer> searchLastOne(Long uid);

	/**
	 * 查询计时器
	 * @param id
	 * @return Result<Timer>
	 */
	Timer selectById(Long id);
}
