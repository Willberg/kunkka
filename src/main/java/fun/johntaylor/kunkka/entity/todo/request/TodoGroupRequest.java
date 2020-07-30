package fun.johntaylor.kunkka.entity.todo.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @Author John
 * @Description 任务组
 * @Date 2020/7/30 3:32 PM
 **/
@Data
public class TodoGroupRequest {
	@NotNull
	private Long id;

	@Min(value = 1, message = "最长时间不能少于1")
	private Integer maxTime;

	@Min(value = 1, message = "优先级不能小于1")
	@Max(value = 10, message = "优先级不能大于10")
	private Integer minPriority;

	/**
	 * 是否私有
	 */
	private Boolean isPrivate;
}
