drop table if exists `t_user`;
create table `t_user` (
	`id` bigint(20) not null auto_increment comment 'id',
	`user_name` varchar(50) default null comment '用户名',
	`password` varchar(50) default null comment '密码',
	`phone_number` varchar(50) default null comment '手机号',
	`email` varchar(50) default null comment '邮箱',
	`alias` varchar(50) default null comment '别名，用户自定义',
	`create_time` bigint(20) default null comment '创建时间',
	`update_time` bigint(20) default null comment '更新时间',
	`type` bigint(20) default null comment '用户类别， 1xxx--用户，2xxx-管理员',
	`status` bigint(20) default null comment '0--无效， 1--有效',
	primary key(`id`),
	unique key(`user_name`),
	unique key(`phone_number`),
	unique key(`email`)
)engine=InnoDB auto_increment=1;