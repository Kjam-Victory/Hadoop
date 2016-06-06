package org.apache.hadoop.database;
import java.util.*; 

public class User {
	private String name;
	private long ip;
	public User(String name, long ip) {
		this.name = name;
		this.ip = ip;
	}
	public User(String name, String s) {
		System.out.println(s);
		long ip_num = (Long.parseLong(s.split("\\.")[0])<<24) + (Long.parseLong(s.split("\\.")[1])<<16)
				+ (Long.parseLong(s.split("\\.")[2])<<8) +Long.parseLong(s.split("\\.")[3]); 
		this.name = name;
		this.ip = ip_num;
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
	public void setIP(String s){
		long ip_num = (Long.parseLong(s.split("\\.")[0])<<24) + (Long.parseLong(s.split("\\.")[1])<<16)
				+ (Long.parseLong(s.split("\\.")[2])<<8) +Long.parseLong(s.split("\\.")[3]); 
		this.ip = ip_num;
	}
}
