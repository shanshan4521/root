 package com.axon.mercenary.common;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.axon.mercenary.db.ScheduleTaskInfoBean;
 
public class Constants {
	//配置文件名
	public final static String PROPERTY_FILENAME = "mercenary.properties";
	
	//数据库配置信息
	public static final String MYSQL_URL = "mySqlUrl";
	public static final String MYSQL_USER = "mySqlUserName";
	public static final String MYSQL_PW = "mySqlPassWord";
	
	public static HashMap currentTaskInfoMap = new HashMap<String, ScheduleTaskInfoBean>();
	public static HashMap oldTaskInfoMap = new HashMap<String, ScheduleTaskInfoBean>();
	
}
