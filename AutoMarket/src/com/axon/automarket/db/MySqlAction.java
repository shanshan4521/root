package com.axon.automarket.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
public class MySqlAction {
	// 日志
	private Logger log = LoggerFactory.getLogger(MySqlAction.class);

	
	
	public TaskBean getTaskBean(String id){
		String selectSql = "select module_type," +
				"module_str," +
				"module_cid," +
				"is_push," +
				"push_method," +
				"is_autosend," +
				"is_second," +
				"task_etime," +
				"module_last_etime," +
				"module_period " +
				" from pdc.task_model where id = "+id;
		MySqlService service = new MySqlService();
		Statement stmt = null;
		ResultSet rs = null;
		TaskBean taskInfo = new TaskBean();
		try {
			stmt = service.getConnection().createStatement();
			rs = stmt.executeQuery(selectSql);
			if (rs.next()) {
				taskInfo.setId(id);
				taskInfo.setModule_type(rs.getString("module_type"));
				taskInfo.setModule_str(rs.getString("module_str"));
				taskInfo.setModule_cid(rs.getString("module_cid"));
				taskInfo.setIs_push(rs.getString("is_push"));
				taskInfo.setPush_method(rs.getString("push_method"));
				taskInfo.setIs_autosend(rs.getString("is_autosend"));
				taskInfo.setIs_second(rs.getString("is_autosend"));
				taskInfo.setTask_etime(rs.getLong("task_etime"));
				taskInfo.setModule_last_etime(rs.getLong("module_last_etime"));
				taskInfo.setModule_period(rs.getInt("module_period"));
			}
		} catch (SQLException e) {
			log.error("任务信息读取失败！");
			log.error(e.toString());
			//throw e;
		} finally {
			service.releaseResultSet(rs);
			service.releaseStatement(stmt);
			service.releaseConnection();
		}
		return taskInfo;
	}

	public void updatePushTable(String id, String pushTable) throws SQLException {
		String updateSql = "update pdc.task_model  set user_table = \""+pushTable+
				"\",module_last_etime = unix_timestamp(now()) where id = "+ id;
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
}
