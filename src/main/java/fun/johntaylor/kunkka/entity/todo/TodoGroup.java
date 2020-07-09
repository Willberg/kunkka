package fun.johntaylor.kunkka.entity.todo;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * @Author John
 * @Description 任务组
 * @Date 2020/7/7 9:12 PM
 **/
@Data
public class TodoGroup {
	private Long id;

	/**
	 * 用户ID
	 */
	private Long uid;

	/**
	 * 总价值
	 */
	private Integer value;

	/**
	 * 完成价值
	 */
	private Integer finishValue;

	/**
	 * 总用时 单位分钟
	 */
	private Integer totalTime;

	/**
	 * 最多用时， 不能超过此用时时间
	 */
	private Integer maxTime;

	/**
	 * 最低优先级，高于此优先级的任务不可过滤
	 */
	@Min(value = 1, message = "不能小于1")
	@Max(value = 10, message = "不能超过10")
	private Integer minPriority;

	private Long createTime;

	private Long updateTime;

	/**
	 * 是否私有
	 */
	private Boolean isPrivate;

	/**
	 * 1-- 待处理， 50- 作废， 100- 完成
	 */
	private Integer status;


	public static final Integer MAX_PRIORITY = 10;

	public static final Integer S_PENDING = 1;
	public static final Integer S_DEL = 50;
	public static final Integer S_FINISHED = 100;
}
