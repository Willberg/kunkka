drop table if exists `t_cipher`;
create table `t_cipher` (
	`id` bigint(20) not null auto_increment comment 'id',
	`uid` bigint(20) default null comment 'uid',
	`name` varchar(50) default null comment '网站名',
	`user_name` varchar(50) default null comment '用户名',
	`password` varchar(16) default null comment '密码明文',
	`salt` varchar(16) default null comment 'salt',
	`email` varchar(50) default null comment '邮箱',
	`phone_number` varchar(50) default null comment '手机号',
	`link` varchar(50) default null comment '登录链接',
	`create_time` bigint(20) default null comment '创建时间',
	`update_time` bigint(20) default null comment '更新时间',
	`status` bigint(20) default null comment '状态 1--正常, 2--删除',
	primary key(`id`),
	key(`uid`)
)engine=InnoDB auto_increment=1;