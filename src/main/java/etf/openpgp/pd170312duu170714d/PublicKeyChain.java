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
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyRingGenerator;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator;

/**
 *
 * @author Uros
 */
public class PublicKeyChain {
    private PGPPublicKeyRingCollection publicKeyRingCollection;
    
    public PublicKeyChain(){
        try {
            publicKeyRingCollection = new PGPPublicKeyRingCollection(
                    new ArmoredInputStream(
                            new FileInputStream( "./public_keychain.asc") ),
                    new BcKeyFingerprintCalculator() );
        } catch (IOException ex) {
            Logger.getLogger(SecretKeyChain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PGPException ex) {
            Logger.getLogger(SecretKeyChain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void importSecretKey( String path ) {
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
            Logger.getLogger(SecretKeyChain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SecretKeyChain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PGPException ex) {
            Logger.getLogger(SecretKeyChain.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                ais.close();
            } catch (IOException ex) {
                Logger.getLogger(SecretKeyChain.class.getName()).log(Level.SEVERE, null, ex);
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
        try( ArmoredOutputStream aos = new ArmoredOutputStream( new FileOutputStream( file ) ) )
        {
            publicKeyRingCollection.encode( aos );
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PublicKeyChain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PublicKeyChain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
