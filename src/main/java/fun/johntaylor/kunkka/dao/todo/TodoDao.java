package fun.johntaylor.kunkka.dao.todo;

import fun.johntaylor.kunkka.entity.todo.Todo;
import fun.johntaylor.kunkka.entity.todo.TodoList;
import fun.johntaylor.kunkka.mapper.todo.TodoListMapper;
import fun.johntaylor.kunkka.mapper.todo.TodoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;


@Repository
public class TodoDao {

    @Autowired
    private TodoListMapper todoListMapper;

    @Autowired
    private TodoMapper todoMapper;

    // 价值评判由权限×价值决定
    private static int[] calMaxValueByDp(int capacity, List<Todo> todoList) {
        int[] dp = new int[capacity + 1];
        for (int i = 0; i < todoList.size(); i++) {
            for (int j = capacity; j >= 0; j--) {
                if (j > todoList.get(i).getEstimateTime()) {
                    dp[i] = Math.max(dp[i], dp[j - todoList.get(i).getEstimateTime()] + todoList.get(i).getValue() * todoList.get(i).getPriority());

                }
            }
        }
        return dp;
    }

    // 找到最佳方案，设置状态
    private static void findWhat(int i, int j, int[] dp, List<Todo> todos, TodoList todoList) {
        if (i >= 1) {
            if (j - todos.get(i).getEstimateTime() >= 0 && dp[j] == dp[j - todos.get(i).getEstimateTime()] + todos.get(i).getValue() * todos.get(i).getPriority()) {
                todos.get(i).setStatus(Todo.S_PENDING);
                // 存在未完成任务
                todoList.setStatus(TodoList.S_PENDING);
                findWhat(i - 1, j - todos.get(i).getEstimateTime(), dp, todos, todoList);
            } else {
                todos.get(i).setStatus(Todo.S_DEL);
                findWhat(i - 1, j, dp, todos, todoList);
            }
        }
    }

    @Transactional
    public void addData(TodoList todoList, List<Todo> todos) {
        // 根据背包问题方案解决, f[v] = max{f[v], f[v-w[i]] +v[i]}
        int capacity = todoList.getMaxTime();
        if (Objects.nonNull(todoList.getId())) {
            capacity = capacity - todoListMapper.select(todoList.getId()).getTotalTime();
        }
        int[] dp = calMaxValueByDp(capacity, todos);

        // 找到最佳方案
        findWhat(todos.size() - 1, capacity, dp, todos, todoList);

        todoListMapper.insertWithUpdate(todoList);
        todos.forEach(t -> todoMapper.insert(t));
    }

    @Transactional
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
