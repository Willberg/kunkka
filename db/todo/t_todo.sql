drop table if exists `t_todo`;
create table `t_todo` (
	`id` bigint(20) not null auto_incrementcomment '',
	`task` varchar(50) default null comment '',
	`value` bigint(20) default null comment '',
	`estimate_time` bigint(20) default null comment '',
	`reality_time` bigint(20) default null comment '',
	`list_id` bigint(20) default null comment '',
	`create_time` bigint(20) default null comment '',
	`update_time` bigint(20) default null comment '',
	`status` bigint(20) default null comment '',
	primary key(`id`)
)engine=InnoDB auto_increment=1;