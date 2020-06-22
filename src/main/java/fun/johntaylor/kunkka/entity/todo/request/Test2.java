package fun.johntaylor.kunkka.entity.todo.request;

import lombok.Data;

import javax.validation.constraints.Max;

@Data
public class Test2 {
	@Max(value = 10,message = "test2不超过10")
	private Long id;
}
