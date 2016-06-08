package org.apache.hadoop.fs;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class AESEncryption {
	Cipher cipher;
	
	public AESEncryption() throws NoSuchAlgorithmException, NoSuchPaddingException{
		this.cipher = Cipher.getInstance("AES");
	}
	
	byte[] encrypt(byte[] originalBytes, SecretKey sk) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		cipher.init(Cipher.ENCRYPT_MODE, sk);
		byte[] encryptedBytes = cipher.doFinal(originalBytes);
		return encryptedBytes;
	}
	
	byte[] decrypt(byte[] encryptedBytes, SecretKey sk) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		cipher.init(Cipher.DECRYPT_MODE, sk);
		byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
		return decryptedBytes;
	}
	
}
