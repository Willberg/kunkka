package fun.johntaylor.kunkka.controller.todo.param;

import fun.johntaylor.kunkka.entity.todo.Todo;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class TodoEntity {
	@Min(value = 1, message = "最长时间不能少于1")
	private Integer maxTime;

	@Min(value = 1, message = "优先级不能小于1")
	@Max(value = 10, message = "优先级不能大于10")
	private Integer minPriority;

	@NotNull
	private List<Todo> todos;
}