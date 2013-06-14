 package com.axon.mercenary.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.axon.mercenary.common.ProcessConfig;
import com.axon.mercenary.common.Constants;
 
public class MySqlService {
	// 日志
	private Logger log =  LoggerFactory.getLogger(MySqlService.class);
	
	private Connection conn = null;

	// MySQL的URL
	private String mySqlUrl = null;

	// 用户名
	private String user = null;

	// 密码
	private String passWord = null;

	public MySqlService() {
		// 从mercenary.properties读取MYSQL的配置
		ProcessConfig config = ProcessConfig.getInstance();
		mySqlUrl = config.getProperty(Constants.MYSQL_URL);
		user = config.getProperty(Constants.MYSQL_USER);
		passWord = config.getProperty(Constants.MYSQL_PW);
	}
	
	public MySqlService(String ip, int prot,String user, String passWord) {
		mySqlUrl = "jdbc:mysql://"+ip+":"+prot;
		this.user = user;
		this.passWord = passWord;
	}
	// 取得数据库连接
	public Connection getConnection() throws SQLException {
		try {
			conn = DriverManager.getConnection(mySqlUrl, user, passWord);
		} catch (SQLException e) {
			log.error("MYSQL连接取得失败！！");
			log.error("url:" + mySqlUrl+"  User:"+user+"  PassWord:"+passWord);
			log.error(e.toString());
			throw e;
		}
		return conn;
	}

	// 释放数据库连接
	public void releaseConnection() {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				// do nohting
			}
		}
	}

	public void releaseResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				// do nothing
			}
		}
	}

	public void releaseStatement(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				// do nothing
			}
		}
	}
	

}
