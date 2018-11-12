package org.pac4j.saml.client;

import org.junit.Test;
import org.pac4j.saml.config.SAML2Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.io.File;

import static org.junit.Assert.*;

/**
 * This is {@link SAML2ClientConfigurationTests}.
 *
 * @author Misagh Moayyed
 */
public class SAML2ClientConfigurationTests {
    @Test
    public void verifySigningCertExported() {
        final SAML2Configuration configuration = new SAML2Configuration();
        configuration.setForceKeystoreGeneration(true);
        configuration.setKeystorePath("target/keystore.jks");
        configuration.setKeystorePassword("pac4j");
        configuration.setPrivateKeyPassword("pac4j");
        configuration.setServiceProviderMetadataResource(new FileSystemResource("target/out.xml"));
        configuration.setIdentityProviderMetadataResource(new ClassPathResource("idp-metadata.xml"));
        configuration.init();
        final File signingCertPem = new File("target/saml-signing-cert.pem");
        assertTrue(signingCertPem.exists());
        final File signingCert = new File("target/saml-signing-cert.crt");
        assertTrue(signingCert.exists());
    }
}
