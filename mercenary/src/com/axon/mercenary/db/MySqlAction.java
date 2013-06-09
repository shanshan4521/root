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

	//启动中的任务
	String selectSql = "SELECT taskId, name, execTime, programUrl, " +
			"pluralFlag, parameter, repeatTime, repeatUnit, updateTime " +
			"FROM pdc_aide.schedule_task_info where status=1";
	//过期任务暂停
	String updateSql = "update pdc_aide.schedule_task_info set status  = 2 where deadTime > timestamp('0000-00-00 00:00:00') and deadtime < timestamp(now())";

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
				taskInfo.setExecTime(rs.getTimestamp("execTime"));
				taskInfo.setProgramUrl(rs.getString("programUrl"));
				taskInfo.setPluralFlag(rs.getInt("pluralFlag"));
				taskInfo.setParameter(rs.getString("parameter").trim());
				taskInfo.setRepeatTime(rs.getInt("repeatTime"));
				taskInfo.setRepeatUnit(rs.getString("repeatUnit"));
				Constants.currentTaskInfoMap.put(taskInfo.getJobKey(), taskInfo);
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
}
