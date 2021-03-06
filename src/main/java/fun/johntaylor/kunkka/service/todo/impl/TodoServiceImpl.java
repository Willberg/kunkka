package fun.johntaylor.kunkka.service.todo.impl;

import fun.johntaylor.kunkka.entity.todo.Todo;
import fun.johntaylor.kunkka.entity.todo.TodoGroup;
import fun.johntaylor.kunkka.repository.mybatis.todo.TodoGroupMapper;
import fun.johntaylor.kunkka.repository.mybatis.todo.TodoMapper;
import fun.johntaylor.kunkka.service.todo.TodoService;
import fun.johntaylor.kunkka.utils.error.ErrorCode;
import fun.johntaylor.kunkka.utils.general.CopyUtil;
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
	public Result<Todo> openAddTodo(Todo todo) {
		TodoGroup todoGroup = todoGroupMapper.select(todo.getGroupId());
		if (Objects.isNull(todoGroup)) {
			return Result.failWithCustomMessage("任务组不存在");
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
			return Result.failWithCustomMessage("我今天的任务都做不完了");
		}
		todoMapper.insert(todo);
		return Result.success(todo);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result<Todo> openUpdateTodo(Todo todo) {
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
	public Result<Object> addPatch(TodoGroup todoGroup, List<Todo> todoList) {
		boolean hasGroupId = true;
		if (Objects.nonNull(todoGroup.getId())) {
			TodoGroup oldTodoGroup = todoGroupMapper.select(todoGroup.getId());
			if (Objects.isNull(oldTodoGroup) || !todoGroup.getUid().equals(oldTodoGroup.getUid())) {
				return Result.fail(ErrorCode.USER_ILLEGAL_OPERATION);
			}

			if (!TodoGroup.S_PENDING.equals(oldTodoGroup.getStatus())) {
				return Result.failWithCustomMessage("任务组已完成或删除，请新建新任务组");
			}

			todoGroup.setMinPriority(Optional.ofNullable(todoGroup.getMinPriority()).orElse(oldTodoGroup.getMinPriority()));
			todoGroup.setMaxTime(Optional.ofNullable(todoGroup.getMaxTime()).orElse(oldTodoGroup.getMaxTime()));
		} else {
			hasGroupId = false;
			if (Objects.isNull(todoGroup.getMinPriority())) {
				return Result.failWithMessage(ErrorCode.SYS_PARAMETER_ERROR, "请确定必须完成的任务优先级");
			}

			if (Objects.isNull(todoGroup.getMaxTime())) {
				return Result.failWithMessage(ErrorCode.SYS_PARAMETER_ERROR, "请确定任务组最长时间");
			}
			// 默认是删除，后面发现有pending的任务再改为pending
			todoGroup.setStatus(TodoGroup.S_DEL);
		}
		Result<Set<Long>> result = process(todoGroup, todoList);
		if (!result.isSuccess()) {
			return Result.failWithCustomMessage(result.getMessage());
		}

		if (Objects.isNull(todoGroup.getId())) {
			todoGroupMapper.insert(todoGroup);
		} else {
			todoGroupMapper.update(todoGroup);
		}

		Set<Long> updateTodoSet = result.getData();
		todoList.forEach(t -> {
			t.setGroupId(todoGroup.getId());
			if (updateTodoSet.contains(t.getId())) {
				todoMapper.update(t);
			} else {
				todoMapper.insert(t);
			}
		});
		if (hasGroupId) {
			return Result.success(todoList);
		}
		return Result.success(todoGroup);
	}


	/**
	 * @Author John
	 * @Description 动态规划最佳的任务解决方案
	 * @Date 2020/7/12 5:42 PM
	 * @Param
	 * @return
	 **/
	private Result<Set<Long>> process(TodoGroup todoGroup, List<Todo> todoList) {
		// 根据背包问题方案解决, f[v] = max{f[v], f[v-w[i]] +v[i]}
		int capacity = todoGroup.getMaxTime();
		int usedTotalTime = 0;
		int usedTotalValue = 0;
		// 需要更新的todo
		Set<Long> updateTodoSet = new HashSet<>();
		if (Objects.nonNull(todoGroup.getId())) {
			// pending状态的任务要重新进行动态规划，processing状态和finished不需要，只有finished状态的任务采用实际用时，initial,delete状态不参与
			// totalTime = pending + processing + finished
			// totalValue = pending + processing + finished
			List<Todo> oldTodoList = todoMapper.selectTodoList(todoGroup.getId());

			for (Todo t : oldTodoList) {
				if (Todo.S_PENDING.equals(t.getStatus())) {
					// pending状态需要重新规划
					todoList.add(t);
					updateTodoSet.add(t.getId());
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
		} else {
			// 初始任务组，状态为pending
			todoGroup.setStatus(TodoGroup.S_PENDING);
		}
		// 没有时间做别的事了
		if (capacity <= 0) {
			return Result.failWithCustomMessage("我今天的任务都做不完了");
		}

		//为了动态规划，引入一个空的todo，使用完需要删除
		todoList.add(0, new Todo());
		int[][] dp = calMaxValueByDp(capacity, todoList);
		log.info("groupId: {}, total value: {}", todoGroup.getId(), dp[todoList.size() - 1][capacity]);
		// 找出最优解
		findWho(todoList.size() - 1, capacity, dp, todoList);
		todoList.remove(0);

		// 排序
		sortTodoListByStatusCp(todoGroup.getMaxTime(), todoList);

		int totalTime = usedTotalTime;
		int totalValue = usedTotalValue;
		for (Todo t : todoList) {
			// 优先级较高不可过滤
			if (Todo.S_DEL.equals(t.getStatus()) && todoGroup.getMinPriority() >= t.getPriority()) {
				t.setStatus(Todo.S_PENDING);
			}
			if (!updateTodoSet.contains(t.getId())) {
				t.setCreateTime(System.currentTimeMillis());
			}
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
				todoGroup.setStatus(TodoGroup.S_PENDING);
			}
		}
		todoGroup.setTotalTime(totalTime);
		todoGroup.setValue(totalValue);
		return Result.success(updateTodoSet);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result<Todo> updateTodo(Todo todo) {
		Todo oldTodo = todoMapper.select(todo.getId());
		if (Objects.isNull(oldTodo)) {
			return Result.failWithCustomMessage("任务不存在");
		}

		TodoGroup oldTodoGroup = todoGroupMapper.select(oldTodo.getGroupId());
		if (Objects.isNull(oldTodoGroup)) {
			return Result.failWithCustomMessage("任务组不存在");
		}

		if (Todo.S_FINISHED.equals(oldTodo.getStatus())) {
			return Result.failWithCustomMessage("事都做完了，还改啥，重新分配个任务呗");
		}

		Result<Todo> checkResult = checkUpdateTodo(todo, oldTodo);
		if (!checkResult.isSuccess()) {
			return checkResult;
		}

		todoMapper.update(todo);
		// 从内存中构造返回的todo
		Todo retTodo = CopyUtil.deepCopy(oldTodo, Todo.class);
		CopyUtil.copy(todo, retTodo);

		// 只有pending状态需要重新规划
		if (Todo.S_PENDING.equals(todo.getStatus())) {
			List<Todo> todoList = new ArrayList<>();
			Result<Set<Long>> result = process(oldTodoGroup, todoList);
			if (!result.isSuccess()) {
				return Result.failWithCustomMessage(result.getMessage());
			}
			oldTodoGroup.setUpdateTime(System.currentTimeMillis());
			todoGroupMapper.update(oldTodoGroup);
			todoList.forEach(t -> {
				todoMapper.update(t);
				if (Objects.equals(retTodo.getId(), t.getId())) {
					CopyUtil.copy(t, retTodo);
				}
			});
		}

		// 如果任务完成或删除，要检查任务组是否还有未完成的任务，如果没有要将任务组标记为完成
		if (Todo.S_FINISHED.equals(todo.getStatus()) || Todo.S_DEL.equals(todo.getStatus())) {
			boolean isChangeStatus = true;
			boolean isFinished = false;
			List<Todo> todoList = todoMapper.selectTodoList(oldTodoGroup.getId());
			for (Todo t : todoList) {
				if (!Todo.S_DEL.equals(t.getStatus()) && !Todo.S_FINISHED.equals(t.getStatus())) {
					isChangeStatus = false;
				} else {
					if (Todo.S_FINISHED.equals(t.getStatus())) {
						isFinished = true;
					}
				}
			}

			if (isChangeStatus) {
				if (isFinished) {
					oldTodoGroup.setStatus(TodoGroup.S_FINISHED);
				} else {
					oldTodoGroup.setStatus(TodoGroup.S_DEL);
				}
			}

			if (Todo.S_DEL.equals(todo.getStatus()) && !Todo.S_INITIAL.equals(oldTodo.getStatus())) {
				oldTodoGroup.setValue(oldTodoGroup.getValue() - oldTodo.getValue());
			}
			oldTodoGroup.setUpdateTime(System.currentTimeMillis());
			int finishedValue = Todo.S_FINISHED.equals(todo.getStatus()) ? oldTodo.getValue() : 0;
			int oldFinishedValue = Optional.ofNullable(oldTodoGroup.getFinishValue()).orElse(0);
			oldTodoGroup.setFinishValue(oldFinishedValue + finishedValue);
			int totalTime = Todo.S_FINISHED.equals(todo.getStatus()) ? todo.getRealityTime() : 0;
			int esTime = Optional.ofNullable(oldTodo.getEstimateTime()).orElse(0);
			oldTodoGroup.setTotalTime(oldTodoGroup.getTotalTime() + totalTime - esTime);
			todoGroupMapper.update(oldTodoGroup);
		}

		return Result.success(retTodo);
	}

	@Override
	public Result<TodoGroup> updateTodoGroup(TodoGroup todoGroup) {
		todoGroupMapper.update(todoGroup);
		return Result.success(todoGroup);
	}

	private Result<Todo> checkUpdateTodo(Todo todo, Todo oldTodo) {
		if (Todo.S_DEL.equals(todo.getStatus()) && Todo.S_DEL.equals(oldTodo.getStatus())) {
			return Result.failWithCustomMessage("此任务已经被删除了");
		}

		// pending任务且预估时间和价值修改，需要重新进行动态规划, initial, pending, del, processing,finished
		// task内容修改，必定影响预估时间和价值，也必须要重新规划
		// pending,processing, finished预估时间和价值必须都被设置,且finished的实际用时也必须设置
		if (Todo.S_PENDING.equals(todo.getStatus())
				|| Todo.S_PROCESSING.equals(todo.getStatus())
				|| Todo.S_FINISHED.equals(todo.getStatus())) {
			if (Objects.isNull(oldTodo.getEstimateTime()) && Objects.isNull(todo.getEstimateTime())) {
				return Result.failWithCustomMessage("请设置预估时间");
			}

			if (Objects.isNull(oldTodo.getValue()) && Objects.isNull(todo.getValue())) {
				return Result.failWithCustomMessage("请设置价值");
			}

			if (Todo.S_FINISHED.equals(todo.getStatus())) {
				if (Objects.isNull(oldTodo.getRealityTime()) && Objects.isNull(todo.getRealityTime())) {
					return Result.failWithCustomMessage("请设置实际用时");
				}
			}
		}

		if (Objects.nonNull(oldTodo.getEstimateTime()) && Objects.nonNull(todo.getEstimateTime())) {
			if (!Objects.equals(oldTodo.getEstimateTime(), todo.getEstimateTime())
					&& !Todo.S_PENDING.equals(todo.getStatus())) {
				return Result.failWithCustomMessage("修改预估时间，必须重新动态规划");
			}
		}

		if (Objects.nonNull(oldTodo.getValue()) && Objects.nonNull(todo.getValue())) {
			if (!Objects.equals(oldTodo.getValue(), todo.getValue())
					&& !Todo.S_PENDING.equals(todo.getStatus())) {
				return Result.failWithCustomMessage("修改价值，必须重新动态规划");
			}
		}

		if (Objects.nonNull(oldTodo.getRealityTime()) && Objects.nonNull(todo.getRealityTime())) {
			if (!Objects.equals(oldTodo.getRealityTime(), todo.getRealityTime())
					&& !Todo.S_FINISHED.equals(todo.getStatus())) {
				return Result.failWithCustomMessage("修改实际用时，必须在完成任务后");
			}
		}

		return Result.success();
	}


	@Override
	public Result<List<TodoGroup>> searchTodoGroupList(Long uid, Integer offset, Integer count, Integer status, Long startTime, Long endTime, String sort) {
		List<TodoGroup> todoGroupList = todoGroupMapper.selectList(uid, offset, count, status, startTime, endTime, sort);
		return Result.success(todoGroupList);
	}

	@Override
	public Result<List<Todo>> searchTodoListByGroupId(Long groupId) {
		TodoGroup todoGroup = todoGroupMapper.select(groupId);
		if (Objects.isNull(todoGroup)) {
			return Result.fail(ErrorCode.USER_ILLEGAL_OPERATION);
		}
		return Result.success(todoMapper.selectTodoList(groupId));
	}

	@Override
	public Result<List<Todo>> searchTodoListByUidGroupId(Long uid, Long groupId) {
		TodoGroup todoGroup = todoGroupMapper.select(groupId);
		if (Objects.isNull(todoGroup) || Objects.isNull(todoGroup.getUid())) {
			return Result.fail(ErrorCode.USER_ILLEGAL_OPERATION);
		}

		if (!todoGroup.getUid().equals(uid)) {
			return Result.fail(ErrorCode.USER_ILLEGAL_OPERATION);
		}
		return Result.success(todoMapper.selectTodoList(groupId));
	}

	/**
	 * 按预估时间排序（最短的靠前）；最后按价值排序（最大的靠前)
	 * 没有预估时间和价值的，默认排最前
	 * @param todoList
	 */
	public void sortTodoList(List<Todo> todoList) {
		todoList.sort((o1, o2) -> {
			int o1Time = Optional.ofNullable(Todo.S_FINISHED.equals(o1.getStatus()) ? o1.getRealityTime() : o1.getEstimateTime()).orElse(1);
			int o2Time = Optional.ofNullable(Todo.S_FINISHED.equals(o2.getStatus()) ? o2.getRealityTime() : o2.getEstimateTime()).orElse(1);
			if (o1Time > o2Time) {
				return 1;
			} else if (o1Time < o2Time) {
				return -1;
			} else {
				int v1 = Optional.ofNullable(o1.getValue()).orElse(Todo.V_MAX_VALUE);
				int v2 = Optional.ofNullable(o2.getValue()).orElse(Todo.V_MAX_VALUE);
				if (v1 < v2) {
					return 1;
				} else {
					return -1;
				}
			}
		});
	}

	/**
	 * 先按状态排序，pending > del, 再按性价比排，value / time（1/480～100/1） 从大到小;
	 * 如果用时或价值不存在，默认性价比最高
	 * @param todoList
	 */
	public void sortTodoListByStatusCp(int maxTime, List<Todo> todoList) {
		todoList.sort((o1, o2) -> {
			if (o1.getStatus() > o2.getStatus()) {
				return 1;
			} else if (o1.getStatus() < o2.getStatus()) {
				return -1;
			} else {
				int o1Time = o1.getEstimateTime();
				int o2Time = o2.getEstimateTime();
				// 性价比
				int cp1 = Optional.ofNullable(o1.getValue()).orElse(Todo.V_MAX_VALUE) * maxTime / o1Time;
				int cp2 = Optional.ofNullable(o2.getValue()).orElse(Todo.V_MAX_VALUE) * maxTime / o2Time;
				if (cp1 < cp2) {
					return 1;
				} else {
					return -1;
				}
			}
		});
	}

	/**
	 * @decription 价值评判由优先级(越小权优先级高)×价值决定,因为要回溯找最优解，所以无法使用空间压缩的算法
	 * @param capacity
	 * @param todoList
	 * @return
	 */
	private int[][] calMaxValueByDp(int capacity, List<Todo> todoList) {
		int[][] dp = new int[todoList.size()][capacity + 1];
		for (int i = 1; i < todoList.size(); i++) {
			for (int j = 1; j <= capacity; j++) {
				if (j < todoList.get(i).getEstimateTime()) {
					dp[i][j] = dp[i - 1][j];
				} else {
					int newValue = dp[i - 1][j - todoList.get(i).getEstimateTime()] + calTodoValue(i, todoList);
					dp[i][j] = Math.max(dp[i - 1][j], newValue);
				}
			}
		}
		return dp;
	}


	/**
	 * @Author John
	 * @Description 通过回溯找到最优解
	 * @Date 2020/8/5 11:16 AM
	 * @Param
	 * @return
	 **/
	private void findWho(int i, int j, int[][] dp, List<Todo> todoList) {
		if (i >= 1) {
			if (dp[i][j] == dp[i - 1][j]) {
				todoList.get(i).setStatus(Todo.S_DEL);
				findWho(i - 1, j, dp, todoList);
			} else if (j - todoList.get(i).getEstimateTime() >= 0 && dp[i][j] == dp[i - 1][j - todoList.get(i).getEstimateTime()] + calTodoValue(i, todoList)) {
				todoList.get(i).setStatus(Todo.S_PENDING);
				findWho(i - 1, j - todoList.get(i).getEstimateTime(), dp, todoList);
			}
		}
	}


	/**
	 * @Author John
	 * @Description 计算任务价值
	 * @Date 2020/8/5 11:18 AM
	 * @Param
	 * @return
	 **/
	private int calTodoValue(int i, List<Todo> todoList) {
		return todoList.get(i).getValue() * (TodoGroup.PRIORITY_MAX_VALUE - todoList.get(i).getPriority() + 1);
	}
}
