package com.axon.automarket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.axon.automarket.common.Constants;
import com.axon.automarket.common.ProcessConfig;
import com.axon.automarket.db.MySqlAction;
import com.axon.automarket.db.ResultBean;
import com.axon.automarket.db.TaskBean;
import com.google.gson.Gson;

public class AutoMartket4User {

	// 日志
	private Logger log = LoggerFactory.getLogger(AutoMartket4User.class);

	private String id = "";
	private int retry = 1;
	private String socketIp = "";
	private int socketPort = 9527;

	public AutoMartket4User(String id) {
		this.id = id;
		ProcessConfig config = ProcessConfig.getInstance();
		this.retry = config.getProperty2Int(Constants.RETRYCOUNT);
		this.socketIp = config.getProperty(Constants.SOCKETIP);
		this.socketPort = config.getProperty2Int(Constants.SOCKETPORT);
	}

	public void run() {

		for (int i = 1; i < retry; i++) {
			MySqlAction action = new MySqlAction();
			TaskBean taskInfo = action.getTaskBean(id);
			if ("".equals(taskInfo.getId())) {
				log.error("未能取得任务信息，等待10s后重试。taskId:" + id);
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// do nothing
				}
				continue;
			}
			
			String json = null;
			try {
				Socket socket = new Socket(socketIp, socketPort);

				BufferedReader in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(),
						true);
				out.println(taskInfo.getModule_str());

				for (int j = 0; j < 3600; j++) {
					json = in.readLine();
					if (json != null && !"".equals(json)) {
						break;
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
				}


			} catch (Exception e) {
				log.error("多维度筛选失败，等待10s后重试。taskId:" + id);
				log.error(e.toString());
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					// do nothing
				}
				continue;
			}
			
			Gson gson = new Gson();
			ResultBean obj = gson.fromJson(json, ResultBean.class);  
			if(obj.getPushTable() == null){
				log.error("多维度筛选未能得到结果表，等待10s后重试。taskId:" + id);
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					// do nothing
				}
				continue;
			}
			
			try {
				action.updatePushTable(id,obj.getPushTable());
			} catch (SQLException e) {
				log.error("任务信息更新失败，等待10s后重试。taskId:" + id);
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					// do nothing
				}
				continue;
			}
			
			if("1".equals(taskInfo.getIs_push())){
				if(taskInfo.getModule_period() > 0){
					
				}else{
					
				}
			}			
		}
	}

	public static void main(String[] args) throws Exception {
		PropertyConfigurator.configure("log4j.properties");
		if (args.length > 0) {
			String taskId = args[0];
		} else {

		}
	}
}
