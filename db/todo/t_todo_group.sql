drop table if exists `t_todo_group`;
create table `t_todo_group`
(
    `id`           bigint(20) not null auto_increment comment 'id',
    `uid`          bigint(20) default null comment '用户ID',
    `value`        bigint(20) default null comment '价值',
    `finish_value` bigint(20) default null comment '完成的价值',
    `total_time`   bigint(20) default null comment '总时间，分钟',
    `max_time`     bigint(20) default null comment '最多用时， 不能超过此用时时间',
    `min_priority` bigint(20) default null comment '最低优先级，高于此优先级的任务不可过滤',
    `create_time`  bigint(20) default null comment '创建时间',
    `update_time`  bigint(20) default null comment '更新时间',
    `is_private`   tinyint(2) default '0' comment '是否私有，1-- 是，0-- 否',
    `status`       tinyint(4) default null comment '状态，0--无效， 50--进行中， 100--完成',
    primary key (`id`),
    key(`uid`)
)engine=InnoDB auto_increment=1;