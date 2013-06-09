package com.axon.mercenary;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.PropertyConfigurator;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.axon.mercenary.common.Constants;
import com.axon.mercenary.db.MySqlAction;
import com.axon.mercenary.db.ScheduleTaskInfoBean;

//public class TaskControl implements Runnable {
public class TaskControl {

	private Logger log = LoggerFactory.getLogger(DumbInterruptableJob.class);

	public void run() {
		// 任务工厂初始化
		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler sched = null;
		try {
			sched = sf.getScheduler();
			sched.start();
		} catch (SchedulerException e) {
			log.error("任务调度服务初始化失败");
			log.error(e.toString());
			return;
		}
		log.info("任务控制器更新开始。");
		while (true) {
			MySqlAction action = new MySqlAction();
			// 取得最新的任务信息
			try {
				action.setTaskInfo();
			} catch (SQLException e) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					
				}
				continue;
			}

			// 卸载已变更，过去，删除的任务
			unInstallTask(sched);

			// 加载新任务
			installTask(sched);
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				
			}
		}
	}

	private void unInstallTask(Scheduler sched) {

		// 已经在任务控制器中的任务
		HashMap<String, ScheduleTaskInfoBean> oldTaskInfoMap = Constants.oldTaskInfoMap;
		// 配置表中最新的任务
		HashMap<String, ScheduleTaskInfoBean> currentTaskInfoMap = Constants.currentTaskInfoMap;

		ArrayList<String> removeList = new ArrayList<String>();
		// 遍历最新的任务信息
		Iterator<?> iter = oldTaskInfoMap.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			ScheduleTaskInfoBean oldTask = (ScheduleTaskInfoBean) oldTaskInfoMap
					.get(key);

			// 需要执行的认识是否已在任务控制器中
			if (currentTaskInfoMap.containsKey(key)) {
				ScheduleTaskInfoBean currentTask = (ScheduleTaskInfoBean) currentTaskInfoMap
						.get(key);

				// 已在控制器中的任务是否有更新
				if (!currentTask.equals(oldTask)) {
					try {
						// 将有更新的任务从任务控制器中卸载
						sched.unscheduleJob(new TriggerKey(oldTask.getJobKey(),
								null));
						removeList.add(oldTask.getJobKey());
					} catch (SchedulerException e) {
						// 卸载任务失败
						log.error("更新任务失败：" + currentTask.getJobKey());
					}
				}
			} else {
				try {
					// 将已过期，停止，删除的任务从任务控制器中卸载
					sched.unscheduleJob(new TriggerKey(oldTask.getJobKey(),
							null));
					removeList.add(oldTask.getJobKey());
				} catch (SchedulerException e) {
					// 卸载任务失败
					log.error("卸载任务失败：" + oldTask.getJobKey());
				}
			}
		}
		for(int i = 0 ; i < removeList.size(); i++){
			oldTaskInfoMap.remove(removeList.get(i));
			log.info(removeList.get(i));
		}
	}

	private void installTask(Scheduler sched) {

		// 已经在任务控制器中的任务
		HashMap<String, ScheduleTaskInfoBean> oldTaskInfoMap = Constants.oldTaskInfoMap;
		// 配置表中最新的任务
		HashMap<String, ScheduleTaskInfoBean> currentTaskInfoMap = Constants.currentTaskInfoMap;

		// 启动最新的任务
		Iterator<?> iter = currentTaskInfoMap.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			ScheduleTaskInfoBean currentTask = (ScheduleTaskInfoBean) currentTaskInfoMap
					.get(key);

			try {
				// 需要执行的认识是否已在任务控制器中
				if (!oldTaskInfoMap.containsKey(key)) {
					// 第一次执行时间
					Date startTime = currentTask.getExecTime();
					//Date startTime = nextGivenSecondDate(null, 5);

					JobDetail job = newJob(DumbInterruptableJob.class)
							.withIdentity(currentTask.getJobKey()).build();

					// 任务详细
					job.getJobDataMap().put(Constants.TASKBEAN, currentTask);

					SimpleTrigger trigger = null;

					if (currentTask.getRepeatMin() != 0) {
						// 建立可重复的任务
						trigger = newTrigger()
								.withIdentity(currentTask.getJobKey())
								.startAt(startTime)
								.withSchedule(
										simpleSchedule().withIntervalInMinutes(
												currentTask.getRepeatMin())
												.repeatForever()).build();
					} else {
						// 建立一次性的任务
						trigger = (SimpleTrigger) newTrigger()
								.withIdentity(currentTask.getJobKey())
								.startAt(startTime).build();
					}
					sched.scheduleJob(job, trigger);
					oldTaskInfoMap.put(key, currentTask);
				}
			} catch (SchedulerException e) {
			}
		}
	}

	public static void main(String[] args) throws Exception {
		PropertyConfigurator.configure("log4j.properties");
		TaskControl example = new TaskControl();
		example.run();
	}


}
