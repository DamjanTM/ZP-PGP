/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.openpgp.pd170312duu170714d;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.spec.DHParameterSpec;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPKeyPair;

/**
 *
 * @author Uros
 */
public class ElGamal {
<<<<<<< HEAD
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
=======

    protected PGPKeyPair my_pgp_keypair;
    private KeyPair my_keypair;
    
    
    public void setMy_pgp_keypair(PGPKeyPair my_pgp_keypair) {
        this.my_pgp_keypair = my_pgp_keypair;
    }

    public void setMy_keypair(KeyPair my_keypair) {
        this.my_keypair = my_keypair;
    }

    public PGPKeyPair getMy_pgp_keypair() {
        return my_pgp_keypair;
    }

    public KeyPair getMy_keypair() {
        return my_keypair;
    }

    public void generate_keypair(int size_) {
>>>>>>> 69c08f89cf7b9f14a4cac8ad23c26fa40cbb5e67
        try {
            AlgorithmParameterGenerator a = AlgorithmParameterGenerator.getInstance("ElGamal", "BC");
            a.init(size_, new SecureRandom());
            AlgorithmParameters params = a.generateParameters();
            DHParameterSpec elP = (DHParameterSpec)params.getParameterSpec(DHParameterSpec.class);

            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ElGamal", "BC");
            keyPairGenerator.initialize(elP);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            this.my_keypair = keyPair;
            this.my_pgp_keypair = new JcaPGPKeyPair( PGPPublicKey.ELGAMAL_ENCRYPT, my_keypair, new Date() );
        } catch (NoSuchAlgorithmException | NoSuchProviderException | 
                InvalidAlgorithmParameterException | InvalidParameterSpecException e) 
        {} catch (PGPException ex) {
            Logger.getLogger(ElGamal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
<<<<<<< HEAD
	
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
=======
    
    public void export_keypair() {
        try (FileOutputStream stream = new FileOutputStream("../eg_keys.asc"); 
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(stream))) {
            bw.write(Base64.getEncoder().encodeToString(my_keypair.getPrivate().getEncoded()));
            bw.newLine();
            bw.write(Base64.getEncoder().encodeToString(my_keypair.getPublic().getEncoded()));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
>>>>>>> 69c08f89cf7b9f14a4cac8ad23c26fa40cbb5e67

        } catch (IOException e) {
            // TODO Auto-generated catch block

<<<<<<< HEAD
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
=======
        }
    }
	
    public void import_keypair(String path) {
            if(path == null)path = "../eg_keys.asc";
            try {
                BufferedReader reader = new BufferedReader(new FileReader(path));
                String privateKeyString = reader.readLine();
                String publicKeyString = reader.readLine();
                byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString);
                byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
                System.out.println("imported byte array: priv :" + privateKeyString);
                System.out.println("pub: " + publicKeyString);
                KeyFactory kf = KeyFactory.getInstance("ElGamal"); // or "EC" or whatever
                PKCS8EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
                PrivateKey privateKey = kf.generatePrivate(encodedKeySpec);
                EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
                PublicKey publicKey = kf.generatePublic(publicKeySpec);
                this.my_keypair = new KeyPair(publicKey, privateKey);
                this.my_pgp_keypair = new JcaPGPKeyPair( PGPPublicKey.ELGAMAL_ENCRYPT, my_keypair, new Date() );
            } catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (PGPException ex) {
            Logger.getLogger(ElGamal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
>>>>>>> 69c08f89cf7b9f14a4cac8ad23c26fa40cbb5e67
}
