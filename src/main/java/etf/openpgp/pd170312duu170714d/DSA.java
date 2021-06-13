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
    private KeyPair my_keypair;
    protected PGPKeyPair my_pgp_keypair;
    
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
    
    public void generate_keypair(int key_size_) {
    try {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA");
        keyPairGenerator.initialize(key_size_);
        this.my_keypair = keyPairGenerator.generateKeyPair();
        this.my_pgp_keypair = new JcaPGPKeyPair( PGPPublicKey.DSA, my_keypair, new Date() );
    } catch (NoSuchAlgorithmException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }   catch (PGPException ex) {
            Logger.getLogger(DSA.class.getName()).log(Level.SEVERE, null, ex);
        }
}

    public void export_keypair() {
        try (FileOutputStream stream = new FileOutputStream("../dsa_keys.asc")) {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(stream));
            bw.write(Base64.getEncoder().encodeToString(my_keypair.getPrivate().getEncoded()));
            bw.newLine();
            bw.write(Base64.getEncoder().encodeToString(my_keypair.getPublic().getEncoded()));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

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
}
