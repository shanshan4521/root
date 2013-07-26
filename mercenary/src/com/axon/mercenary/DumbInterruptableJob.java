package com.axon.mercenary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;

import com.axon.mercenary.common.Constants;
import com.axon.mercenary.common.ProcessConfig;
import com.axon.mercenary.common.Util;
import com.axon.mercenary.db.MySqlAction;
import com.axon.mercenary.db.ScheduleTaskInfoBean;

public class DumbInterruptableJob implements Job {

	// logging services
	private Logger log = LoggerFactory.getLogger(DumbInterruptableJob.class);

	// job name
	private JobKey jobKey = null;

	/**
	 * Empty constructor for job initialization
	 */
	public DumbInterruptableJob() {
	}

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		jobKey = context.getJobDetail().getKey();

		boolean isComplete = false;
		MySqlAction action = new MySqlAction();

		Process p = null;
		BufferedReader br = null;

		final ScheduleTaskInfoBean task = (ScheduleTaskInfoBean) context
				.getJobDetail().getJobDataMap().get(Constants.TASKBEAN);
		try {
			action.updateExecTime(task.getTaskId());
		} catch (SQLException e2) {
			log.error("定时任务启动时间更新失败！");
			log.error(task.toString());
			log.error(e2.toString());
			return;
		}

		if (task.getPluralFlag() == 0
				&& Constants.executingJobList.contains(task.getJobKey())) {
			log.error("任务" + jobKey + "还在执行中，启动失败！");
			log.error(task.toString());
			return;
		}
		Constants.executingJobList.add(task.getJobKey());

		// 启动守护线程监控存储过程执行时间，超时时强制退出。
		Thread t = new Thread() {
			public void run() {
				ProcessConfig config = ProcessConfig.getInstance();
				int timeOut= config.getProperty2Int(Constants.TASK_TIMEOUT);
				try {
					Thread.sleep(timeOut * 60 * 1000);
				} catch (InterruptedException e) {
					return;
				}
				Util.sendMail("任务执行超时:"+task.getJobKey(), task.toString());
			}
		};
		t.setDaemon(true);
		t.start();

		try {
			try {
				p = Runtime.getRuntime().exec(
						task.getProgramUrl() + " " + task.getParameter());

				br = new BufferedReader(new InputStreamReader(
						p.getInputStream()));
				String line = null;
				StringBuilder sb = new StringBuilder();
				while ((line = br.readLine()) != null) {
					;
				}

				br = new BufferedReader(new InputStreamReader(
						p.getErrorStream()));
				while ((line = br.readLine()) != null) {
					sb.append(line).append("\n");
				}

				// if (p.waitFor() == 0 && sb.toString().trim().length() == 0) {
				if (p.waitFor() == 0) {
					isComplete = true;
				} else {
					log.error("任务执行失败!");
					log.error(task.toString());
					log.error(sb.toString());
					Util.sendMail("任务执行失败:"+task.getJobKey(), task.toString()+sb.toString());
				}

			} catch (IOException e1) {
				log.error("任务执行失败!");
				log.error(task.toString());
				log.error(e1.toString());
				Util.sendMail("任务执行失败:"+task.getJobKey(), task.toString()+e1.toString());
			} catch (InterruptedException e) {
				log.error("任务执行失败!");
				log.error(task.toString());
				log.error(e.toString());
				Util.sendMail("任务执行失败:"+task.getJobKey(), task.toString()+e.toString());
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						// do nothing
					}
				}
				if (p != null) {
					p.destroy();
				}
				if(t != null){
					t.interrupt();
				}
			}
			try {
				action.updateTaskResult(task.getTaskId(), isComplete);
			} catch (SQLException e1) {
				log.error("定时任务结果更新失败！");
				log.error(task.toString());
				log.error(e1.toString());
				Util.sendMail("定时任务结果更新失败:"+task.getJobKey(), task.toString()+e1.toString());
			}

		} finally {
			Constants.executingJobList.remove(task.getJobKey());
			log.info("---- " + jobKey + " 结束在 " + new Date());
		}
	}
}
