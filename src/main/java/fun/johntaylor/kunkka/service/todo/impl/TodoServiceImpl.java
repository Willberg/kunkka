package fun.johntaylor.kunkka.service.todo.impl;

import fun.johntaylor.kunkka.entity.todo.Todo;
import fun.johntaylor.kunkka.entity.todo.TodoGroup;
import fun.johntaylor.kunkka.repository.mybatis.todo.TodoGroupMapper;
import fun.johntaylor.kunkka.repository.mybatis.todo.TodoMapper;
import fun.johntaylor.kunkka.service.todo.TodoService;
import fun.johntaylor.kunkka.utils.error.ErrorCode;
import fun.johntaylor.kunkka.utils.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @Author John
 * @Description todo服务
 * @Date 2020/7/2 11:38 PM
 **/
@Service
@Slf4j
public class TodoServiceImpl implements TodoService {
	@Autowired
	private TodoGroupMapper todoGroupMapper;

	@Autowired
	private TodoMapper todoMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result<TodoGroup> openAddTodo(Todo todo) {
		TodoGroup todoGroup = todoGroupMapper.select(todo.getGroupId());
		if (Objects.isNull(todoGroup)) {
			return Result.failWithMessage(ErrorCode.SYS_CUSTOMIZE_ERROR, "任务组不存在");
		}

		List<Todo> todoList = todoMapper.selectTodoList(todo.getGroupId());
		int totalTime = 0;
		for (Todo t : todoList) {
			// 根据状态计算已占用时长
			if (Todo.S_FINISHED.equals(t.getStatus())) {
				totalTime += t.getRealityTime();
			} else if (Todo.S_PENDING.equals(t.getStatus()) || Todo.S_PROCESSING.equals(t.getStatus())) {
				totalTime += t.getEstimateTime();
			}
		}

		if (totalTime >= todoGroup.getMaxTime()) {
			return Result.failWithMessage(ErrorCode.SYS_CUSTOMIZE_ERROR, "我今天的任务都做不完了");
		}
		todoMapper.insert(todo);
		return Result.success(todoGroup);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result<TodoGroup> openUpdateTodo(Todo todo) {
		Todo oldTodo = todoMapper.select(todo.getId());
		if (Objects.isNull(oldTodo)) {
			return Result.failWithMessage(ErrorCode.SYS_CUSTOMIZE_ERROR, "任务不存在");
		}

		if (Todo.S_PROCESSING.equals(oldTodo.getStatus())
				|| Todo.S_FINISHED.equals(oldTodo.getStatus())) {
			return Result.failWithMessage(ErrorCode.SYS_CUSTOMIZE_ERROR, "大哥，任务都在处理了，你还想偷偷改");
		}

		// 以后改成幂等操作
		todoMapper.update(todo);
		return Result.success();
	}


	/**
	 * 需要调用该方法，且需要支持事务特性的调用方，是在 @Transactional所在的类的外面
	 * @param todoGroup
	 * @param todoList
	 * @return Result
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result<TodoGroup> addPatch(TodoGroup todoGroup, List<Todo> todoList) {
		Result<TodoGroup> result = process(todoGroup, todoList);
		if (!result.isSuccess()) {
			return result;
		}

		todoGroupMapper.insertWithUpdate(todoGroup);
		todoList.forEach(t -> {
			t.setGroupId(todoGroup.getId());
			todoMapper.insert(t);
		});
		return Result.success(todoGroup);
	}


	/**
	 * @Author John
	 * @Description 动态规划最佳的任务解决方案
	 * @Date 2020/7/12 5:42 PM
	 * @Param
	 * @return
	 **/
	private Result<TodoGroup> process(TodoGroup todoGroup, List<Todo> todoList) {
		// 根据背包问题方案解决, f[v] = max{f[v], f[v-w[i]] +v[i]}
		int capacity = todoGroup.getMaxTime();
		int usedTotalTime = 0;
		int usedTotalValue = 0;
		if (Objects.nonNull(todoGroup.getId())) {
			// pending状态的任务要重新进行动态规划，processing状态和finished不需要，只有finished状态的任务采用实际用时，initial,delete状态不参与
			// totalTime = pending + processing + finished
			// totalValue = pending + processing + finished
			List<Todo> oldTodoList = todoMapper.selectTodoList(todoGroup.getId());

			for (Todo t : oldTodoList) {
				if (Todo.S_PENDING.equals(t.getStatus())) {
					// pending状态需要重新规划
					todoList.add(t);
				}

				if (Todo.S_PROCESSING.equals(t.getStatus())) {
					usedTotalTime += t.getEstimateTime();
					usedTotalValue += t.getValue();
				}

				if (Todo.S_FINISHED.equals(t.getStatus())) {
					usedTotalTime += t.getRealityTime();
					usedTotalValue += t.getValue();
				}
			}

			// 剩余的时间
			capacity = capacity - usedTotalTime;
		}
		// 没有时间做别的事了
		if (capacity <= 0) {
			return Result.failWithMessage(ErrorCode.SYS_CUSTOMIZE_ERROR, "我今天的任务都做不完了");
		}

		int[] dp = calMaxValueByDp(capacity, todoList);
		log.info("groupId: {}, total value: {}", todoGroup.getId(), dp[capacity]);

		int totalTime = usedTotalTime;
		int totalValue = usedTotalValue;
		for (Todo t : todoList) {
			// 优先级较高不可过滤
			if (Todo.S_DEL.equals(t.getStatus()) && todoGroup.getMinPriority() >= t.getPriority()) {
				t.setStatus(Todo.S_PENDING);
			}
			t.setCreateTime(System.currentTimeMillis());
			t.setUpdateTime(System.currentTimeMillis());

			// 不能超过总时长
			if (totalTime + t.getEstimateTime() > todoGroup.getMaxTime()) {
				t.setStatus(Todo.S_DEL);
			} else {
				totalTime += t.getEstimateTime();
				totalValue += t.getValue();
			}

			if (Todo.S_PENDING.equals(t.getStatus())) {
				// 存在未完成任务
				todoGroup.setTotalTime(totalTime);
				todoGroup.setValue(totalValue);
				todoGroup.setStatus(TodoGroup.S_PENDING);
			}
		}
		return Result.success();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result<Todo> updateTodo(Todo todo) {
		Todo oldTodo = todoMapper.select(todo.getId());
		if (Objects.isNull(oldTodo)) {
			return Result.failWithMessage(ErrorCode.SYS_CUSTOMIZE_ERROR, "任务不存在");
		}

		TodoGroup oldTodoGroup = todoGroupMapper.select(oldTodo.getGroupId());
		if (Objects.isNull(oldTodoGroup)) {
			return Result.failWithMessage(ErrorCode.SYS_CUSTOMIZE_ERROR, "任务组不存在");
		}

		if (Todo.S_FINISHED.equals(oldTodo.getStatus())) {
			return Result.failWithMessage(ErrorCode.SYS_CUSTOMIZE_ERROR, "事都做完了，还改啥，重新分配个任务呗");
		}

		// pending任务且预估时间和价值修改，需要重新进行动态规划, initial, pending, del, processing,finished
		// task内容修改，必定影响预估时间和价值，也必须要重新规划
		// pending,processing, finished预估时间和价值必须都被设置,且finished的实际用时也必须设置
		if (Todo.S_PENDING.equals(todo.getStatus())
				|| Todo.S_PROCESSING.equals(todo.getStatus())
				|| Todo.S_FINISHED.equals(todo.getStatus())) {
			if (Objects.isNull(oldTodo.getEstimateTime()) && Objects.isNull(todo.getEstimateTime())) {
				return Result.failWithMessage(ErrorCode.SYS_CUSTOMIZE_ERROR, "请设置预估时间");
			}

			if (Objects.isNull(oldTodo.getValue()) && Objects.isNull(todo.getValue())) {
				return Result.failWithMessage(ErrorCode.SYS_CUSTOMIZE_ERROR, "请设置价值");
			}

			if (Todo.S_FINISHED.equals(todo.getStatus())) {
				if (Objects.isNull(oldTodo.getRealityTime()) && Objects.isNull(todo.getRealityTime())) {
					return Result.failWithMessage(ErrorCode.SYS_CUSTOMIZE_ERROR, "请设置实际用时");
				}
			}
		}

		todoMapper.update(todo);
		List<Todo> todoList = new ArrayList<>();
		Result<TodoGroup> result = process(oldTodoGroup, todoList);
		if (!result.isSuccess()) {
			return Result.failWithMessage(result.getCode(), result.getMessage());
		}

		oldTodoGroup.setUpdateTime(System.currentTimeMillis());
		todoGroupMapper.insertWithUpdate(oldTodoGroup);
		todoList.forEach(t -> {
			t.setGroupId(oldTodoGroup.getId());
			todoMapper.insert(t);
		});
		return Result.success(todo);
	}


	@Override
	public Result<List<TodoGroup>> searchTodoGroupList(Long uid, Integer offset, Integer count, Long timeMillis, String sort) {
		List<TodoGroup> todoGroupList = todoGroupMapper.selectList(uid, offset, count, timeMillis, sort);
		return Result.success(todoGroupList);
	}

	@Override
	public Result<Map<Integer, List<Todo>>> searchTodoListByGroupId(Long groupId) {
		TodoGroup todoGroup = todoGroupMapper.select(groupId);

		List<Todo> oldList = todoMapper.selectTodoList(groupId);
		if (Objects.isNull(oldList)) {
			return Result.success();
		}

		Map<Integer, List<Todo>> retMap = new HashMap<>(5);
		oldList.forEach(o -> {
			List<Todo> todoList = retMap.get(o.getStatus());
			if (Objects.isNull(todoList)) {
				todoList = new LinkedList<>();
				retMap.put(o.getStatus(), todoList);
			}
			todoList.add(o);
		});

		retMap.forEach((s, l) -> sortTodoListByCp(todoGroup.getMaxTime(), l));
		return Result.success(retMap);
	}

	/**
	 * 按预估时间排序（最短的靠前）；最后按价值排序（最大的靠前)
	 * @param todoList
	 */
	public void sortTodoList(List<Todo> todoList) {
		todoList.sort((o1, o2) -> {
			int o1Time = Todo.S_FINISHED.equals(o1.getStatus()) ? o1.getRealityTime() : o1.getEstimateTime();
			int o2Time = Todo.S_FINISHED.equals(o2.getStatus()) ? o2.getRealityTime() : o2.getEstimateTime();
			if (o1Time > o2Time) {
				return 1;
			} else if (o1Time < o2Time) {
				return -1;
			} else {
				if (o1.getValue() < o2.getValue()) {
					return 1;
				} else {
					return -1;
				}
			}
		});
	}

	/**
	 * 按性价比排，value / time（1/480～100/1） 从大到小
	 * @param todoList
	 */
	public void sortTodoListByCp(int maxTime, List<Todo> todoList) {
		todoList.sort((o1, o2) -> {
			int o1Time = Todo.S_FINISHED.equals(o1.getStatus()) ? o1.getRealityTime() : o1.getEstimateTime();
			int o2Time = Todo.S_FINISHED.equals(o2.getStatus()) ? o2.getRealityTime() : o2.getEstimateTime();
			// 性价比
			int cp1 = o1.getValue() * maxTime / o1Time;
			int cp2 = o2.getValue() * maxTime / o2Time;
			if (cp1 < cp2) {
				return 1;
			} else {
				return -1;
			}
		});
	}

	/**
	 * @decription 价值评判由优先级(越小权优先级高)×价值决定
	 * @param capacity
	 * @param todoList
	 * @return
	 */
	private int[] calMaxValueByDp(int capacity, List<Todo> todoList) {
		int[] dp = new int[capacity + 1];
		for (int i = 0; i < todoList.size(); i++) {
			for (int j = capacity; j >= 0; j--) {
				if (j > todoList.get(i).getEstimateTime()) {
					int preValue = dp[j - todoList.get(i).getEstimateTime()] + todoList.get(i).getValue() * (TodoGroup.PRIORITY_MAX_VALUE - todoList.get(i).getPriority());
					dp[j] = Math.max(dp[j], preValue);
					if (dp[j] == preValue) {
						todoList.get(i).setStatus(Todo.S_INITIAL);
					} else {
						todoList.get(i).setStatus(Todo.S_DEL);
					}
				}
			}
		}
		return dp;
	}
}
