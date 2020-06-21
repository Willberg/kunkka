package fun.johntaylor.kunkka.entity.user;

import lombok.Data;

@Data
public class user {
    private Long id;

    private String userName;

    private String password;

    private String phoneNumber;


    private String email;

    private String alias;

    private Long createTime;

    private Long updateTime;

    private Integer type;

    private Integer status;
}
