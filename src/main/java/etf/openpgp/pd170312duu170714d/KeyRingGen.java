/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.openpgp.pd170312duu170714d;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPException;
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
    public KeyRingGen(ElGamal eg_, DSA dsa_, String name, String email, String password){
        try {
            String userID = name + " <" + email + ">";
            PGPDigestCalculator shaCalc = new JcaPGPDigestCalculatorProviderBuilder().build().get( HashAlgorithmTags.SHA1 );
            my_keyring_generator = new PGPKeyRingGenerator(
                PGPSignature.POSITIVE_CERTIFICATION,
                dsa_.getMy_pgp_keypair(),
                userID,
                shaCalc,
                null,
                null,
                new JcaPGPContentSignerBuilder( dsa_.getMy_pgp_keypair().getPublicKey().getAlgorithm(), HashAlgorithmTags.SHA256 ),
                new JcePBESecretKeyEncryptorBuilder( PGPEncryptedData.AES_256, shaCalc ).setProvider( "BC" ).build( password.toCharArray()  ) );
        } catch (PGPException ex) {
            Logger.getLogger(KeyRingGen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public PGPKeyRingGenerator get_keyring_generator(){
        return my_keyring_generator;
    }
}
