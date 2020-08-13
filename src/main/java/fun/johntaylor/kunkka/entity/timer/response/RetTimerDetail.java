package fun.johntaylor.kunkka.entity.timer.response;

import lombok.Data;

/**
 * @Author John
 * @Description 计时器细节
 * @Date 2020/8/13 11:07 AM
 **/
@Data
public class RetTimerDetail {
	/**
	 * type
	 */
	private Integer type;

	/**
	 * 耗时
	 */
	private Long useTime;
}
