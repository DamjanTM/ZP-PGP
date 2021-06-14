/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.openpgp.pd170312duu170714d;

import java.security.Security;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPKeyRingGenerator;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.operator.PGPDigestCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyEncryptorBuilder;

/**
 *
 * @author Uros
 */
public class KeyRingGen {
    private PGPKeyRingGenerator my_keyring_generator;
    ElGamal eg;
    DSA dsa;
    public KeyRingGen(int dsa_size, int eg_size, String name, String email, String password){
        dsa = new DSA(dsa_size);
        if(eg_size>0)eg = new ElGamal(eg_size);
        try {
            String userID = name + " <" + email + ">";
            PGPDigestCalculator shaCalc = new JcaPGPDigestCalculatorProviderBuilder().build().get( HashAlgorithmTags.SHA1 );
            my_keyring_generator = new PGPKeyRingGenerator(
                PGPSignature.POSITIVE_CERTIFICATION,
                dsa.getMy_pgp_keypair(),
                userID,
                shaCalc,
                null,
                null,
                new JcaPGPContentSignerBuilder( dsa.getMy_pgp_keypair().getPublicKey().getAlgorithm(), HashAlgorithmTags.SHA256 ),
                new JcePBESecretKeyEncryptorBuilder( PGPEncryptedData.AES_256, shaCalc ).setProvider( "BC" ).build( password.toCharArray()  ) );
            if(eg_size > 0)my_keyring_generator.addSubKey( eg.getMy_pgp_keypair() );
        } catch (PGPException ex) {
            Logger.getLogger(KeyRingGen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public PGPKeyRingGenerator get_keyring_generator(){
        return my_keyring_generator;
    }
    
    
}
