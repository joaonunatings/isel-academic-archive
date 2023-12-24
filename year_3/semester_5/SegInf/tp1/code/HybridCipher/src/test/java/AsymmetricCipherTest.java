import ciphers.AsymmetricCipher;
import org.junit.jupiter.api.Test;
import utils.CertificateUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AsymmetricCipherTest {

    @Test
    public void cipher_key_and_decipher()
            throws IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, IOException,
            BadPaddingException, InvalidKeyException {
        AsymmetricCipher cipher = new AsymmetricCipher("RSA");
        String keyStr = "01234567890123456789012345678901234567890123456789";
        String endUserCertPath = "files/certs/end-entities/Alice_1.cer";
        String privateKeyPath = "files/certs/pfx/Alice_1.pfx";
        String privateKeyPassword = "changeit";
        String outputFileCipherPath = "files/output/key.ciphered";

        SecretKey key = new SecretKeySpec(keyStr.getBytes(), "AES");
        X509Certificate certificate = CertificateUtils.loadCertificate(endUserCertPath);
        File outputFileCipher = new File(outputFileCipherPath);

        cipher.cipherKey(key, certificate, outputFileCipher);

        PrivateKey privateKey = CertificateUtils.getPrivateKey(privateKeyPath, privateKeyPassword);

        byte[] decryptedKeyArray = cipher.decipherKey(privateKey, outputFileCipher);
        SecretKey decryptedKey = new SecretKeySpec(decryptedKeyArray, "AES");

        assertEquals(keyStr, new String(decryptedKey.getEncoded()));
        assertEquals(key, decryptedKey);
    }
}
