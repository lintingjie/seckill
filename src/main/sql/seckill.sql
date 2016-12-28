CREATE DATABASE seckill;

USE seckill;

CREATE TABLE seckill(
`seckill_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品库存id',
`name` VARCHAR(120) NOT NULL COMMENT '商品名称',
`number` INT NOT NULL COMMENT '库存数量',
`create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`start_time` TIMESTAMP NOT NULL COMMENT '秒杀开启时间',
`end_time` TIMESTAMP NOT NULL COMMENT '秒杀结束时间',
PRIMARY KEY(seckill_id),
KEY idx_start_time(start_time),
KEY idx_end_time(end_time),
KEY idx_create_time(create_time)
)ENGINE=INNODB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='秒杀库存表';

`success_killed`

CREATE TABLE success_killed(
`seckill_id` BIGINT NOT NULL COMMENT '秒杀商品id',
`user_phone` BIGINT NOT NULL COMMENT '用户手机号',
`state` TINYINT NOT NULL DEFAULT -1 COMMENT '状态标识：-1：无效 0：成功 1：已付款',2：已发货
`create_time` TIMESTAMP NOT NULL COMMENT '创建时间',
PRIMARY KEY(seckill_id,user_phone),/*联合主键*/
KEY ids_create_time(create_time)

)ENGINE=INNODB DEFAULT CHARSET = utf8 COMMENT='秒杀成功明细表';
