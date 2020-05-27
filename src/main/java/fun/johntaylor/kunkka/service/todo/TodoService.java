package fun.johntaylor.kunkka.service.todo;

import fun.johntaylor.kunkka.dao.todo.TodoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TodoService {

    @Autowired
    private TodoDao todoDao;

    public void add() {
        todoDao.addData();
    }

    public void update(Long id) {
        todoDao.updateData(id);
    }
}
