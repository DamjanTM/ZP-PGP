package etf.openpgp.pd170312duu170714d;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.spec.DHParameterSpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class ElGamal {
	private KeyPair my_key_pair;
		
	public byte[] encrypt(byte[] input_) throws GeneralSecurityException
	{
		SecureRandom rand = new SecureRandom();
		 
		Cipher cipher = Cipher.getInstance("ElGamal", "BC");
		cipher.init(Cipher.ENCRYPT_MODE, my_key_pair.getPublic(), rand);
		return cipher.doFinal(input_);
	}
	
	public byte[] decrypt(byte[] encryptedData_) throws GeneralSecurityException
	{
		Cipher cipher = Cipher.getInstance("ElGamal", "BC");
		cipher.init(Cipher.DECRYPT_MODE, my_key_pair.getPrivate());
		return cipher.doFinal(encryptedData_);
	}
	
	public void generate_keypair(int size_) {
        try {
        	AlgorithmParameterGenerator a = AlgorithmParameterGenerator.getInstance("ElGamal", "BC");
            a.init(size_, new SecureRandom());
            AlgorithmParameters params = a.generateParameters();
            DHParameterSpec elP = (DHParameterSpec)params.getParameterSpec(DHParameterSpec.class);

        	KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ElGamal", "BC");
            keyPairGenerator.initialize(elP);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            this.my_key_pair = keyPair;
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidParameterSpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	public void export_keypair() {
		  try (FileOutputStream stream = new FileOutputStream("../eg_keys.asc")) {
			  	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(stream));
			  	bw.write(KeyTools.bytesToHex(my_key_pair.getPrivate().getEncoded()));
			  	bw.newLine();
			  	bw.write(KeyTools.bytesToHex(my_key_pair.getPublic().getEncoded()));
			  	bw.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public KeyPair import_keypair(String path) {
		KeyPair imported = null;
		if(path == null)path = "../eg_keys.asc";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
			String privateKeyString = reader.readLine();
			String publicKeyString = reader.readLine();
			byte[] privateKeyBytes = KeyTools.hexToBytes(privateKeyString);
			byte[] publicKeyBytes = KeyTools.hexToBytes(publicKeyString);
			System.out.println("imported byte array: priv :" + privateKeyString);
			System.out.println("pub: " + publicKeyString);
        	KeyFactory kf = KeyFactory.getInstance("ElGamal"); // or "EC" or whatever
        	PKCS8EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
			PrivateKey privateKey = kf.generatePrivate(encodedKeySpec);
			EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
			PublicKey publicKey = kf.generatePublic(publicKeySpec);
			imported = new KeyPair(publicKey, privateKey);
		} catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return imported;
	}
	
	public String returnPublicKey() {
		return new String(my_key_pair.getPublic().getEncoded(), StandardCharsets.UTF_8);
	}
	

	public static void main( String args[]) throws GeneralSecurityException {
		Security.addProvider(new BouncyCastleProvider());

		String msg = "We ready we ready we ready! FOR Y'ALL!";
		ElGamal lg = new ElGamal();
		lg.generate_keypair(512);
		System.out.println("Enkriptovacemo poruku '" + new String(msg.getBytes(), StandardCharsets.UTF_8) + "'");
		lg.export_keypair();
		KeyPair imported_kp = lg.import_keypair(null);
		if(lg.my_key_pair.getPrivate().equals(imported_kp.getPrivate()) && lg.my_key_pair.getPublic().equals(imported_kp.getPublic()))System.out.println("IMPORT YAY");
		else System.out.println("IMPORT NAY");

		byte[] encryptedData = lg.encrypt(msg.getBytes());
		
		System.out.println("Enkriptovani podaci: " + encryptedData);
		
		byte[] decryptedData = lg.decrypt(encryptedData);
		
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
