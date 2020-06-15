package ct.wiremock.demo._global.Utils.crypto;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class AES256 extends AbstractCrypto {
	
	private static AES256 instance = null;
	
	public static AES256 getInstance()
	{
		if (instance == null)
			instance = new AES256();
		
		return instance;
	}
	
	private static String secretKey = "{zvUnP_Fx=>Zc}RX&*qrbsT4eJ~,t5(57teJmC?^";
	private static String salt = "qTL^QytM^<qy+p}SXd@T)dgP3M!#4yVv5vs4+#u+";
	
	private static Cipher returnCipher(int cipherMode) {
		byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		IvParameterSpec ivspec = new IvParameterSpec(iv);
		Cipher cipher = null;
		
		SecretKeyFactory factory = null;
		try {
			factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
			
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(cipherMode, secretKey, ivspec);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
			String message = "Error while encrypting: " + e.toString();
			System.out.println(message);
		}
		
		return cipher;
	}
	
	public String encrypt(String s) {
		String result = null;
		try
		{
			if (s != null) {
				Cipher cipher = returnCipher(Cipher.ENCRYPT_MODE);
				result = Base64.getEncoder().encodeToString(cipher.doFinal(s.getBytes("UTF-8")));
			}
		}
		catch (Exception e)
		{
			String message = "Error while encrypting: " + e.toString();
			System.out.println(message);
		}
		return result;
	}
	
	public String decrypt(String s) {
		String result = null;
		try
		{
			if (s != null) {
				Cipher cipher = returnCipher(Cipher.DECRYPT_MODE);
				result = new String(cipher.doFinal(Base64.getDecoder().decode(s)));
			}
		}
		catch (Exception e) {
			String message = "Error while decrypting: " + e.toString();
			System.out.println(message);
		}
		
		return result;
	}
}
