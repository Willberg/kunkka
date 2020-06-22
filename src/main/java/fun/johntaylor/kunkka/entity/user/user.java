package fun.johntaylor.kunkka.entity.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Data
public class user {
    private Long id;

    private String userName;

    private String password;

    @Pattern(regexp = "^[0-9]{11}$", message = "手机号不对")
    private String phoneNumber;

    @Email(message = "邮箱格式错误")
    private String email;

    private String alias;

    private Long createTime;

    private Long updateTime;

    private Integer type;

    private Integer status;
}
