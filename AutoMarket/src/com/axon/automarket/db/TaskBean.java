 package com.axon.automarket.db;
 
public class TaskBean {
	private String id = "";
	private String module_type = "";
	private String module_str = "";
	private String is_push = "";
	private String module_cid = "";
	private String push_method = "";
	private String is_autosend = "";
	private String is_second = "";
	private String user_table = "";
	private long task_etime = 0;
	private long module_last_etime = 0;
	private int module_period = 0;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getModule_type() {
		return module_type;
	}
	public void setModule_type(String module_type) {
		this.module_type = module_type;
	}
	public String getModule_str() {
		return module_str;
	}
	public void setModule_str(String module_str) {
		this.module_str = module_str;
	}
	public String getModule_cid() {
		return module_cid;
	}
	public void setModule_cid(String module_cid) {
		this.module_cid = module_cid;
	}
	public String getPush_method() {
		return push_method;
	}
	public void setPush_method(String push_method) {
		this.push_method = push_method;
	}
	public String getIs_autosend() {
		return is_autosend;
	}
	public void setIs_autosend(String is_autosend) {
		this.is_autosend = is_autosend;
	}
	public String getUser_table() {
		return user_table;
	}
	public void setUser_table(String user_table) {
		this.user_table = user_table;
	}
	public String getIs_second() {
		return is_second;
	}
	public void setIs_second(String is_second) {
		this.is_second = is_second;
	}
	public long getTask_etime() {
		return task_etime;
	}
	public void setTask_etime(long task_etime) {
		this.task_etime = task_etime;
	}
	public long getModule_last_etime() {
		return module_last_etime;
	}
	public void setModule_last_etime(long module_last_etime) {
		this.module_last_etime = module_last_etime;
	}
	public int getModule_period() {
		return module_period;
	}
	public void setModule_period(int module_period) {
		this.module_period = module_period;
	}
	public String getIs_push() {
		return is_push;
	}
	public void setIs_push(String is_push) {
		this.is_push = is_push;
	}
}
