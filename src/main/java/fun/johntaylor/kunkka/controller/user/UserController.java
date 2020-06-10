package fun.johntaylor.kunkka.controller.user;

import fun.johntaylor.kunkka.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
}
