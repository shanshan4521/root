 package com.axon.mercenary.common;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.axon.mercenary.common.Constants;
 
public class ProcessConfig {

	// 单例模式
	private static ProcessConfig instance = null;

	// 日志
	private Logger log = initLogger();

	private Logger initLogger() {
		PropertyConfigurator.configure("log4j.properties");
		Logger log = Logger.getLogger(ProcessConfig.class);
		return log;
	}
	
	//Properties实例
	private Properties property = new Properties();
	
	public synchronized static ProcessConfig getInstance() {
		if(instance == null){
			instance = new ProcessConfig();
		}
		return instance;
	}
	
	public synchronized static void resetInstance() {
		instance = null;
	}

	private ProcessConfig() {
		InputStream in = null;
		try {
			//读取配置文件userDataHandler.properties
			in = new BufferedInputStream(new FileInputStream(Constants.PROPERTY_FILENAME));
		} catch (FileNotFoundException e) {
			 log.error("未找到配置文件："+Constants.PROPERTY_FILENAME);
			 log.error(e);
			 throw new RuntimeException();
		}
		
		try {
			//解析配置文件streamMedia.properties
			this.property.load(in);
		} catch (IOException e) {
			log.error("配置文件"+Constants.PROPERTY_FILENAME+"加载失败");
			log.error(e);
			throw new RuntimeException();
		}		
	}
	
	public String getProperty(String name){
		return property.getProperty(name);
	}

	public boolean getProperty2Boolean(String name){
		return new Boolean(getProperty(name));
	}
	
	public int getProperty2Int(String name){
		int num = -1;
		try{
			num = Integer.valueOf(getProperty(name));
		}catch(NumberFormatException e){
		}
		return num;
	}
}
