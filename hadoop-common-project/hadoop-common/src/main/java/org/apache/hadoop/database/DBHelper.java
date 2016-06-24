package org.apache.hadoop.database;

import java.io.*;
import java.util.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.conf.Configuration;
import java.security.PrivilegedExceptionAction;

public class DBHelper {
    private Connection con;
    public DBHelper() {
        con = null;
    }
    public boolean createGroup(String group) {
        try {
            con = DbManager.getConnection(false);
            Statement stmt = con.createStatement();
            String sqlString = "SELECT * FROM Groups WHERE Name = \'" + group + "\';";
            ResultSet rs = stmt.executeQuery(sqlString);
            if (rs.next()) {
                con.close();
                return false;
            }
            rs.close();
            sqlString = "INSERT INTO Groups (Name) VALUES(\'"+ group + "\');";
            stmt.executeUpdate(sqlString);
            con.close();
	    
            return true;
        } catch (SQLException ex) {
            
	    System.out.println(ex);
            return false;
        }
    }
    public boolean deleteGroup(String group) {
        try {
            con = DbManager.getConnection(false);
            Statement stmt = con.createStatement();
            String sqlString = "DELETE FROM Groups WHERE Name = \'" + group + "\'";
            stmt.executeUpdate(sqlString);
            con.close();
            return true;
        } catch (SQLException ex) {
            System.out.println(ex);
            return false;
        }
    }
    public boolean createUser(User user, Timestamp time) {
        try {
            con = DbManager.getConnection(false);
            Statement stmt = con.createStatement();
            String sqlString = "SELECT * FROM Users WHERE Name = \'" + user.getName() + "\' AND IP = " + user.getIP();
            ResultSet rs = stmt.executeQuery(sqlString);
            if (rs.next()) {
                con.close();
                return false;
            }
            rs.close();
            sqlString = "INSERT INTO Users (Name, IP, CreateTime) VALUES(\'"+ user.getName() + "\', " + user.getIP() + ", \'" + time + "\')";
            stmt.executeUpdate(sqlString);
            con.close();
            return true;
        } catch (SQLException ex) {
            System.out.println(ex);
            return false;
        }
    }    
    public boolean deleteUserByIp(final String s){
    	long ip_num = (Long.parseLong(s.split("\\.")[0])<<24) + (Long.parseLong(s.split("\\.")[1])<<16)
				+ (Long.parseLong(s.split("\\.")[2])<<8) +Long.parseLong(s.split("\\.")[3]); 
    	try {
            con = DbManager.getConnection(false);
            Statement stmt = con.createStatement();
            String sqlString = "SELECT * FROM Users WHERE IP = " + ip_num;
        	ResultSet rs = stmt.executeQuery(sqlString);
        	while (rs.next()) {
        		final String userName = rs.getString("Name");
        		Long ipnum = rs.getLong("IP");
        		UserGroupInformation ugi = UserGroupInformation.createRemoteUser(userName);                                
        		ugi.doAs(new PrivilegedExceptionAction<Void>() {        	
        			public Void run() throws Exception {
        				Configuration conf = new Configuration();
        				conf.set("fs.defaultFS", "hdfs://master:9000/");                         
        				FileSystem fs = FileSystem.get(conf);
        				fs.delete(new Path("/user/"+userName+"_"+s), true);
        				return null;
        			}
        		});        		
        		if(!deleteUser(new User(userName, ipnum))) return false;
        	}
        	rs.close();
        	con.close();
        	return true;
    	}catch (Exception ex) {
    		ex.printStackTrace();
    		return false;
    	}        
        
    }
    public boolean deleteUser(User user) {
        try {
            con = DbManager.getConnection(false);
            Statement stmt = con.createStatement();
            String sqlString = "SELECT * FROM UserGroup WHERE Username = \'" + user.getName() + "\' AND UserIP = " + user.getIP();
            ResultSet rs = stmt.executeQuery(sqlString);
            while (rs.next()) {
            	String groupName = rs.getString("Groupname");
            	Integer isOwner = rs.getInt("IsOwner");
                if(isOwner == 1){
                	if(!deleteGroup(groupName)) return false;
                }
            }    
            rs.close();
            sqlString = "DELETE FROM Users WHERE Name = \'" + user.getName() + "\' AND IP = " + user.getIP();
            stmt.executeUpdate(sqlString);
            con.close();
            return true;
        } catch (SQLException ex) {
            System.out.println(ex);
            return false;
        }
    }

