package utils;

import javax.crypto.Mac;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CertificateUtils {

    private static final String[] DEFAULT_EXTENSIONS = new String[]{"cer"};

    public static List<X509Certificate> loadCertificates(String path, String[] extensions) {
        List<X509Certificate> result = new ArrayList<>(0);
        try (Stream<Path> paths = Files.walk(Paths.get(path))) {
            result = paths
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .filter(p -> extensions.length == 0 || Stream.of(extensions).anyMatch(p::endsWith))
                    .map(CertificateUtils::loadCertificate)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Could not load certificates from: " + path + "\nReason: " + e.getMessage() + "\n");
        }
        return result;
    }

    public static List<X509Certificate> loadCertificates(String path) {
        return loadCertificates(path, DEFAULT_EXTENSIONS);
    }
    
    public static X509Certificate loadCertificate(String path) {
        CertificateFactory certificateFactory;
        try {
            certificateFactory = CertificateFactory.getInstance("X.509");
            return (X509Certificate) certificateFactory.generateCertificate(new FileInputStream(path));
        } catch (CertificateException | FileNotFoundException e) {
            System.err.println("Could not load certificate: " + path + "\nReason: " + e.getMessage() + "\n");
            return null;
        }
    }

    public static List<X509Certificate> loadTrustedCertificates(String path, String password) {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream(path), password.toCharArray());

            List<X509Certificate> result = new ArrayList<>(keyStore.size());
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                if (keyStore.isCertificateEntry(alias)) {
                    result.add((X509Certificate)keyStore.getCertificate(alias));
                }
            }
            return result;
        } catch (Exception e) {
            System.err.println("Could not load trusted certificates from: " + path + "\nReason: " + e.getMessage() + "\n");
            return new ArrayList<>(0);
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean isValidCertificate(X509Certificate certificate, CertStore store) {
        X509CertSelector selector = new X509CertSelector();
        selector.setCertificateValid(new Date());
        selector.setSubject(certificate.getIssuerX500Principal());
        try {
            Collection<X509Certificate> certs = (Collection<X509Certificate>) store.getCertificates(selector);
            if (certs.size() == 0) throw new CertificateException("No certificate found");
            for (X509Certificate certFromStore : certs) {
                if (certFromStore.getIssuerX500Principal().equals(certFromStore.getSubjectX500Principal())) {
                    certFromStore.verify(certFromStore.getPublicKey());
                } else {
                    certificate.verify(certFromStore.getPublicKey());
                    return isValidCertificate(certFromStore, store);
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static CertStore createCertStore(List<X509Certificate> certificates) {
        try {
            CollectionCertStoreParameters parameters = new CollectionCertStoreParameters(certificates);
            return CertStore.getInstance("Collection", parameters);
        } catch (Exception e) {
            System.err.println("Could not create CertStore\nReason: " + e.getMessage() + "\n");
            return null;
        }
    }

    public static PublicKey getPublicKey(X509Certificate certificate) {
        return certificate.getPublicKey();
    }

    public static PublicKey getPublicKey(String certificatePath) {
        X509Certificate certificate = loadCertificate(certificatePath);
        return (certificate == null) ? null : getPublicKey(certificate);
    }

    public static PrivateKey getPrivateKey(String certificatePath, String password) {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(new FileInputStream(certificatePath), password.toCharArray());
            Enumeration<String> entries = keyStore.aliases();
            String alias = entries.nextElement();
            return (PrivateKey) keyStore.getKey(alias, password.toCharArray());
        } catch (Exception e) {
            System.err.println("Could not load private key\nReason: " + e.getMessage() + "\n");
            System.exit(-1);
        }
        return null;
    }


}
