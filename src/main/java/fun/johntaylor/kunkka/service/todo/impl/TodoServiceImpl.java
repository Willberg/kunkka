package fun.johntaylor.kunkka.service.todo.impl;

import fun.johntaylor.kunkka.entity.todo.Todo;
import fun.johntaylor.kunkka.entity.todo.TodoGroup;
import fun.johntaylor.kunkka.repository.mybatis.todo.TodoGroupMapper;
import fun.johntaylor.kunkka.repository.mybatis.todo.TodoMapper;
import fun.johntaylor.kunkka.service.todo.TodoService;
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
	public Result<TodoGroup> addTodo(Todo todo) {
		return null;
	}

	/**
	 * 需要调用该方法，且需要支持事务特性的调用方，是在 @Transactional所在的类的外面
	 * @param todoGroup
	 * @param todoList
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result<TodoGroup> addPatch(TodoGroup todoGroup, List<Todo> todoList) {
		// 根据背包问题方案解决, f[v] = max{f[v], f[v-w[i]] +v[i]}
		int capacity = todoGroup.getMaxTime();
		if (Objects.nonNull(todoGroup.getId())) {
			capacity = capacity - todoGroupMapper.select(todoGroup.getId()).getTotalTime();
		}
		int[] dp = calMaxValueByDp(capacity, todoList);
		log.debug("total value: " + dp[capacity]);

		int totalTime = 0;
		for (Todo t : todoList) {
			// 优先级较高不可过滤
			if (Todo.S_DEL.equals(t.getStatus()) && todoGroup.getMinPriority() >= t.getPriority()) {
				t.setStatus(Todo.S_PENDING);
			}
			t.setCreateTime(System.currentTimeMillis());
			t.setUpdateTime(System.currentTimeMillis());

			// 不能超过总时长
			totalTime += t.getEstimateTime();
			if (totalTime > todoGroup.getMaxTime()) {
				t.setStatus(Todo.S_DEL);
			}

			if (Todo.S_PENDING.equals(t.getStatus())) {
				// 存在未完成任务
				int oldValue = Optional.ofNullable(todoGroup.getValue()).orElse(0);
				todoGroup.setValue(oldValue + t.getValue());
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
	public Result<List<TodoGroup>> searchTodoGroupList(Long uid, Integer offset, Integer count, Long timeMillis) {
		return null;
	}

	@Override
	public Result<List<Todo>> searchTodoListByGroupId(Long id, Long uid) {
		return null;
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
						todoList.get(i).setStatus(Todo.S_PENDING);
					} else {
						todoList.get(i).setStatus(Todo.S_DEL);
					}
				}
			}
		}
		return dp;
	}
}
