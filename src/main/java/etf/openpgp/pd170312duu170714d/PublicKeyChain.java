/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.openpgp.pd170312duu170714d;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Security;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyRingGenerator;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator;

/**
 *
 * @author Uros
 */
public class PublicKeyChain {
    private PGPPublicKeyRingCollection publicKeyRingCollection;
    
    public PublicKeyChain(){
        File file = new File("./public_keychain.asc");
        try {
            Utils.touch_file(file);
            
            publicKeyRingCollection = new PGPPublicKeyRingCollection(
                    new ArmoredInputStream(
                            new FileInputStream( "./public_keychain.asc") ),
                    new BcKeyFingerprintCalculator() );
            
        } catch (IOException ex) {
            System.out.println("err01");
            Logger.getLogger(PublicKeyChain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PGPException ex) {
            System.out.println("err02");
            Logger.getLogger(PublicKeyChain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void importPublicKey( String path ) {
         ArmoredInputStream ais = null;
        try {
            File file = new File(path);
            ais = new ArmoredInputStream( new FileInputStream( file ) );
            PGPPublicKeyRingCollection pgpPubKeyCol = new PGPPublicKeyRingCollection( ais, new BcKeyFingerprintCalculator() );

            Iterator<PGPPublicKeyRing> keyRingIter = pgpPubKeyCol.getKeyRings();
            while( keyRingIter.hasNext() )
            {
                PGPPublicKeyRing keyRing = keyRingIter.next();
                publicKeyRingCollection = PGPPublicKeyRingCollection.addPublicKeyRing( publicKeyRingCollection, keyRing );
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PublicKeyChain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PublicKeyChain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PGPException ex) {
            Logger.getLogger(PublicKeyChain.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                ais.close();
            } catch (IOException ex) {
                Logger.getLogger(PublicKeyChain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public PGPPublicKeyRingCollection getPublicKeysCollection()
    {
        return publicKeyRingCollection;
    }
    
    public void addPublicKey( PGPKeyRingGenerator keyRingGenerator ) {
        PGPPublicKeyRing publicKeyRing  = keyRingGenerator.generatePublicKeyRing();
        publicKeyRingCollection = PGPPublicKeyRingCollection.addPublicKeyRing( publicKeyRingCollection, publicKeyRing );
    }
    
     public void removePublicKey( PGPPublicKeyRing publicKeyRing ) {
        publicKeyRingCollection = PGPPublicKeyRingCollection.removePublicKeyRing( publicKeyRingCollection, publicKeyRing );
    }
     
     
     
     public void saveKeysToFile(File file) {
        try{
            if(file == null)file = new File("../public_ring_file.asc");
            Utils.touch_file(file);
            try( ArmoredOutputStream aos = new ArmoredOutputStream( new FileOutputStream( file ) ) )
            {
                publicKeyRingCollection.encode( aos );
            } catch (FileNotFoundException ex) {
                System.out.println("err1");
                Logger.getLogger(PublicKeyChain.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                System.out.println("err2");
                Logger.getLogger(PublicKeyChain.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PublicKeyChain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public PGPPublicKeyRing getPublicKeyRing( long keyID )
    {
        Iterator<PGPPublicKeyRing> i = this.publicKeyRingCollection.getKeyRings();
        while( i.hasNext() )
        {
            PGPPublicKeyRing keyRing = i.next();
            Iterator<PGPPublicKey> keyIter = keyRing.getPublicKeys();

            while( keyIter.hasNext() )
            {
                PGPPublicKey key = keyIter.next();
                if( key.getKeyID() == keyID )
                {
                    return keyRing;
                }
            }
        }
        return null;
    }
    
    public void exportPublicKey( PGPPublicKeyRing publicKeyRing, String path){
        File file = new File(path);
        try( ArmoredOutputStream aos = new ArmoredOutputStream( new FileOutputStream( file ) ) )
        {
            publicKeyRing.encode( aos );
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PublicKeyChain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PublicKeyChain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     
    public static void main(String args[]){
        Security.addProvider(new BouncyCastleProvider());
        
        KeyRingGen krg = new KeyRingGen(1024, 1024, "Uros", "ugrinic.u@gmail.com", "pass1234");
        KeyRingGen kdg = new KeyRingGen(2048, 1024, "Uroske", "ugrinicuu.u@gmail.com", "pass1234");
        PublicKeyChain pkc = new PublicKeyChain();
        SecretKeyChain skc = new SecretKeyChain();
        
        pkc.addPublicKey(krg.get_keyring_generator());
        skc.addSecretKey(krg.get_keyring_generator());
        
        pkc.addPublicKey(kdg.get_keyring_generator());
        skc.addSecretKey(kdg.get_keyring_generator());
        
        skc.saveKeysToFile(new File("../secret_ring_file.asc"));
        pkc.saveKeysToFile(new File("../public_ring_file.asc"));
        
    }

}
