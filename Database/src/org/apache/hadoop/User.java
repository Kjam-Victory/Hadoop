package org.apache.hadoop;
import java.util.*; 

 class User {
	private String name;
	private long ip;
	public User(String name, long ip) {
		this.name = name;
		this.ip = ip;
	}
	public String getName() {
		return name;
	}
	public long getIP() {
		return ip;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setIP(long ip) {
		this.ip = ip;
	}
}