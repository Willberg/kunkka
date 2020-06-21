drop table if exists `t_todo_list`;
create table `t_todo_list` (
	`id` bigint(20) not null auto_increment comment 'id',
	`value` bigint(20) default null comment '价值',
	`finish_value` bigint(20) default null comment '完成的价值',
	`total_time` bigint(20) default null comment '总时间，分钟',
	`max_time` bigint(20) default null comment '最多用时， 不能超过此用时时间',
  `min_priority` bigint(20) default null comment '最低优先级，高于此优先级的任务不可过滤',
	`create_time` bigint(20) default null comment '创建时间',
	`update_time` bigint(20) default null comment '更新时间',
	`status` tinyint(4) default null comment '状态，0--无效， 50--进行中， 100--完成',
	primary key(`id`)
)engine=InnoDB auto_increment=1;