    public boolean addUsertoGroup(User user, String group) {
        try {
            con = DbManager.getConnection(false);
            Statement stmt = con.createStatement();
            String sqlString = "INSERT INTO UserGroup (Username, UserIP, GroupName, IsOwner) VALUES(\'"+ user.getName() + "\', " + user.getIP() + ", \'" + group + "\', 0)";
            stmt.executeUpdate(sqlString);
            con.close();
            return true;
        } catch (SQLException ex) {
            System.out.println(ex);
            return false;
        }
    }
    public boolean isGroupOwner(User user, String group) {
        boolean res = false;
        try {
            con = DbManager.getConnection(true);
            Statement stmt = con.createStatement();
            String sqlString = "SELECT * FROM UserGroup where Groupname = \'"+ group+"\' AND Username = \'" + user.getName() + "\' AND UserIP = " + user.getIP() + " AND IsOwner = 1";
            ResultSet rs = stmt.executeQuery(sqlString);
            while (rs.next()) {
                res = true;
                break;
            }
            rs.close();
            con.close();
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return res;
    }
    public boolean addGroupOwnertoGroup(User user, String group) {
        try {
            con = DbManager.getConnection(false);
            Statement stmt = con.createStatement();
            String sqlString = "INSERT INTO UserGroup (Username, UserIP, GroupName, IsOwner) VALUES(\'"+ user.getName() + "\', " + user.getIP() + ", \'" + group + "\', 1)";
            stmt.executeUpdate(sqlString);
            con.close();
            return true;
        } catch (SQLException ex) {
            System.out.println(ex);
            return false;
        }
    }
    public boolean removeUserFromGroup(User user, String group) {
        try {
            con = DbManager.getConnection(false);
            Statement stmt = con.createStatement();
            String sqlString = "DELETE FROM UserGroup WHERE Groupname = \'" + group + "\' AND Username = \'" + user.getName() + "\' AND UserIP = " + user.getIP();
            stmt.executeUpdate(sqlString);
            con.close();
            return true;
        } catch (SQLException ex) {
            System.out.println(ex);
            return false;
        }
    }
    public List<String> getGroups(User user) {
        List<String> res = new ArrayList<>();
        try {
            con = DbManager.getConnection(true);
            Statement stmt = con.createStatement();
            String sqlString = "SELECT Groupname FROM UserGroup WHERE Username = \'" + user.getName() + "\' AND UserIP = " + user.getIP();
            ResultSet rs = stmt.executeQuery(sqlString);
            while (rs.next()) {
                res.add(rs.getString("Groupname"));
            }
            rs.close();
            con.close();
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return res;
    }
    public List<User> getAllUsers() {
        List<User> res = new ArrayList<>();
        try {
            con = DbManager.getConnection(true);
            Statement stmt = con.createStatement();
            String sqlString = "SELECT * FROM Users";
            ResultSet rs = stmt.executeQuery(sqlString);
            while (rs.next()) {
                User user = new User(rs.getString("Name"), rs.getLong("IP"));
                res.add(user);
            }
            rs.close();
            con.close();
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return res;
    }
    public List<String> getAllGroups() {
        List<String> res = new ArrayList<>();
        try {
            con = DbManager.getConnection(true);
            Statement stmt = con.createStatement();
            String sqlString = "SELECT * FROM Groups";
            ResultSet rs = stmt.executeQuery(sqlString);
            while (rs.next()) {
                res.add(rs.getString("Name"));
            }
            rs.close();
            con.close();
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return res;
    }
    public Timestamp getLastUserTime() {
        Timestamp res = null;
        try {
            con = DbManager.getConnection(true);
            Statement stmt = con.createStatement();
            String sqlString = "SELECT MAX(CreateTime) FROM Users";
            ResultSet rs = stmt.executeQuery(sqlString);
            if (rs.next()) {
                res = rs.getTimestamp(1);
            }
            rs.close();
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return res;
    }
    public void test() {
        if (createGroup("test")) {
            List<String> groups = getAllGroups();
            System.out.println("Add a new group. Groups in DB:");
            for (String g : groups)
                System.out.println(g);
        } else 
            System.out.println("Adding group failed.");
        
        User u1 = new User("u1", 10);
        Timestamp time = new Timestamp(116,6,2,0,0,0,0);
        if (createUser(u1, time)) {
            List<User> users = getAllUsers();
            System.out.println("Add a new user. Users in DB:");
            for (User u : users)
                System.out.println(u.getName() + ", " + u.getIP());
        } else
            System.out.println("Adding user failed.");
        
        if (addUsertoGroup(u1, "test")) {
            System.out.println("Add a user to group test. User's groups:");
            List<String> groups = getGroups(u1);
            for (String g : groups)
                System.out.println(g);
        } else
            System.out.println("Adding user to group failed.");
        
        if (removeUserFromGroup(u1, "test")) {
            System.out.println("Remove a user from group test. User's groups:");
            List<String> groups = getGroups(u1);
            for (String g : groups)
                System.out.println(g);
        } else
            System.out.println("Removing user from group failed.");
        
        if (deleteUser(u1)) {
            List<User> users = getAllUsers();
            System.out.println("Delete a user. Users in DB:");
            for (User u : users)
                System.out.println(u.getName() + ", " + u.getIP());
        } else
            System.out.println("Deleting user failed.");
        
        if (deleteGroup("test")) {
            List<String> groups = getAllGroups();
            System.out.println("Delete a group. Groups in DB:");
            for (String g : groups)
                System.out.println(g);
        } else
            System.out.println("Deleteing group failed.");
    }
    public static void main(String[] args) {
    	DBHelper db = new DBHelper();
        db.test();
    }
}
