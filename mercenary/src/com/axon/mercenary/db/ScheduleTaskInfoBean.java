package com.axon.mercenary.db;

import java.util.Date;

public class ScheduleTaskInfoBean {

	// 任务编号
	private String taskId = "";

	// 名称
	private String name = "";

	// 执行时间
	private Date execTime = new Date();

	// 执行程序路径
	private String programUrl = "";

	// 任务状态
	// 0:未启动
	// 1:已启动
	// 2:停止
	private int status = 0;

	// 是否可以启动多个实例
	// 0:不可以
	// 1:可以
	private int pluralFlag = 0;

	// 启动可任务的参数
	private String parameter = "";

	// 过期时间
	private Date deadTime = new Date();

	// 第一次执行时间
	private Date firstExecTime;

	// 最新执行时间
	private Date lastExecTime;

	// 间隔时间
	private int repeatTime = 0;

	// 间隔时间单位,MIN,HOUR,DAY,MONTH
	private String repeatUnit = "";

	// 执行结果
	private String result = "";

	// 任务描述
	private String description = "";

	// 规则添加时间
	private Date updateTime = new Date();

	// 规则添加人
	private String updateUser = "";

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getExecTime() {
		return execTime;
	}

	public void setExecTime(Date execTime) {
		this.execTime = execTime;
	}

	public String getProgramUrl() {
		return programUrl;
	}

	public void setProgramUrl(String programUrl) {
		this.programUrl = programUrl;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getPluralFlag() {
		return pluralFlag;
	}

	public void setPluralFlag(int pluralFlag) {
		this.pluralFlag = pluralFlag;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		if(parameter != null)
			this.parameter = parameter.trim();
	}

	public Date getDeadTime() {
		return deadTime;
	}

	public void setDeadTime(Date deadTime) {
		this.deadTime = deadTime;
	}

	public Date getFirstExecTime() {
		return firstExecTime;
	}

	public void setFirstExecTime(Date firstExecTime) {
		this.firstExecTime = firstExecTime;
	}

	public Date getLastExecTime() {
		return lastExecTime;
	}

	public void setLastExecTime(Date lastExecTime) {
		this.lastExecTime = lastExecTime;
	}

	public int getRepeatTime() {
		return repeatTime;
	}

	public void setRepeatTime(int repeatTime) {
		this.repeatTime = repeatTime;
	}

	public String getRepeatUnit() {
		return repeatUnit;
	}

	public void setRepeatUnit(String repeatUnit) {
		this.repeatUnit = repeatUnit;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public boolean equals(ScheduleTaskInfoBean task) {
		boolean ret = false;
		if (this.taskId.equals(task.getTaskId())
				&& this.execTime.equals(task.getExecTime())
				&& this.programUrl.equals(task.getProgramUrl())
				&& this.status == task.getStatus()
				&& this.pluralFlag == task.getPluralFlag()
				&& this.parameter.equals(task.getParameter())
				&& this.deadTime.equals(task.getDeadTime())
				&& this.repeatTime == task.getRepeatTime()
				&& this.repeatUnit.equals(task.getRepeatUnit())) {
			ret = true;
		}
		return ret;
	}
	
	public String getJobKey() {
		return "[" + this.taskId + "]:" + this.name;
	}

	public int getRepeatMin() {
		int time = 0;

		if ("min".equals(this.repeatUnit.toLowerCase())) {
			time = this.repeatTime;
		} else if ("hour".equals(this.repeatUnit.toLowerCase())) {
			time = this.repeatTime * 60;
		} else if ("day".equals(this.repeatUnit.toLowerCase())) {
			time = this.repeatTime * 60 * 24;
		} else if ("month".equals(this.repeatUnit.toLowerCase())) {
			time = this.repeatTime * 60 * 24 * 30;
		}else{
			//设定错误时未day
			time = this.repeatTime * 60 * 24;
		}
		return time;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getJobKey()).append(",执行时间:").append(this.execTime)
				.append(",间隔时间").append(this.repeatTime)
				.append(this.repeatUnit).append(",任务内容:")
				.append(this.programUrl).append(" ").append(this.parameter);

		return sb.toString();
	}
}
