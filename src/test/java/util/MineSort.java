package util;

import fun.johntaylor.kunkka.entity.todo.Todo;
import fun.johntaylor.kunkka.service.todo.impl.TodoServiceImpl;
import fun.johntaylor.kunkka.utils.json.JsonUtil;
import org.junit.jupiter.api.Test;

import java.util.*;

public class MineSort {
	@Test
	public void testTodoSort() {
		int maxTime = 480;
		TodoServiceImpl todoService = new TodoServiceImpl();
		int[] statusArr = {1, 10, 50, 100};
		List<Todo> todoList = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			Todo t = new Todo();
			Double time = Math.random() * maxTime;
			t.setEstimateTime(time.intValue());
			Double value = Math.random() * 100;
			t.setValue(value.intValue());
			Double statusValue = Math.random() * 4;
			t.setStatus(statusArr[statusValue.intValue()]);
			if (Todo.S_FINISHED.equals(t.getStatus())) {
				time = Math.random() * maxTime;
				t.setRealityTime(time.intValue());
			}
			todoList.add(t);
			System.out.println(JsonUtil.toJson(t));
		}

		// 加入到map
		Map<Integer, List<Todo>> retMap = new HashMap<>(5);
		todoList.forEach(o -> {
			List<Todo> todoListTmp = retMap.get(o.getStatus());
			if (Objects.isNull(todoListTmp)) {
				todoListTmp = new LinkedList<>();
				retMap.put(o.getStatus(), todoListTmp);
			}
			todoListTmp.add(o);
		});

		todoService.sortTodoList(todoList);
		System.out.println("排序后：");
		todoList.forEach(t -> {
			System.out.println(JsonUtil.toJson(t));
		});

		todoService.sortTodoListByCp(maxTime, todoList);
		System.out.println("排序后：");
		todoList.forEach(t -> {
			int time = Todo.S_FINISHED.equals(t.getStatus()) ? t.getRealityTime() : t.getEstimateTime();
			System.out.print("cp:" + (t.getValue() * maxTime / time));
			System.out.println(JsonUtil.toJson(t));
		});

		System.out.println("排序后：");
		retMap.forEach((s, l) -> todoService.sortTodoListByCp(maxTime, l));
		retMap.forEach((s, l) -> {
			System.out.print(s + ":");
			l.forEach(o -> System.out.println(JsonUtil.toJson(o)));
		});
	}
}
