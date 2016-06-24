

import java.security.PrivilegedExceptionAction;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.security.UserGroupInformation;

public class Main {
	public static void main(String[] args) throws Exception {
		
        try {
            UserGroupInformation ugi// = UserGroupInformation.getCurrentUser();
                = UserGroupInformation.createRemoteUser("hadoop");
            for(String s:ugi.getGroupNames())
            	System.out.println(s);
            ugi.doAs(new PrivilegedExceptionAction<Void>() {

                public Void run() throws Exception {

                    Configuration conf = new Configuration();
                    conf.set("fs.defaultFS", "hdfs://master:9000/user/hadoop");
                    conf.set("hadoop.job.ugi", "hadoop");
                    
                    FileSystem fs = FileSystem.get(conf);                    
                    fs.addGroup("QYZ");                    
                    
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
    }
}
