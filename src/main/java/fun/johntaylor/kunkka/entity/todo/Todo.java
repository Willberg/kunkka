package fun.johntaylor.kunkka.entity.todo;

import fun.johntaylor.kunkka.entity.validation.Insert;
import fun.johntaylor.kunkka.entity.validation.todo.InsertPatchTodo;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @Author John
 * @Description 具体任务
 * @Date 2020/7/7 9:34 PM
 **/
@Data
public class Todo {
	private Long id;

	/**
	 * 任务
	 */
	@NotNull(message = "请指定任务内容", groups = Insert.class)
	private String task;

	/**
	 * 价值 1-100
	 */
	@Min(value = 1, message = "价值不能小于1")
	@Max(value = 100, message = "价值不能超过100")
	@NotNull(message = "请输入价值", groups = InsertPatchTodo.class)
	private Integer value;

	/**
	 * 预估时间 单位分钟
	 */
	@NotNull(message = "请输入预估时间", groups = InsertPatchTodo.class)
	private Integer estimateTime;

	/**
	 * 实际用时 单位分钟
	 */
	private Integer realityTime;

	/**
	 * 关联的groupId
	 */
	private Long groupId;

	private Long createTime;

	private Long updateTime;

	/**
	 * 优先级 1-10， 数值越小，优先级越高
	 */
	@Min(value = 1, message = "优先级不能小于1")
	@Max(value = 10, message = "优先级不能超过10")
	@NotNull(message = "请确定优先级", groups = {Insert.class, InsertPatchTodo.class})
	private Integer priority;

	/**
	 * 1--初始（任务发起者），10--待处理（任务发起者和任务执行者均可处理），
	 * 20-- 处理中（任务发起者无权修改，任务执行者进行中）， 50- 作废（任务发起者可以处理），
	 * 100- 完成
	 */
	private Integer status;

	public static final Integer P_MIN = 1;
	public static final Integer P_MAX = 10;

	public static final Integer V_MAX_VALUE = 100;

	public static final Integer S_INITIAL = 1;
	public static final Integer S_PENDING = 10;
	public static final Integer S_PROCESSING = 20;
	public static final Integer S_DEL = 50;
	public static final Integer S_FINISHED = 100;
}
