

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Security {
	public static String hashSha3(String password){
		try {
			MessageDigest sha = MessageDigest.getInstance("SHA-256");
			byte[] pass = password.getBytes();
			sha.update(pass);
			byte[] digest= sha.digest();
			StringBuffer hexDigest = new StringBuffer();
			for (int i = 0; i < digest.length; i++){
				hexDigest.append(Integer.toString((digest[i]&0xff)+0x100,16).substring(1));
			}
			return hexDigest.toString();
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		return null;
	}
	public static String hashMD5(String password){
		try {
			MessageDigest sha = MessageDigest.getInstance("MD5");
			byte[] pass = password.getBytes();
			sha.update(pass);
			byte[] digest= sha.digest();
			StringBuffer hexDigest = new StringBuffer();
			for (int i = 0; i < digest.length; i++){
				hexDigest.append(Integer.toString((digest[i]&0xff)+0x100,16).substring(1));
			}
			return hexDigest.toString();
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		return null;
	}
	private static String generateIV(){
		String iv = "";
		Random rand = new Random();
		for (int i =0;i<16;++i){
			iv += rand.nextInt(10);
		}
		return iv;
	
	}

	public static String encrypt(String key, String value) {
	
		key = hashMD5(key);
		byte[] inv = generateIV().getBytes();
	
    	try {
    		IvParameterSpec iv = new IvParameterSpec(inv);
    		SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

        	Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        	cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        	byte[] encrypted = cipher.doFinal(value.getBytes());
        
        	return new String(inv,"UTF-8")+java.util.Base64.getEncoder().encodeToString(encrypted);
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}

    	return null;
	}
	public static String decrypt(String key, String encrypted) throws InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
	
		key = hashMD5(key);
	
		String inv = encrypted.substring(0,16);
		encrypted = encrypted.substring(16);
	
    
		IvParameterSpec iv = new IvParameterSpec(inv.getBytes("UTF-8"));
		SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

		byte[] original = cipher.doFinal( java.util.Base64.getDecoder().decode(encrypted));
		return new String(original);
	}
	

}
