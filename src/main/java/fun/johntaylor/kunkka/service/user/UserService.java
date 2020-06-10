package fun.johntaylor.kunkka.service.user;

import fun.johntaylor.kunkka.dao.user.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserDao userDao;
}
