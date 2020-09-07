package fun.johntaylor.kunkka.service.todo;

import fun.johntaylor.kunkka.entity.todo.Todo;
import fun.johntaylor.kunkka.entity.todo.TodoGroup;
import fun.johntaylor.kunkka.utils.result.Result;

import java.util.List;

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
	Result<Todo> openAddTodo(Todo todo);

	/**
	 * 修改任务(已知todoList), 开放服务
	 * @param todo
	 */
	Result<Todo> openUpdateTodo(Todo todo);

	/**
	 * 批量增加任务
	 * @param todoGroup
	 * @param todoList
	 */
	Result<Object> addPatch(TodoGroup todoGroup, List<Todo> todoList);


	/**
	 * 查询timeMillis以来的todoGroup
	 * @param uid
	 * @param offset
	 * @param count
	 * @param status
	 * @param startTime
	 * @param endTime
	 * @param sort
	 * @return Result
	 */
	Result<List<TodoGroup>> searchTodoGroupList(Long uid, Integer offset, Integer count, Integer status, Long startTime, Long endTime, String sort);

	/**
	 * @Author John
	 * @Description 根据groupId查询todo任务
	 * @Date 2020/7/7 8:41 PM
	 * @Param
	 * @return
	 **/
	Result<List<Todo>> searchTodoListByGroupId(Long groupId);

	/**
	 * @Author John
	 * @Description 根据groupId, uid查询todo任务
	 * @Date 2020/7/7 8:41 PM
	 * @Param
	 * @return
	 **/
	Result<List<Todo>> searchTodoListByUidGroupId(Long uid, Long groupId);

	/**
	 * @Author John
	 * @Description 更新任务
	 * @Date 2020/7/12 5:24 PM
	 * @Param
	 * @return
	 **/
	Result<Todo> updateTodo(Todo todo);


	/**
	 * @Author John
	 * @Description 更新任务组
	 * @Date 2020/7/30 3:35 PM
	 * @Param todoGroup
	 * @return
	 **/
	Result<TodoGroup> updateTodoGroup(TodoGroup todoGroup);
}
