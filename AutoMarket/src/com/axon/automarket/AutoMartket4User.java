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
		log.info("营销任务自动生成开始，task_ID："+id);
		
		MySqlAction action = new MySqlAction();
		TaskBean taskInfo = new TaskBean();
		int i = 0;
		// 取得任务信息。
		while (true) {
			taskInfo = action.getTaskBean(id);
			if ("".equals(taskInfo.getId())) {
				i++;
				// 失败时重试。
				if (i < retry) {
					log.error("未能取得任务信息，等待10s后重试第" + (i + 1) + "次。taskId:" + id);
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// do nothing
					}
					continue;
				} else {
					log.error("营销任务自动生成失败，task_ID："+id);
					return;
				}

			}
			break;
		}

		String json = null;
		ResultBean obj = new ResultBean();
		// obj.setPushTable("userdata_4.t_push_zhonghai_0_20121208133452");
		// obj.setUserCount(555);

		i = 0;
		// 多维度筛选
		while (true) {
			try {
				Socket socket = new Socket(socketIp, socketPort);

				BufferedReader in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(),
						true);
				out.println(taskInfo.getModule_str());

				for (int j = 0; j < 1800; j++) {
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
				i++;
				if (i < retry) {
					log.error("多维度筛选失败，等待10s后重试重试第" + (i + 1) + "次。taskId:"
							+ id);
					log.error(e.toString());
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e1) {
						// do nothing
					}
					continue;
				} else {
					log.error("营销任务自动生成失败，task_ID："+id);
					return;
				}
			}

			Gson gson = new Gson();
			obj = gson.fromJson(json, ResultBean.class);
			if (obj.getPushTable() == null) {
				i++;
				// 失败时重试。
				if (i < retry) {
					log.error("多维度筛选未能得到结果表，等待10s后重试第" + (i + 1) + "次。taskId:"
							+ id);
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e1) {
						// do nothing
					}
					continue;
				} else {
					log.error("营销任务自动生成失败，task_ID："+id);
					return;
				}

			}
			break;
		}
		obj.setPushTable("pdc_temp."+obj.getPushTable());
		i = 0;
		// 更新任务信息表
		while (true) {
			try {
				action.updatePushTable(id, obj.getPushTable(),obj.getUserCount());
			} catch (SQLException e) {
				i++;
				if (i < retry) {
					log.error("任务信息更新失败，等待10s后重试第" + (i + 1) + "次。taskId:" + id);
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e1) {
						// do nothing
					}
					continue;
				} else {
					log.error("营销任务自动生成失败，task_ID："+id);
					return;
				}
			}
			break;
		}

		// 更新营销任务表
		if ("1".equals(taskInfo.getIs_push())) {
			if (taskInfo.getModule_period() > 1 ) {
				// 分表
				for (int j = 0; j < taskInfo.getModule_period(); j++) {
					i = 0;
					while (true) {
						try {
							action.splitPushTable(j, obj.getUserCount(),
									taskInfo.getModule_period(),
									obj.getPushTable(),taskInfo.getRepeatFlag());
						} catch (SQLException e) {
							i++;
							if (i < retry) {
								log.error("分表失败，等待10s后重试第" + (i + 1)
										+ "次。table:" + id + "  table:"
										+ obj.getPushTable());
								try {
									Thread.sleep(10000);
								} catch (InterruptedException e1) {
									// do nothing
								}
								continue;
							} else {
								log.error("营销任务自动生成失败，task_ID："+id);
								return;
							}
						}
						break;
					}
				}

				// 更新营销任务表
				for (int j = 0; j < taskInfo.getModule_period(); j++) {
					i = 0;
					while (true) {
						int num = obj.getUserCount();
						if(taskInfo.getRepeatFlag() == 1){
							num = obj.getUserCount()/taskInfo.getModule_period();
							if (j == taskInfo.getModule_period() - 1) {
								num = obj.getUserCount() - num * j;
							}
						}
						String table = obj.getPushTable() + "_" + j;
						if(j != 0){
							taskInfo.setTask_etime(taskInfo.getTask_etime()+24*60*60);
						}
						try {
							action.insertPTask(taskInfo, table, num);
						} catch (SQLException e) {
							i++;
							if (i < retry) {
								log.error(" 更新营销任务表失败，等待10s后重试第" + (i + 1)
										+ "次。table:" + id + "  table:" + table);
								try {
									Thread.sleep(10000);
								} catch (InterruptedException e1) {
									// do nothing
								}
								continue;
							} else {
								log.error("营销任务自动生成失败，task_ID："+id);
								return;
							}
						}
						break;
					}
				}

			} else {
				// 不分表
				i = 0;
				while (true) {
					try {
						action.insertPTask(taskInfo, obj.getPushTable(),
								obj.getUserCount());
					} catch (SQLException e) {
						i++;
						if (i < retry) {
							log.error(" 更新营销任务表失败，等待10s后重试第" + (i + 1)
									+ "次。table:" + id + "  table:"
									+ obj.getPushTable());
							try {
								Thread.sleep(10000);
							} catch (InterruptedException e1) {
								// do nothing
							}
							continue;
						} else {
							log.error("营销任务自动生成失败，task_ID："+id);
							return;
						}
					}
					break;
				}
			}
			try {
				action.updateTask4Push(id, taskInfo.getPush_num());
			} catch (SQLException e) {
				log.error("营销任务自动生成失败，task_ID："+id);
			}
		}
		log.info("营销任务自动生成完毕，task_ID："+id);
	}

	public static void main(String[] args) throws Exception {
		PropertyConfigurator.configure("log4j.properties");
		if (args.length > 0) {
			String taskId = args[0];
			AutoMartket4User m = new AutoMartket4User(taskId);
			m.run();
		}
	}
}
