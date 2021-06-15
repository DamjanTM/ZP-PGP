package etf.openpgp.pd170312duu170714d;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPLiteralDataGenerator;
import org.bouncycastle.openpgp.PGPMarker;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPOnePassSignature;
import org.bouncycastle.openpgp.PGPOnePassSignatureList;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureGenerator;
import org.bouncycastle.openpgp.PGPSignatureList;
import org.bouncycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentVerifierBuilderProvider;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyDataDecryptorFactoryBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;
import org.bouncycastle.util.io.Streams;

public class PGP {
    
    public static class PgpMessage
    {
        public byte[] encryptedMessage = null;
        public byte[] decryptedMessage = null;
        public long senderSecretKeyId = 0;
        public long receiverPublicKeyId = 0;
        public String encryptionAlgorithm = "";
        public boolean isEncrypted = false;
        public boolean isSigned = false;
        public boolean isCompressed = false;
        public boolean isRadix64Encoded = false;
        public boolean isIntegrityVerified = false;
        public boolean isSignatureVerified = false;
    }
        private static class PgpDecryptionState
    {
        PGPEncryptedDataList encryptedDataList = null;
        Object pgpObject = null;
        Object currentMessage = null;
        PGPObjectFactory pgpObjectFactory = null;
        PGPPublicKeyEncryptedData publicKeyEncryptedData = null;
        PGPOnePassSignature onePassSignature = null;
        PGPPublicKey signerPublicKey = null;
    }
    
