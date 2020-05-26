drop table if exists `t_todo_list`;
create table `t_todo_list` (
	`id` bigint(20) not null auto_increment comment '',
	`value` bigint(20) default null comment '',
	`finish_value` bigint(20) default null comment '',
	`total_time` bigint(20) default null comment '',
	`create_time` bigint(20) default null comment '',
	`update_time` bigint(20) default null comment '',
	`status` bigint(20) default null comment '',
	primary key(`id`)
)engine=InnoDB auto_increment=1;