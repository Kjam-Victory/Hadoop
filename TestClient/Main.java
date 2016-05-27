

import java.security.PrivilegedExceptionAction;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.security.UserGroupInformation;

public class Main {
	public static void main(String[] args) throws Exception {
//        String uri = args[0];
//        Configuration conf = new Configuration();
//        FileSystem fs = FileSystem.get(URI.create(uri), conf);
//        //Log.info("sad");
//        fs.addGroup("G");
//        FSDataInputStream in = null;
//        try {
//            in = fs.open(new Path(uri));
//            IOUtils.copyBytes(in, System.out, 4096, false);
//        } finally {
//            IOUtils.closeStream(in);
//        }
		
//        try{
//        	
//        }catch(Exception e){
//        	e.printStackTrace();
//        }
//        
		
        //System.out.println(Main.class.toString());
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
    
//        
//        LinkedList<String> l = new LinkedList();
//        //FileOutputStream fm = new FileOutputStream(new File("asd"));
       
        //System.out.println(ClientNamenodeProtocolPB.class);
        
        
//        try{
//        	FileWriter fw = new FileWriter(new File("/Users/Kai_Jiang/Desktop/RpcServerLog.txt"), true);
//        	fw.write("");
//        	fw.close();
//        }
//        catch(Exception e){
//        	e.printStackTrace();
//        }
//        
        
        
    }
}
