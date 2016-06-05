

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.PrivilegedExceptionAction;
import java.security.SecureRandom;
import java.util.regex.Pattern;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FsShellPermissions;
import org.apache.hadoop.fs.permission.ChmodParser;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.fs.shell.PathData;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.security.UserGroupInformation;
import cs219.proj.PrivateFileOp;

public class Main {
	public static void main(String[] args) throws Exception {  

        try {
            UserGroupInformation ugi// = UserGroupInformation.getCurrentUser();
                = UserGroupInformation.createRemoteUser("Peng");
            for(String s:ugi.getGroupNames())
            	System.out.println(s);
            System.out.println(ugi.getUserName());
            ugi.doAs(new PrivilegedExceptionAction<Void>() {

                public Void run() throws Exception {

                    Configuration conf = new Configuration();
                    conf.set("fs.defaultFS", "hdfs://localhost:9000/");
                    conf.set("hadoop.job.ugi", "Peng");
                    
                    FileSystem fs = FileSystem.get(conf);     
                    PathData file1 = new PathData("/user/test1.txt", conf);
                    
                    /*
                     * 1. Test for chmod		rwxr-xr-x 493		rwx------ 448
                     */
//                    short newperms = 448;
//                    short currentperms = 493;
//                    System.out.println(checkPrivate(newperms) + " " + checkPrivate(currentperms));
//                    file1.fs.setPermission(file1.path, new FsPermission(newperms));
//                    if (checkPrivate(newperms) && !checkPrivate(currentperms)) {
//                    	PrivateFileOp.init(file1);
//                    }
//                    if (checkPrivate(currentperms) && !checkPrivate(newperms)) {
//                    	PrivateFileOp.restore(file1, true);
//                    }
                    
                    /*
                     * 2. Test for read from encrypted file: text, cat
                     */
//                  	short perm = file1.stat.getPermission().toShort();
//                  	if(perm%64 == 0 && perm >= 64 && perm <= 448) {
//                  		InputStream in = file1.fs.open(file1.path);
//                    	byte[] decrypted = PrivateFileOp.dec(in, file1);
//                    	System.out.write(decrypted);
//                  	}
//                  	else {
//                  		System.out.println("File is not encrypted");
//                  	}
                  	
                  	
                    /*
                     * 3. Test for write to encrypted file: appendToFile
                     */
//                	short perm = file1.stat.getPermission().toShort();
//                	if(perm%64 == 0 && perm >= 64 && perm <= 448) {
//                		//Restore the file first
//                		System.out.println("Restoring the file first");
//                		PrivateFileOp.restore(file1, false);
//                	}
//                  
//                  InputStream is = null;
//                  FSDataOutputStream fos = file1.fs.append(file1.path);
//                  
//                  
//                  File source = new File("/Users/Peng/Desktop/source.txt");
//                  is = new FileInputStream(source);
//                  IOUtils.copyBytes(is, fos, 1024);
//                  IOUtils.closeStream(is);
//                  is = null;
//                  
//                  IOUtils.closeStream(fos);
//         
//                	if(perm%64 == 0 && perm >= 64 && perm <= 448) {
//                		//Encrypt the file and end
//                		System.out.println("Encrypt the file back");
//                		PrivateFileOp.init(file1);
//                	}
                    
                    
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }     
    }
	
	  private static boolean checkPrivate(short perm) {
	  	return perm%64 == 0 && perm >= 64 && perm <= 448;
	  }
}
