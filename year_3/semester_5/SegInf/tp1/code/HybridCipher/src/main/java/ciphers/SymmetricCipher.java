package ciphers;

import utils.Base64Stream;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class SymmetricCipher {

    private final String algorithm, mode;
    private String padding = "NoPadding";

    private SecretKey key;

    public SymmetricCipher(String algorithm, String mode, String padding) {
        this.algorithm = algorithm;
        this.mode = mode;
        this.padding = padding;
    }

    public SymmetricCipher(String algorithm, String mode) {
        this.algorithm = algorithm;
        this.mode = mode;
    }

    public void cipherFile(File inputFile, File outputFile, byte[] IV)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException,
            IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        KeyGenerator gen = KeyGenerator.getInstance(algorithm);
        key = gen.generateKey();

        cipherFile(inputFile, outputFile, key, IV);
    }

    // TODO: Remove repeated code?
    public void cipherFile(File inputFile, File outputFile, SecretKey key, byte[] IV)
            throws IllegalBlockSizeException, BadPaddingException, IOException, InvalidAlgorithmParameterException,
            InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance(algorithm+"/"+mode+"/"+padding);
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, IV));

        FileInputStream inputStream = new FileInputStream(inputFile);
        Base64Stream outputStream = new Base64Stream(outputFile);
        byte[] buffer = new byte[cipher.getBlockSize()];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            outputStream.write(cipher.update(buffer, 0, read));
        }
        outputStream.write(cipher.doFinal());
        outputStream.close();
        inputStream.close();
    }

    public void decipherFile(File inputFile, File outputFile, SecretKey key, byte[] IV)
            throws IllegalBlockSizeException, BadPaddingException, IOException, InvalidAlgorithmParameterException,
            InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance(algorithm + "/" + mode + "/" + padding);
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, IV));

        Base64Stream inputStream = new Base64Stream(inputFile);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        byte[] buffer = new byte[cipher.getBlockSize()];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            outputStream.write(cipher.update(buffer, 0, read));
        }
        outputStream.write(cipher.doFinal());
        outputStream.close();
        inputStream.close();
    }

    public SecretKey getKey() {
        return key;
    }
}
