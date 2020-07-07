package fun.johntaylor.kunkka.service.todo;

import fun.johntaylor.kunkka.entity.todo.Todo;
import fun.johntaylor.kunkka.entity.todo.TodoList;
import fun.johntaylor.kunkka.utils.result.Result;

import java.util.List;

/**
 * @Author John
 * @Description todoService
 * @Date 2020/6/22 5:55 PM
 **/
public interface TodoService {
	/**
	 * 单独增加任务(已知todoList)
	 * @param todo
	 */
	Result<TodoList> addTodo(Todo todo);

	/**
	 * 批量增加任务
	 * @param uid
	 * @param maxTime
	 * @param minPriority
	 * @param todos
	 */
	Result<TodoList> addPatch(Long uid, Integer maxTime, Integer minPriority, List<Todo> todos);


	/**
	 * @Author John
	 * @Description 查询timeMillis以来的todoList
	 * @Date 2020/7/7 8:41 PM
	 * @Param
	 * @return
	 **/
	Result<List<TodoList>> searchTodoList(Long uid, Integer offset, Integer count, Long timeMillis);

	/**
	 * @Author John
	 * @Description 根据listId和uid查询todo任务
	 * @Date 2020/7/7 8:41 PM
	 * @Param
	 * @return
	 **/
	Result<List<Todo>> searchTodosByListId(Long id, Long uid);
}
