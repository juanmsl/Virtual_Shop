package Utilities;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/** Class Utils.java to represent
 * @author juanm */
public class Utils {
	
	public static String getHash(String text) {
		String hash = null;
		try {
			MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
			sha256.update(text.getBytes("UTF-8"));
			byte[] digest = sha256.digest();
			StringBuffer stringBuffer = new StringBuffer();
			for (byte element : digest) {
				stringBuffer.append(String.format("%02x", element));
			}
			hash = stringBuffer.toString();
		}
		catch (NoSuchAlgorithmException event) {
			System.out.println("Error: [" + event.getMessage() + "]");
		}
		catch (UnsupportedEncodingException event) {
			System.out.println("Error: [" + event.getMessage() + "]");
		}
		return hash;
	}
	
}
