package fun.johntaylor.kunkka.service.todo.impl;

import fun.johntaylor.kunkka.entity.todo.Todo;
import fun.johntaylor.kunkka.entity.todo.TodoList;
import fun.johntaylor.kunkka.repository.mybatis.todo.TodoListMapper;
import fun.johntaylor.kunkka.repository.mybatis.todo.TodoMapper;
import fun.johntaylor.kunkka.service.todo.TodoService;
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
	private TodoListMapper todoListMapper;

	@Autowired
	private TodoMapper todoMapper;


	public void add(Todo todo) {
//        todoDao.addData(todo);
	}

	@Override
	public void addPatch(Integer maxTime, Integer minPriority, List<Todo> todos) {
		if (todos.size() == 0) {
			return;
		}

		// 初始化TodoList
		TodoList todoList = new TodoList();
		todoList.setUid(1L);
		todoList.setId(todos.get(0).getListId());
		todoList.setMinPriority(minPriority);
		todoList.setMaxTime(maxTime);
		int totalTime = 0;
		for (Todo t : todos) {
			totalTime += t.getEstimateTime();
		}
		todoList.setTotalTime(totalTime);
		todoList.setCreateTime(System.currentTimeMillis());
		todoList.setUpdateTime(System.currentTimeMillis());
		todoList.setStatus(TodoList.S_FINISHED);

		// 按优先级给todos排序
		todos.sort((o1, o2) -> {
			if (o1.getPriority() < o2.getPriority()) {
				return 1;
			} else {
				return -1;
			}
		});
		addData(todoList, todos);
	}

	public void update(Long id) {
		updateData(id);
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
