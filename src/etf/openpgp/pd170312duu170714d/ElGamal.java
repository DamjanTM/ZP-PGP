package etf.openpgp.pd170312duu170714d;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.spec.DHParameterSpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class ElGamal {
	private KeyPair my_key_pair;
		
	public static byte[] encrypt(PublicKey ecPublic_, byte[] input_) throws GeneralSecurityException
	{
		SecureRandom rand = new SecureRandom();
		 
		Cipher cipher = Cipher.getInstance("ElGamal", "BC");
		cipher.init(Cipher.ENCRYPT_MODE, ecPublic_, rand);
		return cipher.doFinal(input_);
	}
	
	public static byte[] decrypt(PrivateKey ecPrivate_, byte[] encryptedData_) throws GeneralSecurityException
	{
		Cipher cipher = Cipher.getInstance("ElGamal", "BC");
		cipher.init(Cipher.DECRYPT_MODE, ecPrivate_);
		return cipher.doFinal(encryptedData_);
	}
	
	public void generate_ElGamal_keypair(int size_) {
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
	
	public void export_ElGamal_keypair() {
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out = null;
		try {
		  out = new ObjectOutputStream(bos);   
		  out.writeObject(my_key_pair.getPrivate().getEncoded());
		  out.flush();
		  byte[] export_bytes = bos.toByteArray();
		  try (FileOutputStream stream = new FileOutputStream("../eg_keys.asc")) {
			    stream.write(export_bytes);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		  try {
		    bos.close();
		  } catch (IOException ex) {
		    // ignore close exception
		  }
		}
	}
	
	public KeyPair import_ElGamal_keypair2(String path) {
		KeyPair imported = null;
		if(path == null)path = "../eg_keys.asc";
		try {
			byte[] privateKeyBytes = Files.readAllBytes(Paths.get(path));
        	KeyFactory kf = KeyFactory.getInstance("ElGamal"); // or "EC" or whatever
        	PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
        	imported = new KeyPair(null, privateKey);
		} catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return imported;
	}
	
	public KeyPair import_ElGamal_keypair1(String path) {
		KeyPair imported = null;
		if(path == null)path = "../eg_keys.asc";
		try {
			byte[] import_array = Files.readAllBytes(Paths.get(path));
	        try (ByteArrayInputStream b = new ByteArrayInputStream(import_array)) {
	            try (ObjectInputStream o = new ObjectInputStream(b)) {
	            	imported = new KeyPair(null, new PrivateKey(import_array));
	            } catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return imported;
	}
	
	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for (int j = 0; j < bytes.length; j++) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
	        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	public static void main( String args[]) throws GeneralSecurityException {
		Security.addProvider(new BouncyCastleProvider());

		String msg = "We ready we ready we ready! FOR Y'ALL!";
		ElGamal lg = new ElGamal();
		lg.generate_ElGamal_keypair(512);
		System.out.println("Public key:" + lg.my_key_pair.getPublic().getEncoded());
		System.out.println("Private key:" + lg.my_key_pair.getPrivate().getEncoded());
		System.out.println("Enkriptovacemo poruku '" + new String(msg.getBytes(), StandardCharsets.UTF_8) + "'");
		lg.export_ElGamal_keypair();
		KeyPair imported_kp = lg.import_ElGamal_keypair1(null);
		if(lg.my_key_pair.getPrivate().equals(imported_kp.getPrivate()))System.out.println("IMPORT YAY");
		else System.out.println("IMPORT NAY");
		System.out.println("Original KEY: " + bytesToHex(imported_kp.getPrivate().getEncoded()));
		System.out.println("IMPORTED KEY: " + bytesToHex(imported_kp.getPrivate().getEncoded()));
		byte[] encryptedData = encrypt(lg.my_key_pair.getPublic(), msg.getBytes());
		
		System.out.println("Enkriptovani podaci: " + encryptedData);
		
		byte[] decryptedData = decrypt(lg.my_key_pair.getPrivate(), encryptedData);
		
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