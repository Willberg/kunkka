drop table if exists `t_funds`;
create table `t_funds` (
	`id` bigint(20) not null auto_increment comment 'ID',
	`uid` bigint(20) default null comment 'uid',
	`amount` decimal(20,2) default '0.00' comment '金额',
  `memo` varchar(200) default null comment '备注',
	`create_time` bigint(20) default null comment '创建时间',
	`update_time` bigint(20) default null comment '更新时间',
	`category` bigint(20) default null comment '类别',
	`type` bigint(20) default null comment '类型',
	`status` bigint(20) default null comment '状态',
	primary key(`id`),
	key(`uid`)
)engine=InnoDB auto_increment=1;