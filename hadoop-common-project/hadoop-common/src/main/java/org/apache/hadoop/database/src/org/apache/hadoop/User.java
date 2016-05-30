package org.apache.hadoop.database;
import java.util.*; 

 class User {
	private String name;
	private int ip;
	public User(String name, int ip) {
		this.name = name;
		this.ip = ip;
	}
	public String getName() {
		return name;
	}
	public int getIP() {
		return ip;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setIP(int ip) {
		this.ip = ip;
	}
}