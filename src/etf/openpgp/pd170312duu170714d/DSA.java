package etf.openpgp.pd170312duu170714d;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class DSA {
	private KeyPair my_key_pair;
	
	public static byte[] generateSignature(PrivateKey ecPrivate_, byte[] input_) throws GeneralSecurityException
	{
		Signature signature = Signature.getInstance("SHA1withDSA", "BC");
		signature.initSign(ecPrivate_);
		signature.update(input_);
		return signature.sign();
	}
	
	public static boolean verifySignature(PublicKey ecPublic_, byte[] input_, byte[] encSignature_) throws GeneralSecurityException
	{
		Signature signature = Signature.getInstance("SHA1withDSA", "BC");
		signature.initVerify(ecPublic_);
		signature.update(input_);
		return signature.verify(encSignature_);
	}
	
	public void generate_keypair(int key_size_) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA");
            keyPairGenerator.initialize(key_size_);
            this.my_key_pair = keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
	
	public void export_keypair() {
		  System.out.println("exported byte array: priv: " + KeyTools.bytesToHex(my_key_pair.getPrivate().getEncoded()));
		  System.out.println("pub: " + KeyTools.bytesToHex(my_key_pair.getPublic().getEncoded()));
		  try (FileOutputStream stream = new FileOutputStream("../dsa_keys.asc")) {
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
		if(path == null)path = "../dsa_keys.asc";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
			String privateKeyString = reader.readLine();
			String publicKeyString = reader.readLine();
			byte[] privateKeyBytes = KeyTools.hexToBytes(privateKeyString);
			byte[] publicKeyBytes = KeyTools.hexToBytes(publicKeyString);
			System.out.println("imported byte array: priv :" + privateKeyString);
			System.out.println("pub: " + publicKeyString);
        	KeyFactory kf = KeyFactory.getInstance("DSA"); // or "EC" or whatever
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
	
	public static void main( String args[]) throws GeneralSecurityException {
		Security.addProvider(new BouncyCastleProvider());

		String msg = "We ready we ready we ready! FOR Y'ALL!";
		DSA ldp = new DSA();
		ldp.generate_keypair(1024);
		System.out.println("Public key:" + ldp.my_key_pair.getPublic().getEncoded());
		System.out.println("Private key:" + ldp.my_key_pair.getPrivate().getEncoded());
		System.out.println("Potpisacemo poruku '" + msg + "'");
		ldp.export_keypair();
		KeyPair imported_kp = ldp.import_keypair(null);
		if(ldp.my_key_pair.getPrivate().equals(imported_kp.getPrivate()) && ldp.my_key_pair.getPublic().equals(imported_kp.getPublic()))System.out.println("IMPORT YAY");
		else System.out.println("IMPORT NAY");
		byte[] signature = generateSignature(ldp.my_key_pair.getPrivate(), msg.getBytes());
		
		System.out.println("Potpis: " + signature);
		
		signature = generateSignature(ldp.my_key_pair.getPrivate(), msg.getBytes());
		
		System.out.println("Potpis: " + signature);
		
		boolean worked = verifySignature(ldp.my_key_pair.getPublic(), msg.getBytes(), signature);
		
		if (worked) {
			System.out.println("Jupi!");
		} else System.out.println("Bu hu!");
	}
}
