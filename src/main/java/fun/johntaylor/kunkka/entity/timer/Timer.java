package fun.johntaylor.kunkka.entity.timer;

import fun.johntaylor.kunkka.entity.validation.Insert;
import fun.johntaylor.kunkka.entity.validation.Update;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author John
 * @Description 计时器
 * @Date 2020/8/12 5:52 PM
 **/
@Data
public class Timer {
	/**
	 * ID
	 */
	@NotNull(message = "请填写ID", groups = {Update.class})
	private Long id;

	/**
	 * uid
	 */
	private Long uid;

	/**
	 * 创建时间
	 */
	private Long createTime;

	/**
	 * 更新时间
	 */
	private Long updateTime;

	/**
	 * 类别， 1-- 工作， 2--吃饭， 3--休闲娱乐， 4--睡觉, 5--学习, 6--未知
	 */
	@NotNull(message = "请选择类别", groups = {Insert.class})
	private Integer type;

	/**
	 * 关联ID，每一个closed状态的计时器都会与一个open计时器关联
	 */
	private Long relatedId;

	/**
	 * 状态， 1-- 开始， 2--结束
	 */
	@NotNull(message = "请选择状态", groups = {Insert.class})
	private Integer status;

	public static final Integer T_WORK = 1;
	public static final Integer T_EAT = 2;
	public static final Integer T_ENTERTAINMENT = 3;
	public static final Integer T_SLEEP = 4;
	public static final Integer T_STUDY = 5;
	public static final Integer T_UNKNOWN = 6;

	public static final Integer S_OPEN = 1;
	public static final Integer S_CLOSED = 2;
}
