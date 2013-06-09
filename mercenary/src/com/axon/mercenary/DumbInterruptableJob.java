package com.axon.mercenary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.UnableToInterruptJobException;

import com.axon.mercenary.common.Constants;
import com.axon.mercenary.db.ScheduleTaskInfoBean;

public class DumbInterruptableJob implements InterruptableJob {

	// logging services
	private Logger log = LoggerFactory.getLogger(DumbInterruptableJob.class);

	// has the job been interrupted?
	private boolean interrupted = false;

	// job name
	private JobKey jobKey = null;

	/**
	 * <p>
	 * Empty constructor for job initialization
	 * </p>
	 */
	public DumbInterruptableJob() {
	}

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		jobKey = context.getJobDetail().getKey();

		boolean isComplete = false;
		
		try {
			Process p = null;
			BufferedReader br = null;

			ScheduleTaskInfoBean task = (ScheduleTaskInfoBean) context
					.getJobDetail().getJobDataMap().get(Constants.TASKBEAN);

			try {
				p = Runtime.getRuntime().exec(
						task.getProgramUrl() + " " + task.getParameter());

//				// 启动守护线程监控存储过程执行时间，超时时强制退出。
//				Thread t = new Thread() {
//					public void run() {
//						try {
//							Thread.sleep(24*60*60 * 1000);
//							
//						} catch (InterruptedException e) {
//							return ;
//						}
//						p.destroy();
//						
//					}
//				};
//				t.setDaemon(true);
//				t.start();
				
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

				if (p.waitFor() == 0 && sb.toString().trim().length() == 0) {
					isComplete = true;
				} 
				
			} catch (IOException e1) {
				log.error("任务执行失败：" + task.toString());
				log.error(e1.toString());
			} catch (InterruptedException e) {
				log.error("任务执行失败：" + task.toString());
				log.error(e.toString());
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						;
					}
				}
				if (p != null) {
					p.destroy();
				}
			}

			// periodically check if we've been interrupted...
			if (interrupted) {
				log.info("--- " + jobKey + " 被终止。");
				JobExecutionException e = new JobExecutionException("测试程序结束。");
				throw e;
			}
		} finally {
			log.info("---- " + jobKey + " 结束在 " + new Date());
		}
	}

	public void interrupt() throws UnableToInterruptJobException {
		interrupted = true;
	}

}
