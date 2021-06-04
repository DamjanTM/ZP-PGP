package etf.openpgp.pd170312duu170714d;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class IDEA {
	private SecretKey my_key;
	
	public static byte[] encrypt(SecretKey key_, byte[] input_) throws GeneralSecurityException
	{
		SecureRandom rand = new SecureRandom();
		 
		Cipher cipher = Cipher.getInstance("IDEA", "BC");
		cipher.init(Cipher.ENCRYPT_MODE, key_, rand);
		return cipher.doFinal(input_);
	}
	
	public static byte[] decrypt(SecretKey key_, byte[] encryptedData_) throws GeneralSecurityException
	{
		Cipher cipher = Cipher.getInstance("IDEA", "BC");
		cipher.init(Cipher.DECRYPT_MODE, key_);
		return cipher.doFinal(encryptedData_);
	}
	
	public void generate_IDEA_key(int strength_) {
        	SecureRandom rand = new SecureRandom();
            KeyGenerator keyGen;
            
			try {
				keyGen = KeyGenerator.getInstance("IDEA", "BC");
	            keyGen.init(strength_, rand);
	            this.my_key = keyGen.generateKey();
			} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }
	
	public static void main( String args[]) throws GeneralSecurityException {
		Security.addProvider(new BouncyCastleProvider());

		String msg = "We ready we ready we ready! FOR Y'ALL!";
		IDEA idea = new IDEA();
		idea.generate_IDEA_key(128);
		System.out.println("Key:" + idea.my_key.getEncoded());
		System.out.println("Enkriptovacemo poruku '" + new String(msg.getBytes(), StandardCharsets.UTF_8) + "'");
		
		byte[] encryptedData = encrypt(idea.my_key, msg.getBytes());
		
		System.out.println("Enkriptovani podaci: " + encryptedData);
		
		byte[] decryptedData = decrypt(idea.my_key, encryptedData);
		
		System.out.println("Dekriptovani podaci: " + new String(decryptedData, StandardCharsets.UTF_8));
		
		boolean worked = true;
		
		for(int i=0; i<msg.getBytes().length; i++) {
			if (decryptedData[i] != msg.getBytes()[i]) worked = false;
		}
		
		if (worked) {
			System.out.println("Jupi!");
		} else System.out.println("Bu hu!");
	}
}
