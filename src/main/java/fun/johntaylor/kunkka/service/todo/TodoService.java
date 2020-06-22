package fun.johntaylor.kunkka.service.todo;

import fun.johntaylor.kunkka.entity.todo.Todo;

import java.util.List;

/**
 * @Author John
 * @Description
 * @Date 2020/6/22 5:55 PM
 **/
public interface TodoService {

	/**
	 * @Author John
	 * @Description
	 * @Date 2020/6/22 6:11 PM
	 * @Param
	 * @return
	 **/
	void addPatch(Integer maxTime, Integer minPriority, List<Todo> todos);

	void test(Long id);
}
