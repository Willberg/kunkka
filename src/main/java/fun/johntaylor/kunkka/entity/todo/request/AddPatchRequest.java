package fun.johntaylor.kunkka.entity.todo.request;

import fun.johntaylor.kunkka.entity.todo.Todo;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author John
 * @Description 添加任务的请求body
 * @Date 2020/6/22 8:07 PM
 **/
@Data
public class AddPatchRequest {
	private Long groupId;

	@Min(value = 1, message = "最长时间不能少于1")
	@NotNull(message = "请确定任务组最长时间")
	private Integer maxTime;

	@Min(value = 1, message = "优先级不能小于1")
	@Max(value = 10, message = "优先级不能大于10")
	@NotNull(message = "请确定必须完成的任务优先级")
	private Integer minPriority;

	@NotNull
	@Valid
	private List<Todo> todoList;
}