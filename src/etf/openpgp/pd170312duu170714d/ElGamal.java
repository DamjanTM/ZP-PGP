package etf.openpgp.pd170312duu170714d;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.Cipher;
import javax.crypto.spec.DHParameterSpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class ElGamal {

	private char type='t';
	private String filename;
	private String data;
	private KeyPair my_key_pair;
	
	private static BigInteger  g1024 = new BigInteger("1db17639cdf96bc4eabba19454f0b7e5bd4e14862889a725c96eb61048dcd676ceb303d586e30f060dbafd8a571a39c4d823982117da5cc4e0f89c77388b7a08896362429b94a18a327604eb7ff227bffbc83459ade299e57b5f77b50fb045250934938efa145511166e3197373e1b5b1e52de713eb49792bedde722c6717abf", 16);
    private static BigInteger  p1024 = new BigInteger("a00e283b3c624e5b2b4d9fbc2653b5185d99499b00fd1bf244c6f0bb817b4d1c451b2958d62a0f8a38caef059fb5ecd25d75ed9af403f5b5bdab97a642902f824e3c13789fed95fa106ddfe0ff4a707c85e2eb77d49e68f2808bcea18ce128b178cd287c6bc00efa9a1ad2a673fe0dceace53166f75b81d6709d5f8af7c66bb7", 16);
	
	public static byte[] encrypt(PublicKey ecPublic_, byte[] input_) throws GeneralSecurityException
	{
		SecureRandom    rand = new SecureRandom();
		 
		Cipher cipher = Cipher.getInstance("ElGamal", "BC");
		cipher.init(Cipher.ENCRYPT_MODE, ecPublic_, rand);
		return cipher.doFinal(input_);
	}
	
	public static byte[] decrypt(PrivateKey ecPrivate_, byte[] input_) throws GeneralSecurityException
	{
		Cipher cipher = Cipher.getInstance("ElGamal", "BC");
		cipher.init(Cipher.DECRYPT_MODE, ecPrivate_);
		return cipher.doFinal(input_);
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
	
	public static void main( String args[]) throws GeneralSecurityException {
		Security.addProvider(new BouncyCastleProvider());

		String msg = "We ready we ready we ready! FOR Y'ALL!";
		ElGamal lg = new ElGamal();
		lg.generate_ElGamal_keypair(512);
		System.out.println("Public key:" + lg.my_key_pair.getPublic().getEncoded());
		System.out.println("Private key:" + lg.my_key_pair.getPrivate().getEncoded());
		System.out.println("Enkriptovacemo poruku '" + new String(msg.getBytes(), StandardCharsets.UTF_8) + "'");
		
		byte[] encryptedData = lg.encrypt(lg.my_key_pair.getPublic(), msg.getBytes());
		
		System.out.println("Enkriptovani podaci: " + encryptedData);
		
		byte[] decryptedData = lg.decrypt(lg.my_key_pair.getPrivate(), encryptedData);
		
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
