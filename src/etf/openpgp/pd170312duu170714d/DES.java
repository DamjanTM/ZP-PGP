package etf.openpgp.pd170312duu170714d;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jcajce.provider.symmetric.DESede;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class DES {
	private SecretKey my_key;
	
	public static byte[] encrypt(SecretKey key_, byte[] input_) throws GeneralSecurityException
	{
		SecureRandom rand = new SecureRandom();
		 
		Cipher cipher = Cipher.getInstance("DESEDE", "BC");
		cipher.init(Cipher.ENCRYPT_MODE, key_, rand);
		return cipher.doFinal(input_);
	}
	
	public static byte[] decrypt(SecretKey key_, byte[] encryptedData_) throws GeneralSecurityException
	{
		Cipher cipher = Cipher.getInstance("DESEDE", "BC");
		cipher.init(Cipher.DECRYPT_MODE, key_);
		return cipher.doFinal(encryptedData_);
	}
	
	public void generate_key(int strength_) {
        	SecureRandom rand = new SecureRandom();
            KeyGenerator keyGen;
            
			try {
				keyGen = KeyGenerator.getInstance("DESEDE", "BC");
	            keyGen.init(strength_, rand);
	            this.my_key = keyGen.generateKey();
			} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }
	
	public void export_key() {
		  System.out.println("exported byte array: " + KeyTools.bytesToHex(my_key.getEncoded()));
		  try (FileOutputStream stream = new FileOutputStream("../des_key.asc")) {
			  	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(stream));
			  	bw.write(KeyTools.bytesToHex(my_key.getEncoded()));
			  	bw.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public SecretKey import_key(String path) {
		SecretKey imported = null;
		if(path == null)path = "../des_key.asc";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
			String KeyString = reader.readLine();
			byte[] KeyBytes = KeyTools.hexToBytes(KeyString);
			System.out.println("imported byte array: priv :" + KeyString);
			System.out.println("pub: " + KeyString);
	      	imported = new SecretKeySpec(KeyBytes, 0, KeyBytes.length, "DES"); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return imported;
	}
	
	public static void main( String args[]) throws GeneralSecurityException {
		Security.addProvider(new BouncyCastleProvider());

		String msg = "We ready we ready we ready! FOR Y'ALL!";
		DES des = new DES();
		des.generate_key(128);
		des.export_key();
		SecretKey imported_sk = des.import_key(null);
		if(des.my_key.equals(imported_sk))System.out.println("IMPORT YAY");
		else System.out.println("IMPORT NAY");
		System.out.println("Key:" + des.my_key.getEncoded());
		System.out.println("Enkriptovacemo poruku '" + new String(msg.getBytes(), StandardCharsets.UTF_8) + "'");
		
		byte[] encryptedData = encrypt(des.my_key, msg.getBytes());
		
		System.out.println("Enkriptovani podaci: " + encryptedData);
		
		byte[] decryptedData = decrypt(des.my_key, encryptedData);
		
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
