package org.pac4j.saml.config;

import lombok.val;
import org.junit.Test;
import org.pac4j.core.exception.TechnicalException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * This is {@link SAML2ConfigurationTests}.
 *
 * @author Misagh Moayyed
 */
public class SAML2ConfigurationTests {
    @Test
    public void verifySigningCertExported() {
        val configuration = new SAML2Configuration();
        configuration.setForceKeystoreGeneration(true);
        configuration.setKeystorePath("target/keystore.jks");
        configuration.setKeystorePassword("pac4j");
        configuration.setPrivateKeyPassword("pac4j");
        configuration.setServiceProviderMetadataResource(new FileSystemResource("target/out.xml"));
        configuration.setIdentityProviderMetadataResource(new ClassPathResource("idp-metadata.xml"));
        configuration.init();
        val signingCertPem = new File("target/saml-signing-cert.pem");
        assertTrue(signingCertPem.exists());
        val signingCert = new File("target/saml-signing-cert.crt");
        assertTrue(signingCert.exists());
        val signingCertKey = new File("target/saml-signing-cert.key");
        assertTrue(signingCertKey.exists());
    }

    @Test
    public void verifySigningCertNamedExported() {
        val certNamePart = "id-09 _*#AD";
        val certNameResult = "id-09_AD";
        val configuration = new SAML2Configuration();
        configuration.setForceKeystoreGeneration(true);
        configuration.setKeystorePath("target/keystore.jks");
        configuration.setCertificateNameToAppend(certNamePart);
        configuration.setKeystorePassword("pac4j");
        configuration.setPrivateKeyPassword("pac4j");
        configuration.setServiceProviderMetadataResource(new FileSystemResource("target/out.xml"));
        configuration.setIdentityProviderMetadataResource(new ClassPathResource("idp-metadata.xml"));
        configuration.init();
        val signingCertPem = new File("target/saml-signing-cert-" + certNameResult + ".pem");
        assertTrue(signingCertPem.exists());
        val signingCert = new File("target/saml-signing-cert-" + certNameResult + ".crt");
        assertTrue(signingCert.exists());
        val signingCertKey = new File("target/saml-signing-cert-" + certNameResult + ".key");
        assertTrue(signingCertKey.exists());
    }

    @Test
    public void verifyIdentityProviderMetadataUrlWithBadSslCert() throws Exception {
        val metadataUrl = "https://expired.badssl.com/";
        val configuration = new SAML2Configuration("target/keystore.jks", "pac4j", "pac4j", metadataUrl);
        configuration.setForceKeystoreGeneration(true);
        configuration.setServiceProviderMetadataResource(new FileSystemResource("target/out.xml"));
        configuration.init();
        var metadataResolver = configuration.getIdentityProviderMetadataResolver().resolve();
        assertNull(metadataResolver);
        configuration.setSslSocketFactory(disabledSslContext().getSocketFactory());
        configuration.setHostnameVerifier((s, sslSession) -> true);
        assertThrows(TechnicalException.class, () -> configuration.getIdentityProviderMetadataResolver().resolve());
    }

    @Test
    public void shouldBeAbleToUseAnIdpMetadataResourceWithTheDefaultMetadataResolver() {
        var configuration = new SAML2Configuration();
        configuration.setKeystorePath("target/keystore.jks");
        configuration.setKeystorePassword("pac4j");
        configuration.setPrivateKeyPassword("pac4j");

        configuration.setIdentityProviderMetadataResource(new ClassPathResource("idp-metadata.xml"));
        configuration.init();

        var idpMetadataResolver = configuration.getIdentityProviderMetadataResolver();
        idpMetadataResolver.resolve();

        var result = idpMetadataResolver.getMetadata();
        assertNotNull(result);
    }

    private static SSLContext disabledSslContext() throws Exception {
        var trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        }};

        var sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom());
        return sc;
    }
}
