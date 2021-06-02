package etf.openpgp.pd170312duu170714d;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

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
	
	public void generate_DSA_keypair(int key_size_) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA");
            keyPairGenerator.initialize(key_size_);
            this.my_key_pair = keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
	
	public static void main( String args[]) throws GeneralSecurityException {
		Security.addProvider(new BouncyCastleProvider());

		String msg = "We ready we ready we ready! FOR Y'ALL!";
		DSA ldp = new DSA();
		ldp.generate_DSA_keypair(1024);
		System.out.println("Public key:" + ldp.my_key_pair.getPublic().getEncoded());
		System.out.println("Private key:" + ldp.my_key_pair.getPrivate().getEncoded());
		System.out.println("Potpisacemo poruku '" + msg + "'");
		
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
