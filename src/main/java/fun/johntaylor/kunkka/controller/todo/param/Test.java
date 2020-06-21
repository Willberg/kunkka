package fun.johntaylor.kunkka.controller.todo.param;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import java.util.List;

@Data
public class Test {
	@Max(value = 10,message = "test不能大于10")
	private Integer id;

	@Max(value = 10,message = "测试不能大于10")
	private Integer test;

	@Valid
	private List<Test2> test2List;
}

