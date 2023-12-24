import ciphers.AsymmetricCipher;
import ciphers.SymmetricCipher;
import utils.CertificateUtils;

import java.io.File;
import java.security.cert.CertStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public class HybridEncoder {

    private static final String CA_INT_PATH = "files/certs/cert-int";

    private final List<X509Certificate> certsList;

    public HybridEncoder(String trustAnchorsPath, String password) {
        List<X509Certificate> trustedCerts = CertificateUtils.loadTrustedCertificates(trustAnchorsPath, password);
        certsList = new ArrayList<>(trustedCerts.size());
        certsList.addAll(trustedCerts);
    }

    public void encode(String dataFilePath, String certificateFilePath, String IV, String cipherFilePath, String keyFilePath) {

        List<X509Certificate> caIntCerts = CertificateUtils.loadCertificates(CA_INT_PATH);
        certsList.addAll(caIntCerts);
        X509Certificate endCert = CertificateUtils.loadCertificate(certificateFilePath);
        CertStore certStore = CertificateUtils.createCertStore(certsList);

        if (endCert == null) {
            System.err.println("Error: Could not load certificate from file " + certificateFilePath);
            System.exit(-1);
        }

        if (!CertificateUtils.isValidCertificate(endCert, certStore)) {
            System.err.println("Error: Certificate is not valid");
            System.exit(-1);
        }

        SymmetricCipher symmetricCipher = new SymmetricCipher("AES", "GCM", "PKCS5Padding");
        try {
            File dataFile = new File(dataFilePath);
            File cipherFile = new File(cipherFilePath);
            symmetricCipher.cipherFile(dataFile, cipherFile, IV.getBytes());
        } catch (Exception e) {
            System.err.println("Error while trying to cipher file " + dataFilePath + "\nReason: " + e.getMessage());
            System.exit(-1);
        }

        try {
            AsymmetricCipher asymmetricCipher = new AsymmetricCipher("RSA");
            File keyFile = new File(keyFilePath);
            asymmetricCipher.cipherKey(symmetricCipher.getKey(), endCert, keyFile);
        } catch (Exception e) {
            System.err.println("Error while trying to cipher key\nReason: " + e.getMessage());
            System.exit(-1);
        }
    }

    public void encode(String dataFilePath, String certificateFilePath, String IV) {
        encode(dataFilePath, certificateFilePath, IV, dataFilePath + ".enc", dataFilePath + ".key");
    }
}
