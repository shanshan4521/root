package com.axon.mercenary;

import static org.quartz.JobBuilder.newJob;

import java.util.Date;
import java.util.Iterator;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import com.axon.mercenary.common.Constants;
import com.axon.mercenary.db.MySqlAction;
import com.axon.mercenary.db.ScheduleTaskInfoBean;

public class TaskControl implements Runnable {

	@Override
	public void run() {
		MySqlAction action = new MySqlAction();

		// 取得最新的任务信息
		action.setTaskInfo();

		// 遍历最新的任务信息
		Iterator<?> iter = Constants.taskInfoMap.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			ScheduleTaskInfoBean currentTask = (ScheduleTaskInfoBean) Constants.currentTaskInfoMap
					.get(key);
			if (Constants.currentTaskInfoMap.contains(key)) {
				ScheduleTaskInfoBean task = (ScheduleTaskInfoBean) Constants.taskInfoMap
						.get(key);

				if (task.equals(currentTask)) {
					// To Do
				}
			} else {
				Constants.currentTaskInfoMap.put(key, currentTask);
			}
		}

		iter = Constants.currentTaskInfoMap.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			ScheduleTaskInfoBean currentTask = (ScheduleTaskInfoBean) Constants.currentTaskInfoMap
					.get(key);

			// First we must get a reference to a scheduler
			SchedulerFactory sf = new StdSchedulerFactory();

			try {
				Scheduler sched = sf.getScheduler();
				Date startTime = currentTask.getExecTime();
				JobDetail job = newJob(DumbInterruptableJob.class)
						.withIdentity("interruptableJob1", "group1").build();

			} catch (SchedulerException e) {

			}

		}

	}

}
