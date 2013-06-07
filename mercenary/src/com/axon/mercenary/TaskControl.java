package com.axon.mercenary;

import static org.quartz.DateBuilder.nextGivenSecondDate;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

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
		//任务工厂初始化
		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler sched = null;
		try {
			sched = sf.getScheduler();
	        sched.start();
		} catch (SchedulerException e) {
			log.error("任务调度服务初始化失败");
			log.error(e.toString());
			return ;
		}

		log.info("任务控制器更新开始。");
		
		MySqlAction action = new MySqlAction();
		// 取得最新的任务信息
		action.setTaskInfo();
		//已经在任务控制器中的任务
		HashMap oldTaskInfoMap = Constants.oldTaskInfoMap;
		//配置表中最新的任务
		HashMap currentTaskInfoMap = Constants.currentTaskInfoMap;

		// 遍历最新的任务信息
		Iterator<?> iter = oldTaskInfoMap.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			ScheduleTaskInfoBean oldTask = (ScheduleTaskInfoBean) oldTaskInfoMap
					.get(key);
			
			//需要执行的认识是否已在任务控制器中
			if (currentTaskInfoMap.containsKey(key)) {
				ScheduleTaskInfoBean currentTask = (ScheduleTaskInfoBean) currentTaskInfoMap
					.get(key);
				
				//已在控制器中的任务是否有更新
				if (!currentTask.equals(oldTask)) {
					try {
						//将有更新的任务从任务控制器中卸载
						sched.unscheduleJob(new TriggerKey(oldTask.getJobKey(),null));
						oldTaskInfoMap.remove(oldTask.getJobKey());
					} catch (SchedulerException e) {
						//卸载任务失败
						log.error("更新任务失败："+ currentTask.getJobKey());
					}
				}
			} else {
				try {
					//将已过期，停止，删除的任务从任务控制器中卸载
					sched.unscheduleJob(new TriggerKey(oldTask.getJobKey(),null));
					oldTaskInfoMap.remove(oldTask.getJobKey());
				} catch (SchedulerException e) {
					//卸载任务失败
					log.error("卸载任务失败："+ oldTask.getJobKey());
				}
			}
		}

		iter = currentTaskInfoMap.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			ScheduleTaskInfoBean currentTask = (ScheduleTaskInfoBean) currentTaskInfoMap
					.get(key);

			try {


				//第一次执行时间
				//Date startTime = currentTask.getExecTime();
		        Date startTime = nextGivenSecondDate(null, 5);

		        
				JobDetail job = newJob(DumbInterruptableJob.class)
						.withIdentity(String.valueOf(currentTask.getName())).build();
				job.getJobDataMap().put("url", currentTask.getProgramUrl());
				
		        SimpleTrigger trigger = newTrigger() 
		                .withIdentity(currentTask.getJobKey())
		                .startAt(startTime)
		                .withSchedule(simpleSchedule()
		                        .withIntervalInSeconds(currentTask.getRepeatTime())
		                        .repeatSecondlyForTotalCount(1,currentTask.getRepeatTime()))
		                .build();
		        
		        Date ft = sched.scheduleJob(job, trigger);
		        

		        try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					
				}
		        sched.shutdown(true);
			} catch (SchedulerException e) {
				
			}

		}

	}
	

    public static void main(String[] args) throws Exception {

    	TaskControl example = new TaskControl();
        example.run();
    }


}
