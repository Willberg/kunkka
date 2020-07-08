drop table if exists `t_todo`;
create table `t_todo` (
	`id` bigint(20) not null auto_increment comment 'id',
	`task` varchar(50) default null comment '任务',
	`value` bigint(20) default null comment '价值',
	`estimate_time` bigint(20) default null comment '估计时间，分钟',
	`reality_time` bigint(20) default null comment '实际用时，分钟',
	`group_id` bigint(20) default null comment 'group id',
	`create_time` bigint(20) default null comment '创建时间',
	`update_time` bigint(20) default null comment '更新时间',
	`priority` int(10) default null comment '优先级，数字越小优先级越高，1--最高',
	`status` tinyint(4) default null comment '0--无效， 50--进行中， 100--完成',
	primary key(`id`),
	key(`group_id`)
)engine=InnoDB auto_increment=1;