        public static void readPgpMessage( PgpMessage pgpMessage ) throws Exception
    {
        InputStream inputStream = new ByteArrayInputStream( pgpMessage.encryptedMessage );
        inputStream = PGPUtil.getDecoderStream( new BufferedInputStream( inputStream ) );

        PgpDecryptionState pds = new PgpDecryptionState();
        checkIfEncrypted( inputStream, pgpMessage, pds );

        // If the message is not encrpyted, decoode it to extract all the data
        // without a passphrase
        if( !pgpMessage.isEncrypted )
        {
            pgpMessage.decryptedMessage = pgpMessage.encryptedMessage;
            decryptPgpMessage( null, pgpMessage );
        }
        // If the message is encrypted, get the `To` information so that user
        // know which passphrase to enter
        else
        {
            getPublicKeyId( pgpMessage, pds );
        }
    }
    
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
    private static void checkIfEncrypted(
            InputStream inputStream,
            PgpMessage pgpMessage,
            PgpDecryptionState pgpDecryptionState ) throws IOException
    {
        PGPObjectFactory pgpObjectFactory = new PGPObjectFactory( inputStream, new BcKeyFingerprintCalculator() );
        pgpDecryptionState.pgpObject = pgpObjectFactory.nextObject();

        // Determine if the message is encrypted
        pgpMessage.isEncrypted = false;
        if( pgpDecryptionState.pgpObject instanceof PGPEncryptedDataList )
        {
            pgpDecryptionState.encryptedDataList = ( PGPEncryptedDataList )pgpDecryptionState.pgpObject;
            pgpMessage.isEncrypted = true;
        }
        else if( pgpDecryptionState.pgpObject instanceof PGPMarker )
        {
            pgpDecryptionState.pgpObject = pgpObjectFactory.nextObject();
            if( pgpDecryptionState.pgpObject instanceof PGPEncryptedDataList )
            {
                pgpDecryptionState.encryptedDataList = ( PGPEncryptedDataList )pgpDecryptionState.pgpObject;
                pgpMessage.isEncrypted = true;
            }
        }
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

    public static enum EncryptionAlgorithm
    {
        ELGAMAL_3DES( PGPEncryptedData.TRIPLE_DES ),
        ELGAMAL_IDEA( PGPEncryptedData.IDEA ),
        NONE( PGPEncryptedData.NULL );

        public final int id;

        private EncryptionAlgorithm( int id )
        {
            this.id = id;
        }
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

    public static byte[] decryptFile(String msg_, SecretKeyChain key_, char[] pass_) throws IllegalArgumentException {
    InputStream in = null;
    try {
        in = PGPUtil.getDecoderStream(new BufferedInputStream(new FileInputStream(msg_)));
        try {
            JcaPGPObjectFactory pgpF = new JcaPGPObjectFactory(in);
            PGPEncryptedDataList enc = null;

            Object o = pgpF.nextObject();

            //
            // the first object might be a PGP marker packet.
            //
            if (o instanceof PGPEncryptedDataList) {
                enc = (PGPEncryptedDataList) o;
            } else if (o instanceof PGPOnePassSignatureList) {
                verifyFile((PGPOnePassSignatureList) o, pgpF, key_.getSecretKeysCollection());
                in.close();
                in = new BufferedInputStream(new FileInputStream(msg_.substring(0, msg_.length()-5)));
                in = PGPUtil.getDecoderStream(in);
                pgpF = new JcaPGPObjectFactory(in);
                o = pgpF.nextObject();

                if (o instanceof PGPEncryptedDataList) {
                    enc = (PGPEncryptedDataList) o;
                }
            }
            while (enc == null) {
                o = pgpF.nextObject();
                if (o instanceof PGPEncryptedDataList) {
                    enc = (PGPEncryptedDataList) o;
                }
            }

            //
            // find the secret key
            //
            PBESecretKeyDecryptor pbeskd = new BcPBESecretKeyDecryptorBuilder(
                    new BcPGPDigestCalculatorProvider()).build(pass_);

            Iterator it = enc.getEncryptedDataObjects();
            PGPPrivateKey sKey = null;
            PGPPublicKeyEncryptedData pbe = null;

            while (sKey == null && it.hasNext()) {
                pbe = (PGPPublicKeyEncryptedData) it.next();

                PGPSecretKey sk = key_.getSecretKeysCollection().getSecretKey(pbe.getKeyID());
                if (sk != null) {
                    sKey = sk.extractPrivateKey(pbeskd);
                }
            }

            if (sKey == null) {
                throw new IllegalArgumentException("Missing key.");
            }

            InputStream clear = pbe.getDataStream(new JcePublicKeyDataDecryptorFactoryBuilder().setProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()).build(sKey));

            JcaPGPObjectFactory plainFact = new JcaPGPObjectFactory(clear);

            Object message = plainFact.nextObject();

            if (message instanceof PGPCompressedData) {
                PGPCompressedData cData = (PGPCompressedData) message;
                JcaPGPObjectFactory pgpFact = new JcaPGPObjectFactory(cData.getDataStream());

                message = pgpFact.nextObject();
            }

            if (message instanceof PGPLiteralData) {
                PGPLiteralData ld = (PGPLiteralData) message;

                InputStream unc = ld.getInputStream();
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                
                Streams.pipeAll(unc, outStream);
                
                in.close();
                return outStream.toByteArray();
            } else {
                throw new IOException("Couldn't read message.");
            }
        } catch (PGPException ex) {
            Logger.getLogger(PGP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }   catch (FileNotFoundException ex) {
            Logger.getLogger(PGP.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PGP.class.getName()).log(Level.SEVERE, null, ex);
        }
    finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(PGP.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
}

    public static void verifyFile(
            PGPOnePassSignatureList sigList_,
            JcaPGPObjectFactory factory_,
            PGPSecretKeyRingCollection key_) throws IOException, PGPException
            {
        PGPOnePassSignature ops = sigList_.get(0);

        PGPLiteralData p2 = (PGPLiteralData) factory_.nextObject();

        InputStream dIn = p2.getInputStream();
        int ch;

        PGPPublicKey key = key_.getSecretKey(ops.getKeyID()).getPublicKey();
        FileOutputStream out = new FileOutputStream(p2.getFileName());

        ops.init(new JcaPGPContentVerifierBuilderProvider().setProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()), key);

        while ((ch = dIn.read()) >= 0) {
            ops.update((byte) ch);
            out.write(ch);
        }

        out.close();

        PGPSignatureList p3 = (PGPSignatureList) factory_.nextObject();

        if (ops.verify(p3.get(0))) {
            System.out.println("signature verified.");
        } else {
            System.out.println("signature verification failed.");
        }
    }

}
