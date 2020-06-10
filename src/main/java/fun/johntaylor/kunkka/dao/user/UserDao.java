package fun.johntaylor.kunkka.dao.user;

import fun.johntaylor.kunkka.mapper.user.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao {
    @Autowired
    private UserMapper userMapper;
}
