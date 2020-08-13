drop table if exists `t_timer`;
create table `t_timer` (
	`id` bigint(20) not null auto_increment comment 'id',
	`uid` bigint(20) default null comment 'uid',
	`create_time` bigint(20) default null comment '创建时间',
	`type` bigint(20) default null comment '类别， 1-- 工作， 2--吃饭， 3--休闲娱乐， 4--睡觉, 5--学习, 6--未知',
	`related_id` bigint(20) default null comment '关联ID',
	`status` bigint(20) default null comment '状态， 1-- 开始， 2--结束',
	primary key(`id`),
	key(`uid`),
	key(`related_id`)
)engine=InnoDB auto_increment=1;