import ciphers.AsymmetricCipher;
import ciphers.SymmetricCipher;
import utils.CertificateUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.security.PrivateKey;

public class HybridDecoder {

    public void decode(String cipherFilePath, String keyFilePath, String certificateFilePath, String IV,
                       String certPrivatePassword, String outputFilePath) {
        byte[] keyArray;
        SecretKey key = null;
        try {
            AsymmetricCipher cipher = new AsymmetricCipher("RSA");
            File keyFile = new File(keyFilePath);
            PrivateKey certPrivateKey = CertificateUtils.getPrivateKey(certificateFilePath, certPrivatePassword);
            keyArray = cipher.decipherKey(certPrivateKey, keyFile);
            key = new SecretKeySpec(keyArray, "AES");
        } catch (Exception e) {
            System.err.println("Error while trying to decipher key file " + keyFilePath + "\nReason:" + e.getMessage());
            System.exit(-1);
        }

        try {
            SymmetricCipher symmetricCipher = new SymmetricCipher("AES", "GCM", "PKCS5Padding");
            File cipherFile = new File(cipherFilePath);
            File outputFile = new File(outputFilePath);
            symmetricCipher.decipherFile(cipherFile, outputFile, key, IV.getBytes());
        } catch (Exception e) {
            System.err.println("Error while trying to decipher file " + cipherFilePath + "\nReason:" + e.getMessage());
            System.exit(-1);
        }
    }

    public void decode(String cipherFilePath, String keyFilePath, String IV, String certificateFilePath, String certPrivatePassword) {
        decode(cipherFilePath, keyFilePath, IV, certificateFilePath, certPrivatePassword,  cipherFilePath + ".dec");
    }
}