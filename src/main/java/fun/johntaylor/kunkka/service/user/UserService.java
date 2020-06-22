package fun.johntaylor.kunkka.service.user;

import fun.johntaylor.kunkka.repository.mybatis.user.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {
	@Autowired
	private UserMapper userMapper;
}
