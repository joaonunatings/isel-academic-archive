package ciphers;

import utils.Base64Stream;
import utils.CertificateUtils;

import javax.crypto.*;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

public class AsymmetricCipher {

    private final String algorithm;

    public AsymmetricCipher(String algorithm) {
        this.algorithm = algorithm;
    }

    public void cipherKey(SecretKey privateKey, X509Certificate certificate, File outputFile)
            throws IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException,
            IOException, BadPaddingException {
        PublicKey publicKey = CertificateUtils.getPublicKey(certificate);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        Base64Stream b64 = new Base64Stream(outputFile);
        cipher.update(privateKey.getEncoded());
        byte[] encryptedKey = cipher.doFinal();
        b64.write(encryptedKey);
        b64.close();
    }

    public byte[] decipherKey(PrivateKey certificatePrivateKey, File keyFile)
            throws IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException,
            IOException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, certificatePrivateKey);
        Base64Stream b64 = new Base64Stream(keyFile);
        int read;
        byte[] buffer = new byte[1024];
        while ((read = b64.read(buffer)) != -1) {
            cipher.update(buffer, 0, read);
        }
        byte[] decryptedKey = cipher.doFinal();
        b64.close();
        return decryptedKey;
    }

    // TODO: Add documentation to each function
}
