import org.junit.jupiter.api.Test;
import utils.CertificateUtils;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertStore;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CertificateUtilsTest {

    private static final String CA_PATH = "files/certs/trust-anchors/";
    private static final String CA_INT_PATH = "files/certs/cert-int/";
    private static final String USER_CERT_PATH = "files/certs/end-entities/";
    private static final String CERT_PASSWORD = "changeit";

    @Test
    public void load_certificate() {
        X509Certificate cert = CertificateUtils.loadCertificate(USER_CERT_PATH + "Alice_1.cer");
        assertNotNull(cert);
    }

    @Test
    public void load_non_existent_certificate() {
        X509Certificate cert = CertificateUtils.loadCertificate(USER_CERT_PATH + "non-existent.cer");
        assertNull(cert);
    }

    @Test
    public void load_certificates_from_folder() {
        List<X509Certificate> certsList = CertificateUtils.loadCertificates(CA_INT_PATH, new String[]{"cer"});
        assertEquals(2, certsList.size());
    }

    @Test
    public void load_certificates_from_non_existent_folder() {
        List<X509Certificate> certsList = CertificateUtils.loadCertificates("non-existent-folder", new String[]{"cer"});
        assertEquals(0, certsList.size());
    }

    @Test
    public void load_trust_anchors_certificates() {
        List<X509Certificate> certsList = CertificateUtils.loadTrustedCertificates(CA_PATH + "trust.jks", CERT_PASSWORD);
        assertEquals(2, certsList.size());
    }

    @Test
    public void validate_certificate() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        X509Certificate cert = CertificateUtils.loadCertificate(USER_CERT_PATH + "Alice_1.cer");

        List<X509Certificate> caTrustedCerts = CertificateUtils.loadTrustedCertificates(CA_PATH + "trust.jks", CERT_PASSWORD);
        List<X509Certificate> caIntCerts = CertificateUtils.loadCertificates(CA_INT_PATH);

        List<X509Certificate> allCerts = new ArrayList<>(caTrustedCerts);
        allCerts.addAll(caIntCerts);

        CertStore certStore = CertStore.getInstance("Collection", new CollectionCertStoreParameters(allCerts));

        assert cert != null;
        assertTrue(CertificateUtils.isValidCertificate(cert, certStore));
    }

    @Test
    public void validate_certificate_failure() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        X509Certificate cert = CertificateUtils.loadCertificate(USER_CERT_PATH + "Alice_2.cer");

        List<X509Certificate> caTrustedCerts = CertificateUtils.loadTrustedCertificates(CA_PATH + "trust.jks", CERT_PASSWORD);
        X509Certificate caIntCert = CertificateUtils.loadCertificate(CA_INT_PATH + "CA1-int.cer");

        List<X509Certificate> allCerts = new ArrayList<>(caTrustedCerts);
        allCerts.add(caIntCert);

        CertStore certStore = CertStore.getInstance("Collection", new CollectionCertStoreParameters(allCerts));

        assert cert != null;
        assertFalse(CertificateUtils.isValidCertificate(cert, certStore));
    }
}
