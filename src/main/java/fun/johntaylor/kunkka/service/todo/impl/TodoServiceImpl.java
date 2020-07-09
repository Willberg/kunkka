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

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
			} else if (Todo.S_INITIAL.equals(t.getStatus()) || Todo.S_PENDING.equals(t.getStatus())) {
				totalTime += t.getEstimateTime();
			}
		}

		if (totalTime >= todoGroup.getMaxTime()) {
			return Result.failWithMessage(ErrorCode.SYS_CUSTOMIZE_ERROR, "我今天的任务都做不完了");
		}
		todoMapper.insert(todo);
		return Result.success(todoGroup);
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
		// 根据背包问题方案解决, f[v] = max{f[v], f[v-w[i]] +v[i]}
		int capacity = todoGroup.getMaxTime();
		int usedTotalTime = 0;
		int usedTotalValue = 0;
		if (Objects.nonNull(todoGroup.getId())) {
			// initial状态的任务要重新进行动态规划，pending状态和finished不需要，只有finished状态的任务采用实际用时，delete状态不参与
			// totalTime = initial + pending + finished
			// totalValue = initial + pending + finished
			List<Todo> oldTodoList = todoMapper.selectTodoList(todoGroup.getId());

			for (Todo t : oldTodoList) {
				if (Todo.S_INITIAL.equals(t.getStatus())) {
					// initial状态需要重新规划
					todoList.add(t);
				}

				if (Todo.S_PENDING.equals(t.getStatus())) {
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
		log.debug("total value: " + dp[capacity]);

		int totalTime = usedTotalTime;
		int totalValue = usedTotalValue;
		for (Todo t : todoList) {
			// 优先级较高不可过滤
			if (Todo.S_DEL.equals(t.getStatus()) && todoGroup.getMinPriority() >= t.getPriority()) {
				t.setStatus(Todo.S_INITIAL);
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

			if (Todo.S_INITIAL.equals(t.getStatus())) {
				// 存在未完成任务
				todoGroup.setTotalTime(totalTime);
				todoGroup.setValue(totalValue);
				todoGroup.setStatus(TodoGroup.S_PENDING);
			}
		}

		todoGroupMapper.insertWithUpdate(todoGroup);
		todoList.forEach(t -> {
			t.setGroupId(todoGroup.getId());
			todoMapper.insert(t);
		});
		return Result.success(todoGroup);
	}

	@Override
	public Result<List<TodoGroup>> searchTodoGroupList(Long uid, Integer offset, Integer count, Long timeMillis, String sort) {
		List<TodoGroup> todoGroupList = todoGroupMapper.selectList(uid, offset, count, timeMillis, sort);
		return Result.success(todoGroupList);
	}

	@Override
	public Result<List<Todo>> searchTodoListByGroupId(Long groupId) {
		return Result.success(todoMapper.selectTodoList(groupId));
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
					int preValue = dp[j - todoList.get(i).getEstimateTime()] + todoList.get(i).getValue() * (TodoGroup.MAX_PRIORITY - todoList.get(i).getPriority());
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
