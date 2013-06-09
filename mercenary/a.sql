DROP TABLE IF EXISTS `pdc_aide`.`schedule_task_info`;
CREATE TABLE  `pdc_aide`.`schedule_task_info` (
  `taskId` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '������',
  `name` varchar(100) NOT NULL COMMENT '����',
  `execTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'ִ��ʱ��',
  `programUrl` varchar(200) NOT NULL DEFAULT '0' COMMENT 'ִ�г���·��',
  `status` smallint(4) unsigned DEFAULT '0' COMMENT '0:δ����\r\n1:������\r\n2:ֹͣ',
  `pluralFlag` smallint(4) unsigned DEFAULT '0' COMMENT '�Ƿ�����������ʵ��0:������\r\n1:����',
  `parameter` varchar(45) DEFAULT NULL COMMENT '������ִ�г����Ĳ���',
  `deadTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '����ʱ��',
  `firstExecTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '��һ��ִ��ʱ��',
  `lastExecTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '����ִ��ʱ��',
  `repeatTime` int(10) unsigned DEFAULT '0' COMMENT '���ʱ��',
  `repeatUnit` varchar(20) DEFAULT 'DAY' COMMENT '���ʱ�䵥λ',
  `result` varchar(45) DEFAULT NULL COMMENT 'ִ�н��',
  `description` varchar(1000) DEFAULT NULL COMMENT '��������',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '�������ʱ��',
  `updateUser` varchar(45) DEFAULT NULL COMMENT '���������',
  PRIMARY KEY (`taskId`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='������ȷ������ñ�';