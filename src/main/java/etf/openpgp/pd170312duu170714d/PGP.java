package etf.openpgp.pd170312duu170714d;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
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
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;

public class PGP {
    public static byte[] convertToPGP(byte[] msg_) throws Exception {
        try {
            OutputStream packetStream;
            try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
                PGPLiteralDataGenerator ldg = new PGPLiteralDataGenerator();
                packetStream = ldg.open(outStream, PGPLiteralData.BINARY, "message", new Date(), new byte[65536]);
                packetStream.write( msg_ );
                msg_ = outStream.toByteArray();
            }
            packetStream.close();

            return msg_;
        } catch (IOException ex) {
            Logger.getLogger(PGP.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw(new Exception("Couldn't create literal data packet!"));
    }

    public static byte[] sign(byte[] msg_, PGPSecretKey senderSecretKey_, char[] senderPassphrase_) throws Exception {
        try {
            PGPPrivateKey senderPrivateKey = senderSecretKey_.extractPrivateKey(
                    new JcePBESecretKeyDecryptorBuilder().setProvider( "BC" ).build( senderPassphrase_ ));

            PGPSignatureSubpacketGenerator ssg = new PGPSignatureSubpacketGenerator();
            ssg.setSignatureCreationTime(false, new Date());
            ssg.setSignerUserID(false, senderSecretKey_.getPublicKey().getUserIDs().next());
            ssg.setPreferredHashAlgorithms(false, new int[] { HashAlgorithmTags.SHA256 });
            ssg.setPreferredSymmetricAlgorithms(false, new int[] { PGPEncryptedData.TRIPLE_DES, PGPEncryptedData.IDEA });
            ssg.setPreferredCompressionAlgorithms(false, new int[] { PGPCompressedData.ZIP });
            
            PGPSignatureGenerator signGen = new PGPSignatureGenerator(
                    new JcaPGPContentSignerBuilder(
                            senderSecretKey_.getPublicKey().getAlgorithm(),
                            HashAlgorithmTags.SHA256
                    ).setProvider("BC")
            );
            signGen.init( PGPSignature.BINARY_DOCUMENT, senderPrivateKey );
            signGen.setHashedSubpackets(ssg.generate());

            PGPOnePassSignature signatureHeader = signGen.generateOnePassVersion(false );

            signGen.update( msg_ );

            PGPSignature signature = signGen.generate();

            try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
                signatureHeader.encode( outStream );
                outStream.write( msg_ );
                signature.encode( outStream );
                msg_ = outStream.toByteArray();
            }

            return msg_;
        } catch (PGPException | IOException ex) {
            Logger.getLogger(PGP.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new Exception( "Could not sign message." );
    }

    public static byte[] zip(byte[] msg_) throws Exception {
        try {
            OutputStream packetStream;
            try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
                PGPCompressedDataGenerator cdg = new PGPCompressedDataGenerator( PGPCompressedData.ZIP );
                packetStream = cdg.open( outStream );
                packetStream.write( msg_ );
                msg_ = outStream.toByteArray();
            }
            packetStream.close();

            return msg_;
        } catch (IOException ex) {
            Logger.getLogger(PGP.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw(new Exception("Couldn't zip data packet!"));
    }

    public static byte[] encrypt(byte[] msg_, int algorithm_, PGPPublicKey receiverPublicKey) throws Exception {
        try {
            OutputStream packetStream;
            try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
                PGPEncryptedDataGenerator edg = new PGPEncryptedDataGenerator(
                        new JcePGPDataEncryptorBuilder( algorithm_ ).setProvider( "BC" ).setSecureRandom( new SecureRandom() ).setWithIntegrityPacket( true ));
                edg.addMethod(
                        new JcePublicKeyKeyEncryptionMethodGenerator( receiverPublicKey ).setProvider( "BC" ));
                packetStream = edg.open( outStream, new byte[65536]);
                packetStream.write( msg_ );
                msg_ = outStream.toByteArray();
            }
            packetStream.close();

            return msg_;
        } catch (IOException | PGPException ex) {
            Logger.getLogger(PGP.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw(new Exception("Couldn't encrypt data packet!"));
    }

    public static byte[] serialize(byte[] msg_) throws Exception {
        try {
            ArmoredOutputStream packetStream;
            try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
                packetStream = new ArmoredOutputStream( outStream );
                packetStream.write( msg_ );
                msg_ = outStream.toByteArray();
            }
            packetStream.close();

            return msg_;
        } catch (IOException ex) {
            Logger.getLogger(PGP.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw(new Exception("Couldn't serialize data packet!"));
    }

}
