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
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPKeyPair;

public class DSA {
<<<<<<< HEAD
	private KeyPair my_key_pair;
	
	public byte[] generateSignature(byte[] input_) throws GeneralSecurityException
	{
		Signature signature = Signature.getInstance("SHA1withDSA", "BC");
		signature.initSign(my_key_pair.getPrivate());
		signature.update(input_);
		return signature.sign();
	}
	
	public boolean verifySignature(byte[] input_, byte[] encSignature_) throws GeneralSecurityException
	{
		Signature signature = Signature.getInstance("SHA1withDSA", "BC");
		signature.initVerify(my_key_pair.getPublic());
		signature.update(input_);
		return signature.verify(encSignature_);
	}
	
	public void generate_keypair(int key_size_) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA", "BC");
            keyPairGenerator.initialize(key_size_);
            this.my_key_pair = keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
<<<<<<< HEAD
	
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
	
	public String returnPublicKey() {
		return new String(my_key_pair.getPublic().getEncoded(), StandardCharsets.UTF_8);
	}
	
	public String returnPrivateKey() {
		return new String(my_key_pair.getPrivate().getEncoded(), StandardCharsets.UTF_8);
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
		byte[] signature = ldp.generateSignature(msg.getBytes());
		
		System.out.println("Potpis: " + signature);
		
		signature = ldp.generateSignature(msg.getBytes());
		
		System.out.println("Potpis: " + signature);
		
		boolean worked = ldp.verifySignature(msg.getBytes(), signature);
		
		if (worked) {
			System.out.println("Jupi!");
		} else System.out.println("Bu hu!");
	}
=======

    public void import_keypair(String path) {
        if(path == null)path = "../dsa_keys.asc";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String privateKeyString = reader.readLine();
            String publicKeyString = reader.readLine();
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString);
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
            KeyFactory kf = KeyFactory.getInstance("DSA"); // or "EC" or whatever
            PKCS8EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            PrivateKey privateKey = kf.generatePrivate(encodedKeySpec);
            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            PublicKey publicKey = kf.generatePublic(publicKeySpec);
            my_keypair = new KeyPair(publicKey, privateKey);
            this.my_pgp_keypair = new JcaPGPKeyPair( PGPPublicKey.DSA, my_keypair, new Date() );
        } catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (PGPException ex) {
            Logger.getLogger(DSA.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
>>>>>>> 69c08f89cf7b9f14a4cac8ad23c26fa40cbb5e67
}
