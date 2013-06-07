 package com.axon.mercenary.common;

import com.axon.mercenary.db.ScheduleTaskInfoBean;
 
public class Util {
	public static boolean compareTaskInfoBean(ScheduleTaskInfoBean task,
			ScheduleTaskInfoBean currentTask) {
		boolean ret = false;
		if(task.getTaskId() == currentTask.getTaskId()){
			if(task.getUpdateTime().equals(currentTask.getUpdateTime())){
				if(task.getExecTime().equals(currentTask.getExecTime())){
					if(task.getProgramUrl().equals(currentTask.getProgramUrl())){
						if(task.getParameter().equals(currentTask.getParameter())){
							if(task.getPluralFlag() == currentTask.getPluralFlag()){
								ret = true;
							}
						}
					}
				}
			}
		}
		
		return ret;
		 
	}
}
