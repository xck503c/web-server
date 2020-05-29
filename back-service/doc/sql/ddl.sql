CREATE TABLE `user_info` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `user_id` varchar(50) NOT NULL COMMENT '账号id',
  `user_name` varchar(100) NOT NULL COMMENT '账号名称',
  `user_pwd` varchar(100) DEFAULT NULL,
  `mobile` varchar(500) DEFAULT NULL,
  `email` varchar(100) NOT NULL COMMENT '邮件',
  `status` int(11) NOT NULL COMMENT '状态',
  `insert_time` varchar(50) NOT NULL COMMENT '插入时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='用户信息表';

CREATE TABLE `prices_list` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `name` varchar(100) NOT NULL COMMENT '物品名称',
  `unit` varchar(50) DEFAULT NULL COMMENT '单价的计量单位',
  `type` int(3) DEFAULT 0 COMMENT '物品类型: 0-无分类',
  `place` varchar(200) DEFAULT '' COMMENT '物品购买地点',
  `operator` int(11) DEFAULT NULL COMMENT '操作人',
  `insert_time` varchar(50) NOT NULL COMMENT '插入时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='物品列表';

CREATE TABLE `food_prices_record` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `prices_id` int(11) NOT NULL COMMENT '对应物品列表的id',
  `name` varchar(100) NOT NULL COMMENT '食品名称',
  `price` double(15,3) NOT NULL COMMENT '食品单价',
  `is_discount` int(1) DEFAULT 0 COMMENT '是否有折扣: 0-无,1-有',
  `discount_info` varchar(200) DEFAULT '' COMMENT '折扣信息可用json存储',
  `unit` varchar(50) DEFAULT NULL COMMENT '单价的计量单位',
  `operator` int(11) DEFAULT NULL COMMENT '记录人',
  `insert_time` varchar(50) NOT NULL COMMENT '插入时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_prices_id` (`prices_id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='食品物价记录表';