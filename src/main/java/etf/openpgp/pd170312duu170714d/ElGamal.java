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
import java.security.Security;
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
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPKeyPair;

/**
 *
 * @author Uros
 */
public class ElGamal {

    protected PGPKeyPair my_pgp_keypair;
    private KeyPair my_keypair;
    
    ElGamal(int size_){
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ElGamal", "BC");
            keyPairGenerator.initialize(size_);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            this.my_keypair = keyPair;
            this.my_pgp_keypair = new JcaPGPKeyPair( PGPPublicKey.ELGAMAL_ENCRYPT, my_keypair, new Date() );
            if(my_pgp_keypair==null)System.out.println("NOOOOOPE");
        } catch (NoSuchAlgorithmException | NoSuchProviderException  e) 
        {System.out.println("NoSuchAlgorithmException | NoSuchProviderException | \n" +
"                InvalidAlgorithmParameterException | InvalidParameterSpecException");} catch (PGPException ex) {
            Logger.getLogger(ElGamal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
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
    
    public void export_keypair() {
        try (FileOutputStream stream = new FileOutputStream("../eg_keys.asc"); 
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(stream))) {
            if(my_pgp_keypair==null)System.out.println("NO PGP NOOOO");
            byte[] data = my_pgp_keypair.getPrivateKey().getPrivateKeyDataPacket().getEncoded();
            System.out.println("priv:" + Base64.getEncoder().encodeToString(data));
            bw.write(Base64.getEncoder().encodeToString(data));
            bw.newLine();
            data = my_pgp_keypair.getPublicKey().getEncoded();
            System.out.println("priv:" + Base64.getEncoder().encodeToString(data));
            bw.write(Base64.getEncoder().encodeToString(data));
        } catch (IOException ex) {
            System.out.println("IOException");
            Logger.getLogger(ElGamal.class.getName()).log(Level.SEVERE, null, ex);
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
}
