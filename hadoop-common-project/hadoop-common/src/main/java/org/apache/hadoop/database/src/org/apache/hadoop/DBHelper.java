package org.apache.hadoop.database;

import java.util.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.hadoop.DbManager;
import org.apache.hadoop.User;

public class DBHelper {
	private Connection con;
	public DBHelper() {
		con = null;
	}
	public boolean createGroup(String group) {
		try {
    	    con = DbManager.getConnection(false);
    	    Statement stmt = con.createStatement();
    	    String sqlString = "SELECT * FROM Groups WHERE Name = \'" + group + "\'";
    	    ResultSet rs = stmt.executeQuery(sqlString);
    	    if (rs.next()) return false;
    	    sqlString = "INSERT INTO Groups (Name) VALUES(\'"+ group + "\')";
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
	public boolean createUser(User user) {
		try {
    	    con = DbManager.getConnection(false);
    	    Statement stmt = con.createStatement();
    	    String sqlString = "SELECT * FROM Users WHERE Name = \'" + user.getName() + "\' AND IP = " + user.getIP();
    	    ResultSet rs = stmt.executeQuery(sqlString);
    	    if (rs.next()) return false;
    	    sqlString = "INSERT INTO Users (Name, IP) VALUES(\'"+ user.getName() + "\', " + user.getIP() + ")";
    	    stmt.executeUpdate(sqlString);
    	    con.close();
    	    return true;
    	} catch (SQLException ex) {
    	    System.out.println(ex);
    	    return false;
    	}
	}
	public boolean deleteUser(User user) {
		try {
    	    con = DbManager.getConnection(false);
    	    Statement stmt = con.createStatement();
    	    String sqlString = "DELETE FROM Users WHERE Name = \'" + user.getName() + "\' AND IP = " + user.getIP();
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
    	    String sqlString = "INSERT INTO UserGroup (Username, UserIP, GroupName) VALUES(\'"+ user.getName() + "\', " + user.getIP() + ", \'" + group + "\')";
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
    			User user = new User(rs.getString("Name"), rs.getInt("IP"));
    			res.add(user);
    		}
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
    	    con.close();
    	} catch (SQLException ex) {
    	    System.out.println(ex);
    	}
		return res;
	}
	public static void main(String[] args) {
		DBHelper db = new DBHelper();
		if (db.createGroup("test")) {
			List<String> groups = db.getAllGroups();
			System.out.println("Add a new group. Groups in DB:");
			for (String g : groups)
				System.out.println(g);
		} else 
			System.out.println("Adding group failed.");
		
		User u1 = new User("u1", 10);
		if (db.createUser(u1)) {
			List<User> users = db.getAllUsers();
			System.out.println("Add a new user. Users in DB:");
			for (User u : users)
				System.out.println(u.getName() + ", " + u.getIP());
		} else
			System.out.println("Adding user failed.");
		
		if (db.addUsertoGroup(u1, "test")) {
			System.out.println("Add a user to group test. User's groups:");
			List<String> groups = db.getGroups(u1);
			for (String g : groups)
				System.out.println(g);
		} else
			System.out.println("Adding user to group failed.");
		
		if (db.removeUserFromGroup(u1, "test")) {
			System.out.println("Remove a user from group test. User's groups:");
			List<String> groups = db.getGroups(u1);
			for (String g : groups)
				System.out.println(g);
		} else
			System.out.println("Removing user from group failed.");
		
		if (db.deleteUser(u1)) {
			List<User> users = db.getAllUsers();
			System.out.println("Delete a user. Users in DB:");
			for (User u : users)
				System.out.println(u.getName() + ", " + u.getIP());
		} else
			System.out.println("Deleting user failed.");
		
		if (db.deleteGroup("test")) {
			List<String> groups = db.getAllGroups();
			System.out.println("Delete a group. Groups in DB:");
			for (String g : groups)
				System.out.println(g);
		} else
			System.out.println("Deleteing group failed.");
		
		
	}
}