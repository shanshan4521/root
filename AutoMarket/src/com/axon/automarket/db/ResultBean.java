 package com.axon.automarket.db;
 
public class ResultBean {
	private String type;
	private String pushTable;
	private int userCount;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPushTable() {
		return pushTable;
	}
	public void setPushTable(String pushTable) {
		this.pushTable = pushTable;
	}
	public int getUserCount() {
		return userCount;
	}
	public void setUserCount(int userCount) {
		this.userCount = userCount;
	}
}
