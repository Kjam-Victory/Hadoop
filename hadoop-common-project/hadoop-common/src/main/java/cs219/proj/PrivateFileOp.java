package cs219.proj;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.shell.PathData;
import org.apache.hadoop.io.IOUtils;

public class PrivateFileOp {
	
	/*
	 * Encrypt private file and save key locally.
	 */
	public static void init(PathData file) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
    //Read original file into a byte array
    int fileLenInChar = (int)file.stat.getLen();
    byte[] tempIn = new byte[fileLenInChar];
    FSDataInputStream isTemp = file.fs.open(file.path);
    isTemp.read(tempIn, 0, fileLenInChar);
    IOUtils.closeStream(isTemp);
    
    //Generate a random key
    SecureRandom rd = new SecureRandom();
    BigInteger bi = new BigInteger(120, 0, rd);
    byte[] key = bi.toByteArray();
    SecretKeySpec sk = new SecretKeySpec(key, "AES");
    
    //Store key in a mapping file locally
    Path dir = Paths.get("/Users/" + file.stat.getOwner() + "/hadoopkeys");
    if(!Files.exists(dir)){
      Files.createDirectories(dir);
    }
    Path keyPathFile = Paths.get(dir.toString() + "/" + file.path.toString().replaceAll("[:/.]", "") + ".key");
    Files.write(keyPathFile, key, StandardOpenOption.CREATE);
    
    //Encrypt file
    AESEncryption aes = new AESEncryption();
    byte[] tempOut = aes.encrypt(tempIn, sk);
    file.fs.truncate(file.path, 0);
    FSDataOutputStream outTemp = file.fs.append(file.path);
    outTemp.write(tempOut, 0, tempOut.length);
    IOUtils.closeStream(outTemp);
    
    return;
	}
	
	
	/*
	 * Decrypt private file for display.
	 */
	public static byte[] dec (InputStream in, PathData file) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
    byte[] localKey;
    byte[] decrypted = null;
    Path keyFile = Paths.get("/Users/" + file.stat.getOwner() + "/hadoopkeys/" + file.path.toString().replaceAll("[:/.]", "") + ".key"); 
    if (Files.exists(keyFile)) {
    	System.out.println("Key Found!");
    	localKey = Files.readAllBytes(keyFile);
      SecretKeySpec localsk = new SecretKeySpec(localKey, "AES");
      
      int encFileLenInChar = (int)file.stat.getLen();
      System.out.println(encFileLenInChar);
      byte[] encrypted = new byte[encFileLenInChar];
      AESEncryption aes = new AESEncryption();
      in.read(encrypted, 0, encFileLenInChar);
      decrypted = aes.decrypt(encrypted, localsk);
//      System.out.println(new String(decrypted));
    }
		
		return decrypted;
	}
	
	/*
	 * Encrypt input byte array for write/append.
	 */
	public static byte[] enc (byte[] in, PathData file) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
    byte[] localKey;
    byte[] encrypted = null;
    Path keyFile = Paths.get("/Users/" + file.stat.getOwner() + "/hadoopkeys/" + file.path.toString().replaceAll("[:/.]", "") + ".key"); 
    if (Files.exists(keyFile)) {
    	System.out.println("Key Found!");
    	localKey = Files.readAllBytes(keyFile);
      SecretKeySpec localsk = new SecretKeySpec(localKey, "AES");
      
      AESEncryption aes = new AESEncryption();
      encrypted = aes.encrypt(in, localsk);
    }
		
		return encrypted;
	}
	
	
	/*
	 * Restore the encrypted file.
	 */
	public static void restore(PathData file, Boolean deleteKey) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
	  byte[] localKey;
	  Path keyFile = Paths.get("/Users/" + file.stat.getOwner() + "/hadoopkeys/" + file.path.toString().replaceAll("[:/.]", "") + ".key"); 
	  if (Files.exists(keyFile)) {
	  	System.out.println("Key Found");
	  	localKey = Files.readAllBytes(keyFile);
	    SecretKeySpec localsk = new SecretKeySpec(localKey, "AES");
	    
	    int encFileLenInChar = (int)file.stat.getLen();
	    System.out.println("Encrypted file length: " + encFileLenInChar);
	    byte[] encrypted = new byte[encFileLenInChar];
	    FSDataInputStream isTemp = file.fs.open(file.path);
	    isTemp.read(encrypted, 0, encFileLenInChar);
	    IOUtils.closeStream(isTemp);
      AESEncryption aes = new AESEncryption();
	    byte[] decrypted = aes.decrypt(encrypted, localsk);
	    
	    file.fs.truncate(file.path, 0);
	    FSDataOutputStream outTemp = file.fs.append(file.path);
	    outTemp.write(decrypted, 0, decrypted.length);
	    IOUtils.closeStream(outTemp);
	  }
	}
}