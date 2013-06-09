DROP TABLE IF EXISTS `pdc_aide`.`schedule_task_info`;
CREATE TABLE  `pdc_aide`.`schedule_task_info` (
  `taskId` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '任务编号',
  `name` varchar(100) NOT NULL COMMENT '名称',
  `execTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '执行时间',
  `programUrl` varchar(200) NOT NULL DEFAULT '0' COMMENT '执行程序路径',
  `status` smallint(4) unsigned DEFAULT '0' COMMENT '0:未启动\r\n1:已启动\r\n2:停止',
  `pluralFlag` smallint(4) unsigned DEFAULT '0' COMMENT '是否可以启动多个实例0:不可以\r\n1:可以',
  `parameter` varchar(45) DEFAULT NULL COMMENT '启动可执行程序额的参数',
  `deadTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '过期时间',
  `firstExecTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '第一次执行时间',
  `lastExecTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '最新执行时间',
  `repeatTime` int(10) unsigned DEFAULT '0' COMMENT '间隔时间',
  `repeatUnit` varchar(20) DEFAULT 'DAY' COMMENT '间隔时间单位',
  `result` varchar(45) DEFAULT NULL COMMENT '执行结果',
  `description` varchar(1000) DEFAULT NULL COMMENT '任务描述',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '任务添加时间',
  `updateUser` varchar(45) DEFAULT NULL COMMENT '任务添加人',
  PRIMARY KEY (`taskId`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='任务调度服务配置表';