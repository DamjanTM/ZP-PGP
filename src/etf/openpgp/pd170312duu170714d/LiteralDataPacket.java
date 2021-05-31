package etf.openpgp.pd170312duu170714d;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class LiteralDataPacket {
	private char type='t';
	private String filename;
	private String data;
	private KeyPair my_key_pair;
	
	public static byte[] generateSignature(PrivateKey ecPrivate, byte[] input) throws GeneralSecurityException
	{
		Signature signature = Signature.getInstance("SHA1withDSA", "BC");
		signature.initSign(ecPrivate);
		signature.update(input);
		return signature.sign();
	}
	
	public static boolean verifySignature(PublicKey ecPublic, byte[] input, byte[] encSignature) throws GeneralSecurityException
	{
		Signature signature = Signature.getInstance("SHA1withDSA", "BC");
		signature.initVerify(ecPublic);
		signature.update(input);
		return signature.verify(encSignature);
	}
	
	public void generate_DSA_keypair(int key_size) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA");
            keyPairGenerator.initialize(key_size);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            this.my_key_pair = keyPair;
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
	
	public static void main( String args[]) throws GeneralSecurityException {
		Security.addProvider(new BouncyCastleProvider());

		String msg = "Hey bouse!";
		LiteralDataPacket ldp = new LiteralDataPacket();
		ldp.generate_DSA_keypair(1024);
		System.out.println("Public key:" + ldp.my_key_pair.getPublic());
		System.out.println("Private key:" + ldp.my_key_pair.getPrivate().getEncoded());
		System.out.println("Potpisacemo poruku '" + msg + "'");
		
		byte[] signature = ldp.generateSignature(ldp.my_key_pair.getPrivate(), msg.getBytes());
		
		System.out.println("Potpis: " + signature);
		
		boolean worked = ldp.verifySignature(ldp.my_key_pair.getPublic(), msg.getBytes(), signature);
		
		if (worked) {
			System.out.println("Jupi!");
		} else System.out.println("Bu hu!");
	}
}
