package ct.wiremock.demo._global.Utils.crypto;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class TripleDes extends AbstractCrypto{
	
	private static TripleDes instance = null;
	
	public static TripleDes getInstance()
	{
		if (instance == null)
			instance = new TripleDes();
		
		return instance;
	}
	
	private static final String UNICODE_FORMAT = "UTF8";
	private static final String DESEDE_ENCRYPTION_SCHEME = "DESede";

	private SecretKey returnSecretKey() {
		SecretKey _key = null;
		
		try {
			String myEncryptionKey = "2$a>`)/XMy_&+9+qyZ:K$[GvV?@@Zwb^8=y/m9P/";
			byte[] arrayBytes = myEncryptionKey.getBytes(UNICODE_FORMAT);
			KeySpec ks = new DESedeKeySpec(arrayBytes);
			SecretKeyFactory skf = SecretKeyFactory.getInstance(DESEDE_ENCRYPTION_SCHEME);
			_key = skf.generateSecret(ks);
		} catch (UnsupportedEncodingException | InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException e) {
			String message = "Error while encrypting: " + e.toString();
			System.out.println(message);
		}
		
		return _key;
	}
	
	private Cipher returnCipher(int cipherMode) {
		Cipher cipher = null;
		
		try {
			cipher = Cipher.getInstance(DESEDE_ENCRYPTION_SCHEME);
			cipher.init(cipherMode, returnSecretKey());
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
			e.printStackTrace();
		}
		
		return cipher;
	}
	
	public String encrypt(String s) {
		String result = null;
		try {
			if (s != null) {
				byte[] plainText = s.getBytes(UNICODE_FORMAT);
				byte[] encryptedText = returnCipher(Cipher.ENCRYPT_MODE).doFinal(plainText);
				result = new String(Base64.encodeBase64(encryptedText));
			}
		} catch (Exception e) {
			String message = "Error while encrypting: " + e.toString();
			System.out.println(message);
		}
		return result;
	}
	
	
	public String decrypt(String s) {
		String result = null;
		try {
			if (s != null) {
				byte[] encryptedText = Base64.decodeBase64(s);
				byte[] plainText = returnCipher(Cipher.DECRYPT_MODE).doFinal(encryptedText);
				result = new String(plainText);
			}
		} catch (Exception e) {
			String message = "Error while encrypting: " + e.toString();
			System.out.println(message);
		}
		return result;
	}
	
}
