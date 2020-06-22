package fun.johntaylor.kunkka.service.todo;

import fun.johntaylor.kunkka.entity.todo.Todo;

import java.util.List;

/**
 * @Author John
 * @Description todoService
 * @Date 2020/6/22 5:55 PM
 **/
public interface TodoService {

	/**
	 *
	 * @param maxTime
	 * @param minPriority
	 * @param todos
	 */
	void addPatch(Integer maxTime, Integer minPriority, List<Todo> todos);
}
