package com.axon.mercenary.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.axon.mercenary.common.Constants;

public class MySqlAction {
	// 日志
	private Logger log = LoggerFactory.getLogger(MySqlService.class);

	// 启动中的任务
	String selectSql = "SELECT taskId, name, from_unixtime(execTime) as execTime,  from_unixtime(effecttime) as effecttime, " +
			"programUrl, pluralFlag, parameter, from_unixtime(deadTime) as deadTime ,repeatTime, repeatUnit "
			+ "FROM pdc_aide.schedule_task_info where status=1 and effecttime <= unix_timestamp(now()) ";

	// 过期任务暂停
	String updateSql = "update pdc_aide.schedule_task_info set status  = 2 "
			+ "where (deadTime > 0 and deadtime < unix_timestamp(now())) " +
			"or (repeatTime = 0 and execTime<unix_timestamp(now()))";

	public void setTaskInfo() throws SQLException {
		MySqlService service = new MySqlService();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = service.getConnection().createStatement();
			stmt.execute(updateSql);
			rs = stmt.executeQuery(selectSql);
			Constants.currentTaskInfoMap.clear();
			while (rs.next()) {
				ScheduleTaskInfoBean taskInfo = new ScheduleTaskInfoBean();
				taskInfo.setTaskId(rs.getString("taskId"));
				taskInfo.setName(rs.getString("name"));
				taskInfo.setExecTime(rs.getTimestamp("execTime"),rs.getTimestamp("effecttime"));
				taskInfo.setProgramUrl(rs.getString("programUrl"));
				taskInfo.setPluralFlag(rs.getInt("pluralFlag"));
				taskInfo.setParameter(rs.getString("parameter"));
				taskInfo.setDeadTime(rs.getTimestamp("deadTime"));
				taskInfo.setRepeatTime(rs.getInt("repeatTime"));
				taskInfo.setRepeatUnit(rs.getString("repeatUnit"));
				Constants.currentTaskInfoMap
						.put(taskInfo.getJobKey(), taskInfo);
			}
		} catch (SQLException e) {
			log.error("定时任务信息读取失败！");
			log.error(e.toString());
			throw e;
		} finally {
			service.releaseResultSet(rs);
			service.releaseStatement(stmt);
			service.releaseConnection();
		}
	}

	public void updateTaskResult(String taskId, boolean result)
			throws SQLException {
		// 任务结束更新结果和日期。
		String updateTask = "update pdc_aide.schedule_task_info set result = '"
				+ result
				+ "' where taskId = "
				+ taskId;

		MySqlService service = new MySqlService();
		Statement stmt = null;
		try {
			stmt = service.getConnection().createStatement();
			stmt.execute(updateTask);
		} catch (SQLException e) {
			throw e;
		} finally {
			service.releaseStatement(stmt);
			service.releaseConnection();
		}
	}

	public void updateExecTime(String taskId) throws SQLException {
		// 任务结束更新结果和日期。
		String updateTask = "update pdc_aide.schedule_task_info set "
				+ " lastExecTime = unix_timestamp(now())  where taskId = " + taskId;
		// 任务结束初次执行时间。
		String updateTaskFirstTime = "update pdc_aide.schedule_task_info set "
				+ " firstExecTime = unix_timestamp(now()) where firstExecTime is null "
				+ " or firstExecTime <= 0  "
				+ " and taskId = " + taskId;

		MySqlService service = new MySqlService();
		Statement stmt = null;
		try {
			stmt = service.getConnection().createStatement();
			stmt.execute(updateTask);
			stmt.execute(updateTaskFirstTime);
		} catch (SQLException e) {
			throw e;
		} finally {
			service.releaseStatement(stmt);
			service.releaseConnection();
		}

	}
}
