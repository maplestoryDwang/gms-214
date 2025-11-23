CREATE TABLE `trace_king_user_info` (
                                        `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键自增',
                                        `chrid` INT NOT NULL COMMENT '角色id',
                                        `shopNpc` INT DEFAULT NULL COMMENT '当前所在shop npc',
                                        `cWeight` INT DEFAULT 0 COMMENT '当前承受重量',
                                        `count` INT DEFAULT 0 COMMENT '当前金币数量',
                                        `mWeight` INT DEFAULT 0 COMMENT '最大承重',
                                        `scount` INT DEFAULT 0 COMMENT '总容量',
                                        `worker` VARCHAR(255) DEFAULT NULL COMMENT '工人配置，如 1=4;4=1',
                                        `ride` INT DEFAULT NULL COMMENT '选择的坐骑ID',

                                        `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

                                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色TradeKing追踪信息表';