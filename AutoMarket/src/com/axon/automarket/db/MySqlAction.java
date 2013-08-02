package com.axon.automarket.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySqlAction {
	// 日志
	private Logger log = LoggerFactory.getLogger(MySqlAction.class);

	public TaskBean getTaskBean(String id) {
		String selectSql = "select id, module_type, module_name,module_str,"
				+ "module_cid, is_push, push_method, is_autosend,"
				+ "is_second, task_etime, module_last_etime,"
				+ "module_period, task_type, task_title, "
				+ "task_msg,repeatFlag,push_num,sp_code  from pdc.task_model where id = " + id;
		MySqlService service = new MySqlService();
		Statement stmt = null;
		ResultSet rs = null;
		TaskBean taskInfo = new TaskBean();
		try {
			stmt = service.getConnection().createStatement();
			rs = stmt.executeQuery(selectSql);
			if (rs.next()) {
				taskInfo.setId(rs.getString("id"));
				taskInfo.setModule_type(rs.getString("module_type"));
				taskInfo.setModule_name(rs.getString("module_name"));
				taskInfo.setModule_str(rs.getString("module_str"));
				taskInfo.setModule_cid(rs.getString("module_cid"));
				taskInfo.setIs_push(rs.getString("is_push"));
				taskInfo.setPush_method(rs.getString("push_method"));
				taskInfo.setIs_autosend(rs.getString("is_autosend"));
				taskInfo.setIs_second(rs.getString("is_autosend"));
				taskInfo.setTask_etime(rs.getLong("task_etime"));
				taskInfo.setModule_last_etime(rs.getLong("module_last_etime"));
				taskInfo.setModule_period(rs.getInt("module_period"));
				taskInfo.setTask_type(rs.getString("task_type"));
				taskInfo.setTask_title(rs.getString("task_title"));
				taskInfo.setTask_msg(rs.getString("task_msg"));
				taskInfo.setRepeatFlag(rs.getInt("repeatFlag"));
				taskInfo.setPush_num(rs.getInt("push_num"));
				taskInfo.setSp_code(rs.getInt("sp_code"));
			}
		} catch (SQLException e) {
			log.error("任务信息读取失败！");
			log.error(e.toString());
			// throw e;
		} finally {
			service.releaseResultSet(rs);
			service.releaseStatement(stmt);
			service.releaseConnection();
		}
		return taskInfo;
	}

	public void updatePushTable(String id, String pushTable, int userCount)
			throws SQLException {
		String updateSql = "update pdc.task_model  set user_table = '"
				+ pushTable
				+ "',module_last_etime = unix_timestamp(now()), user_num = "+userCount+" where id = "
				+ id;
		MySqlService service = new MySqlService();
		Statement stmt = null;
		try {
			stmt = service.getConnection().createStatement();
			stmt.execute(updateSql);
		} catch (SQLException e) {
			log.error("任务信息更新失败！");
			log.error(e.toString());
			throw e;
		} finally {
			service.releaseStatement(stmt);
			service.releaseConnection();
		}
	}

	public void insertPTask(TaskBean taskInfo, String pushTable, int userCount)
			throws SQLException {
		MnsCdrBean bean = getMnsCdrBean(taskInfo.getPush_method(),
				taskInfo.getModule_cid());
		String[] link = bean.getRes_link().split("#");
		String res_link = "";
		if(link.length > 0){
			res_link = link[0];
		}
		
		String muduleName ="";
		if ("3".equals(taskInfo.getPush_method())) {
			//彩信
		}else{
			muduleName = "【"+taskInfo.getModule_name()+"】";
		}
		StringBuilder insertSql = new StringBuilder();
		insertSql
				.append("insert into pdc.p_task (task_type, ")
				.append("push_type, task_title, task_url, push_name,")
				.append("cid, exe_time, push_method, ")
				.append("if_execute, ctime, user_num, success_num,")
				.append("ischecked, click_pv, click_uv, where_str,")
				.append("user_filter, areano, uid, status, addtest,")
				.append("is_mutex, is_second,module_id,type_id,sp_num,yxmsg) values( ")
				.append(taskInfo.getModule_type()).append(",")
				.append(taskInfo.getPush_method()).append(",'")
				.append(muduleName.replaceAll("'", "''"))
				.append(bean.getTitle().replaceAll("'", "''"))
				.append("','").append(res_link.replaceAll("'", "''"))
				.append("','")
				.append(taskInfo.getTask_title().replaceAll("'", "''"))
				.append(bean.getTitle().replaceAll("'", "''"))				
				.append("',").append(taskInfo.getModule_cid()).append(",")
				.append(taskInfo.getTask_etime()).append(",")
				.append(taskInfo.getPush_method())
				.append(",0,unix_timestamp(now()),").append(userCount)
				.append(",0,").append(taskInfo.getIs_autosend()).append(",0,0,'select phone from ").append(pushTable)
				.append("','").append(taskInfo.getModule_name().replaceAll("'", "''")).append("',99999,0,0,1,1,")
				.append(taskInfo.getIs_second()).append(",")
				.append(taskInfo.getId()).append(",")
				.append(taskInfo.getTask_type()).append(",").append(taskInfo.getSp_code())
				.append(",'").append(taskInfo.getTask_msg().replaceAll("'", "''")).append("')");

		MySqlService service = new MySqlService();
		Statement stmt = null;
		try {
			stmt = service.getConnection().createStatement();
			stmt.execute(insertSql.toString());
		} catch (SQLException e) {
			log.error("营销任务更新失败！");
			log.error(insertSql.toString());
			log.error(e.toString());
			throw e;
		} finally {
			service.releaseStatement(stmt);
			service.releaseConnection();
		}

	}

	private MnsCdrBean getMnsCdrBean(String push_method, String module_cid) {
		String selectSql = "";
		MnsCdrBean tempBean = new MnsCdrBean();
		if ("3".equals(push_method)) {
			// 读取彩信的状况
			selectSql = "select title,areano,uid, '' as  res_link from pdc.mms where mmsid = "
					+ module_cid;
		} else {
			// 读取短信的状况
			selectSql = "select res_title as title,areano,uid,res_link from pdc.cdr where id = "
					+ module_cid;
		}

		MySqlService service = new MySqlService();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = service.getConnection().createStatement();
			rs = stmt.executeQuery(selectSql);
			if (rs.next()) {
				tempBean.setTitle(rs.getString("title"));
				tempBean.setAreano(rs.getString("areano"));
				tempBean.setUid(rs.getString("uid"));
				tempBean.setRes_link(rs.getString("res_link"));
			}
		} catch (SQLException e) {
			log.error("短彩信信息读取失败！");
			log.error(selectSql);
			log.error(e.toString());
			// throw e;
		} finally {
			service.releaseResultSet(rs);
			service.releaseStatement(stmt);
			service.releaseConnection();
		}

		return tempBean;
	}

	public void splitPushTable(int j, int userCount, int module_period,
			String pushTable, int repeatFlag) throws SQLException {

		MySqlService service = new MySqlService();
		Statement stmt = null;
		ResultSet rs = null;

		String tableName = pushTable + "_" + j;
		String dropTable = "drop table if exists " + tableName;
		String createTable = "CREATE TABLE  " + tableName
				+ "( phone bigint(13) unsigned NOT NULL )"
				+ " ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;";
		String insertSql = "";
		String selectSql = "";
		int num = userCount / module_period;
		if (repeatFlag == 1) {
			if (j == 0) {
				insertSql = "insert into " + tableName + " select phone from "
						+ pushTable + " order by phone limit " + num;
			} else {
				String maxphone = null;
				String tempTable = pushTable + "_" + (j - 1);
				selectSql = "select max(phone) as phone from " + tempTable;
				try {
					stmt = service.getConnection().createStatement();
					rs = stmt.executeQuery(selectSql);
					if (rs.next()) {
						maxphone = rs.getString("phone");
					}
				} catch (SQLException e) {
					log.error("前回最大phone取得失败！tableName:" + tempTable);
					log.error(selectSql);
					log.error(e.toString());
					throw e;
				} finally {
					service.releaseResultSet(rs);
					service.releaseStatement(stmt);
				}
				insertSql = "insert into " + tableName + " select phone from "
						+ pushTable + " where phone > " + maxphone + " order by phone limit "
						+ num;
				if (j == module_period - 1) {
					insertSql = "insert into " + tableName
							+ " select phone from " + pushTable
							+ " where phone > " + maxphone;
				}
			}
		} else {
			insertSql = "insert into " + tableName + " select phone from "
					+ pushTable;
		}
		try {
			stmt = service.getConnection().createStatement();
			stmt.execute(dropTable);
			stmt.execute(createTable);
			stmt.execute(insertSql);
		} catch (SQLException e) {
			log.error("分表失败！");
			log.error(dropTable);
			log.error(createTable);
			log.error(insertSql);
			log.error(e.toString());
			throw e;
		} finally {
			service.releaseStatement(stmt);
			service.releaseConnection();
		}
	}
	
	public void updateTask4Push(String id, int pushNum)
			throws SQLException {
		pushNum = pushNum+1;
		String updateSql = "update pdc.task_model  set push_num = '"
				+ pushNum
				+ "',last_push_time = unix_timestamp(now())  where id = "
				+ id;
		MySqlService service = new MySqlService();
		Statement stmt = null;
		try {
			stmt = service.getConnection().createStatement();
			stmt.execute(updateSql);
		} catch (SQLException e) {
			log.error("营销任务生成后，任务信息更新失败！");
			log.error(e.toString());
			throw e;
		} finally {
			service.releaseStatement(stmt);
			service.releaseConnection();
		}
	}
}
