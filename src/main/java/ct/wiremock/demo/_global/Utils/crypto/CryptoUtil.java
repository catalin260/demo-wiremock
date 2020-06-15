package ct.wiremock.demo._global.Utils.crypto;

public class CryptoUtil {
	public static String encrypt(CryptoEnum cryptoMethod, String s) {
		String result = null;
		switch (cryptoMethod) {
			case TripleDes:
				result = TripleDes.getInstance().encrypt(s);
				break;
			case AES256:
				result = AES256.getInstance().encrypt(s);
				break;
		}
		
		return result;
	}
	
	public static String decrypt(CryptoEnum cryptoEnum, String s) {
		String result = null;
		switch (cryptoEnum) {
			case TripleDes:
				result = TripleDes.getInstance().decrypt(s);
				break;
			case AES256:
				result = AES256.getInstance().decrypt(s);
				break;
		}
		
		return result;
	}
}
