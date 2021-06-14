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
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;

/**
 *
 * @author Uros
 */
public class SecretKeyChain {
    private PGPSecretKeyRingCollection secretKeyRingCollection;
    
    public SecretKeyChain(){
        try {
            Utils.touch_file(new File("./secret_keychain.asc"));
            secretKeyRingCollection = new PGPSecretKeyRingCollection(
                    new ArmoredInputStream(
                            new FileInputStream( new File( "./secret_keychain.asc" ) ) ),
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
            PGPSecretKeyRingCollection pgpSecKeyCol = new PGPSecretKeyRingCollection( ais, new BcKeyFingerprintCalculator() );
            Iterator<PGPSecretKeyRing> keyRingIter = pgpSecKeyCol.getKeyRings();
            while( keyRingIter.hasNext() )
            {
                PGPSecretKeyRing keyRing = keyRingIter.next();
                secretKeyRingCollection = PGPSecretKeyRingCollection.addSecretKeyRing( secretKeyRingCollection, keyRing );
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
    
    public PGPSecretKeyRingCollection getSecretKeysCollection()
    {
        return secretKeyRingCollection;
    }
    
    public void addSecretKey( PGPKeyRingGenerator keyRingGenerator ) {
        PGPSecretKeyRing secretKeyRing = keyRingGenerator.generateSecretKeyRing();
        secretKeyRingCollection = PGPSecretKeyRingCollection.addSecretKeyRing( secretKeyRingCollection, secretKeyRing );
    }
    
     public void removeSecretKey( PGPSecretKeyRing secretKeyRing ) {
        secretKeyRingCollection = PGPSecretKeyRingCollection.removeSecretKeyRing( secretKeyRingCollection, secretKeyRing );
    }
     
    public PGPSecretKeyRing getSecretKeyRing( long keyID )
    {
        Iterator<PGPSecretKeyRing> i = this.secretKeyRingCollection.getKeyRings();
        while( i.hasNext() )
        {
            PGPSecretKeyRing keyRing = i.next();
            Iterator<PGPSecretKey> keyIter = keyRing.getSecretKeys();

            while( keyIter.hasNext() )
            {
                PGPSecretKey key = keyIter.next();
                if( key.getKeyID() == keyID )
                {
                    return keyRing;
                }
            }
        }
        return null;
    }
    
    public static void exportSecretKey( PGPSecretKeyRing publicKeyRing, String path ){
        File file = new File(path);
        try( ArmoredOutputStream aos = new ArmoredOutputStream( new FileOutputStream( file ) ) )
        {
            publicKeyRing.encode( aos );
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SecretKeyChain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SecretKeyChain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void saveKeysToFile(File file) {
        if(file == null)new File("../secret_ring_file.asc");
        try( ArmoredOutputStream aos = new ArmoredOutputStream( new FileOutputStream( file) ) )
        {
            secretKeyRingCollection.encode( aos );
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SecretKeyChain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SecretKeyChain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static boolean isValidPassphrase( PGPSecretKeyRing secretKeyring, int index, char[] password ) throws PGPException
    {
        if( secretKeyring == null || password == null )
            return false;

        Iterator secretKeyIter = secretKeyring.getSecretKeys();
        PGPSecretKey secretKey = null;
        for( int i = 0; i <= index && secretKeyIter.hasNext(); i++ )
            secretKey = ( PGPSecretKey )secretKeyIter.next();

        if( secretKey == null )return false;
        
        secretKey.extractPrivateKey(
                new JcePBESecretKeyDecryptorBuilder()
                        .setProvider( "BC" )
                        .build( password )
        );
        return true;
    }

}
