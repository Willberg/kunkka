drop table if exists `t_user`;
create table `t_user` (
	`id` bigint(20) not null auto_increment comment 'id',
	`user_name` varchar(50) default null comment '用户名',
	`password` varchar(50) default null comment '密码',
	`phone_number` varchar(50) default null comment '手机号',
	`email` varchar(50) default null comment '邮箱',
	`create_time` bigint(20) default null comment '创建时间',
	`update_time` bigint(20) default null comment '更新时间',
	`role_id` bigint(20) default null comment '用户角色， 1--用户，2-管理员',
	`status` bigint(20) default null comment '0--无效， 1--有效',
	primary key(`id`),
	unique key(`user_name`),
	key(`phone_number`),
	key(`email`)
)engine=InnoDB auto_increment=1;