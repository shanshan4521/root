package com.axon.mercenary.common;

import java.util.HashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import com.axon.mercenary.db.ScheduleTaskInfoBean;

public class Constants {
	// 配置文件名
	public final static String PROPERTY_FILENAME = "mercenary.properties";

	// 数据库配置信息
	public static final String MYSQL_URL = "mySqlUrl";
	public static final String MYSQL_USER = "mySqlUserName";
	public static final String MYSQL_PW = "mySqlPassWord";

	public static HashMap<String, ScheduleTaskInfoBean> currentTaskInfoMap = new HashMap<String, ScheduleTaskInfoBean>();
	public static HashMap<String, ScheduleTaskInfoBean> oldTaskInfoMap = new HashMap<String, ScheduleTaskInfoBean>();
	public static ConcurrentSkipListSet<String> executingJobList = new ConcurrentSkipListSet<String>();

	public static final String TASKBEAN = "taskbean";
	public static final String MAIL_SERVER_HOST = "mailServerHost";
	public static final String MAIL_SERVER_PORT = "mailServerPort";
	public static final String MAIL_USER_NAME = "mailUserName";
	public static final String MAIL_PASSWORD = "mailPassword";
	public static final String MAIL_TO_ADDRESS = "toAddress";
	
	public static final String TASK_TIMEOUT = "taskTimeOut";
	
	public static final String CITY = "city";

}
