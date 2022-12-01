package org.pac4j.saml.config;

import lombok.val;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.io.File;

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
}
