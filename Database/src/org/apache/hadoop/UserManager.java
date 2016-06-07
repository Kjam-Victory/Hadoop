package org.apache.hadoop.database;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.database.DBHelper;
import org.apache.hadoop.database.User;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;


public class UserManager {
	private DBHelper db;
	public UserManager() {
		db = new DBHelper();
	}
	public boolean detectNewUser(String filePath) {
        String line = null;
        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(filePath);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                String[] info = line.split(",");
            	//check timestamp
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date parsedDate = dateFormat.parse(info[2]);
                Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
                Timestamp lastTime = db.getLastUserTime();
                if (lastTime == null || timestamp.after(lastTime)) {
	                //ip convertor
	                long ip = ip2num(info[1]);
	                if (ip == -1) continue;
	                User user = new User(info[0], ip);
	                db.createUser(user, timestamp);
	                System.out.println(user.getName()+"/"+user.getIP());
                    addUserRootDir(info[0], info[1]);
                }
            }   
            bufferedReader.close(); 
            return true;
        } catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                filePath + "'"); 
            return false;
        } catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + filePath + "'"); 
            return false;
        } catch(ParseException ex) {
        	System.out.println("Invalid Timestamp.");
        	return false;
        }
	}
	private long ip2num(String ip) {
		String[] ipStr = ip.split("\\.");
        if (ipStr.length != 4) {
        	System.out.println("Invalid ip address.");
        	return -1;
        }
        long res = (long) (Integer.parseInt(ipStr[0]) * Math.pow(2, 24) + Integer.parseInt(ipStr[1]) * Math.pow(2, 16) + Integer.parseInt(ipStr[2]) * Math.pow(2, 8) + Integer.parseInt(ipStr[3]));
        System.out.println(res);
		return res;
	}

    public static void addUserRootDir(final String userName, final String IpAddr){
        final String realUserName = userName+"/"+IpAddr;
        final String UserDirName = userName+"_"+IpAddr;
        try {
            final UserGroupInformation ugi = UserGroupInformation.createRemoteUser("root");
            final UserGroupInformation caller = UserGroupInformation.createRemoteUser(userName);
            
            ugi.doAs(new PrivilegedExceptionAction<Void>() {            
                public Void run() throws Exception {
                    Configuration conf = new Configuration();
                    conf.set("fs.defaultFS", "hdfs://master:9000/");
                    
                    FileSystem fs = FileSystem.get(conf);   
                    fs.mkdirs(new Path("/user/"+UserDirName));
                    fs.setOwner(new Path("/user/"+UserDirName), realUserName, caller.getPrimaryGroupName());  
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

	
}

