package etf.openpgp.pd170312duu170714d;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPLiteralDataGenerator;
import org.bouncycastle.openpgp.PGPOnePassSignature;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureGenerator;
import org.bouncycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;

public class PGP {
public static byte[] convertToPGP(byte[] msg_) throws Exception {
    try {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        PGPLiteralDataGenerator ldg = new PGPLiteralDataGenerator();
        OutputStream packetStream = ldg.open(outStream, PGPLiteralData.BINARY, "message", new Date(), new byte[65536]);
        
        packetStream.write( msg_ );
        msg_ = outStream.toByteArray();
        
        outStream.close();
        packetStream.close();

        return msg_;
    } catch (IOException ex) {
        Logger.getLogger(PGP.class.getName()).log(Level.SEVERE, null, ex);
    }
    throw(new Exception("Couldn't create literal data packet!"));
}

public static byte[] sign(byte[] msg_, PGPSecretKey senderSecretKey_, char[] senderPassphrase_) {
    try {
        ByteArrayOutputStream outStream = null;
        
        // get the sender's private key using the given passphrase
        PGPPrivateKey senderPrivateKey = senderSecretKey_.extractPrivateKey(
                new JcePBESecretKeyDecryptorBuilder().setProvider( "BC" ).build( senderPassphrase_ ));
        
        // make a signature generator
        PGPSignatureGenerator signGen = new PGPSignatureGenerator(
                new JcaPGPContentSignerBuilder(
                        senderSecretKey_.getPublicKey().getAlgorithm(),
                        HashAlgorithmTags.SHA256
                ).setProvider( "BC" )
        );
        signGen.init( PGPSignature.BINARY_DOCUMENT, senderPrivateKey );
        
        // make a generator for the signature's header subpackets
        PGPSignatureSubpacketGenerator signatureSubpacketGen = new PGPSignatureSubpacketGenerator();
        signatureSubpacketGen.setSignerUserID(false, senderSecretKey_.getPublicKey().getUserIDs().next() );
        signatureSubpacketGen.setSignatureCreationTime(false, new Date() );
        signatureSubpacketGen.setPreferredHashAlgorithms(false, new int[] { HashAlgorithmTags.SHA256 } );
        signatureSubpacketGen.setPreferredSymmetricAlgorithms(false, new int[]
        {
            PGPEncryptedData.IDEA, PGPEncryptedData.TRIPLE_DES
        } );
        signatureSubpacketGen.setPreferredCompressionAlgorithms(false, new int[]
        {
            PGPCompressedData.ZIP
        } );
        
        // set the hashed subpackets in the signature
        signGen.setHashedSubpackets( signatureSubpacketGen.generate() );
        
        // create a one-pass signature header (parameter header in front of the message used for calculating the message signature in one pass)
        PGPOnePassSignature signatureHeader = signGen.generateOnePassVersion( /*isNested=*/ false );

        // update the message digest by hashing the message body
        signGen.update( msg_ );
        // create a signature by signing the message digest with the sender's private key
        PGPSignature signature = signGen.generate();
        
        outStream = new ByteArrayOutputStream();
        // prepend the signature one-pass header
        signatureHeader.encode( outStream );
        // write the literal data packet
        outStream.write( literalPacket );
        // append the signature packet
        signature.encode( outStream );
        
        // overwrite the message buffer and close the message stream
        msg_ = outStream.toByteArray();
        outStream.close();
        
        return msg_;
        
        
        throw new Exception( "Could not sign message." );
    } catch (PGPException ex) {
        Logger.getLogger(PGP.class.getName()).log(Level.SEVERE, null, ex);
    }
}

public static byte[] zip(byte[] msg_) {
    
}

public static byte[] encrypt(byte[] msg_) {
    
}

public static byte[] serialize(byte[] msg_) {
    
}

}
