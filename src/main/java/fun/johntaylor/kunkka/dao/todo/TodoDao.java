package fun.johntaylor.kunkka.dao.todo;

import fun.johntaylor.kunkka.entity.todo.Todo;
import fun.johntaylor.kunkka.entity.todo.TodoList;
import fun.johntaylor.kunkka.mapper.todo.TodoListMapper;
import fun.johntaylor.kunkka.mapper.todo.TodoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Repository
@Slf4j
public class TodoDao {

	@Autowired
	private TodoListMapper todoListMapper;

	@Autowired
	private TodoMapper todoMapper;

	// 价值评判由优先级(越小权优先级高)×价值决定
	private static int[] calMaxValueByDp(int capacity, List<Todo> todoList) {
		int[] dp = new int[capacity + 1];
		for (int i = 0; i < todoList.size(); i++) {
			for (int j = capacity; j >= 0; j--) {
				if (j > todoList.get(i).getEstimateTime()) {
					int preValue = dp[j - todoList.get(i).getEstimateTime()] + todoList.get(i).getValue() * (TodoList.MAX_PRIORITY - todoList.get(i).getPriority());
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

	@Transactional(rollbackFor = Exception.class)
	public void addData(TodoList todoList, List<Todo> todos) {
		// 根据背包问题方案解决, f[v] = max{f[v], f[v-w[i]] +v[i]}
		int capacity = todoList.getMaxTime();
		if (Objects.nonNull(todoList.getId())) {
			capacity = capacity - todoListMapper.select(todoList.getId()).getTotalTime();
		}
		int[] dp = calMaxValueByDp(capacity, todos);
		log.debug("total value: " + dp[capacity]);

		int totalTime = 0;
		for (Todo t : todos) {
			// 优先级较高不可过滤
			if (Todo.S_DEL.equals(t.getStatus()) && todoList.getMinPriority() >= t.getPriority()) {
				t.setStatus(Todo.S_PENDING);
			}
			t.setCreateTime(System.currentTimeMillis());
			t.setUpdateTime(System.currentTimeMillis());

			// 不能超过总时长
			totalTime += t.getEstimateTime();
			if (totalTime > todoList.getMaxTime()) {
				t.setStatus(Todo.S_DEL);
			}

			if (Todo.S_PENDING.equals(t.getStatus())) {
				// 存在未完成任务
				int oldValue = Optional.ofNullable(todoList.getValue()).orElse(0);
				todoList.setValue(oldValue + t.getValue());
				todoList.setStatus(TodoList.S_PENDING);
			}
		}

		todoListMapper.insertWithUpdate(todoList);
		todos.forEach(t -> {
			t.setListId(todoList.getId());
			todoMapper.insert(t);
		});
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateData(Long id) {
		Todo todo = todoMapper.select(id);
		todo.setValue(id.intValue());
		todo.setUpdateTime(System.currentTimeMillis());
		todoMapper.update(todo);

		TodoList todoList = new TodoList();
		todoList.setId(todo.getListId());
		todoList.setUpdateTime(System.currentTimeMillis());
		todoList.setValue(50);
		todoListMapper.update(todoList);
	}
}
