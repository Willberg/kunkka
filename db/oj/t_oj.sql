drop table if exists `t_oj`;
create table `t_oj`
(
    `id`          bigint(20) not null auto_increment comment 'id',
    `pid`         bigint(20) not null comment '题目ID',
    `uid`         bigint(20) not null comment 'uid',
    `name`        varchar(500)  not null comment '题目',
    `difficulty`  varchar(4)    not null comment '难度 简单, 中等, 困难',
    `oj_type`     varchar(50)   not null comment '题库',
    `type`        varchar(50)   not null comment '题目类别',
    `pre_time`    bigint(20) not null comment '开始或上一次暂停的时刻 毫秒',
    `use_time`    bigint(20) default '0' comment '时长 秒',
    `standalone`  varchar(2) default null comment '是否参考题解',
    `study`       varchar(2) default null comment '是否学习了题解',
    `link`        varchar(1000) not null comment '题目链接',
    `importance`  int(10) not null comment '重要程度',
    `create_time` bigint(20) not null comment '创建时间',
    `update_time` bigint(20) default null comment '更新时间',
    `status`      tinyint       not null comment '状态， 1-- 开始, 2-- 暂停, 3-- 结束, 4-- 删除',
    primary key (`id`),
    key(`pid`),
    key(`uid`)
)engine=InnoDB auto_increment=1;