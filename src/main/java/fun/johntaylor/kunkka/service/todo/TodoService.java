package fun.johntaylor.kunkka.service.todo;

import fun.johntaylor.kunkka.entity.todo.Todo;
import fun.johntaylor.kunkka.entity.todo.TodoGroup;
import fun.johntaylor.kunkka.utils.result.Result;

import java.util.List;
import java.util.Map;

/**
 * @Author John
 * @Description todoService
 * @Date 2020/6/22 5:55 PM
 **/
public interface TodoService {
	/**
	 * 单独增加任务(已知todoList), 开放服务
	 * @param todo
	 */
	Result<TodoGroup> openAddTodo(Todo todo);

	/**
	 * 修改任务(已知todoList), 开放服务
	 * @param todo
	 */
	Result<TodoGroup> openUpdateTodo(Todo todo);

	/**
	 * 批量增加任务
	 * @param todoGroup
	 * @param todoList
	 */
	Result<TodoGroup> addPatch(TodoGroup todoGroup, List<Todo> todoList);


	/**
	 * @Author John
	 * @Description 查询timeMillis以来的todoGroup
	 * @Date 2020/7/7 8:41 PM
	 * @Param
	 * @return
	 **/
	Result<List<TodoGroup>> searchTodoGroupList(Long uid, Integer offset, Integer count, Long timeMillis, String sort);

	/**
	 * @Author John
	 * @Description 根据groupId查询todo任务
	 * @Date 2020/7/7 8:41 PM
	 * @Param
	 * @return
	 **/
	Result<Map<Integer, List<Todo>>> searchTodoListByGroupId(Long groupId);

	/**
	 * @Author John
	 * @Description 根据groupId, uid查询todo任务
	 * @Date 2020/7/7 8:41 PM
	 * @Param
	 * @return
	 **/
	Result<Map<Integer, List<Todo>>> searchTodoListByUidGroupId(Long uid, Long groupId);

	/**
	 * @Author John
	 * @Description 更新任务
	 * @Date 2020/7/12 5:24 PM
	 * @Param
	 * @return
	 **/
	Result<Todo> updateTodo(Todo todo);
}
