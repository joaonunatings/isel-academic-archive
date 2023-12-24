import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.util.*;

public class KeyGuess {
    static String PLAINTEXT = "255044462d312e350a25d0d4c5d80a34";
    static String CIPHERTEXT = "d06bf9d0dab8e8ef880660d2af65aa82";
    static String IV = "09080706050403020100A2B2C2D2E2F2";

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java keyGuess <keys_filename>");
            System.exit(-1);
        }
        String filename = args[0];

        System.out.println("Plaintext: " + PLAINTEXT);
        System.out.println("Ciphertext: " + CIPHERTEXT);
        System.out.println("IV: " + IV);

        byte[] ciphertext_bytes = hexStringToByteArray(CIPHERTEXT);
        byte[] iv_bytes = hexStringToByteArray(IV);

        String[] keys = readKeys(filename);

        // Print keys from array readKeys()
        for (int i = 0; i < keys.length; i++) {
            byte[] key_bytes = hexStringToByteArray(keys[i]);
            String key = decryptAES(key_bytes, iv_bytes, ciphertext_bytes);
        }
    }

    // Function that decwiphers AES-128-CBC with the given key and IV using JCA
    public static String decryptAES(byte[] key, byte[] iv, byte[] ciphertext) {
        try {
            // Create cipher
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            // Initialize cipher with key and IV

            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
            // Decrypt ciphertext
            byte[] decrypted = cipher.doFinal(ciphertext);
            byte[] plaintext_bytes = hexStringToByteArray(PLAINTEXT);
            if (Arrays.equals(decrypted, plaintext_bytes)) {
                System.out.println("\nKey found: " + byteArrayToHexString(key));
                System.exit(0);
            }
            // Convert decrypted bytes to string
            return new String(decrypted);
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return "";
        }
    }

    // Convert hex string to byte array
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    // Function that converts byte array to hex string
    public static String byteArrayToHexString(byte[] b) {
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result +=
                    Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    // Method readKeys that reads keys from a file and returns a list of keys
    public static String[] readKeys(String filename) {
        List<String> keys = new ArrayList();
        try {
            // Create file object
            File file = new File(filename);
            // Create scanner object
            Scanner sc = new Scanner(file);
            // Read keys from file
            while(sc.hasNextLine()) {
                keys.add(sc.nextLine());
            }

            // Close scanner
            sc.close();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return keys.toArray(new String[keys.size()]);
    }
}
