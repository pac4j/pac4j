package org.pac4j.saml.metadata;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.crypto.KeyStoreCredentialProvider;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import static org.junit.Assert.*;

public class SAML2ServiceProviderMetadataResolverTest {

    private SAML2ServiceProviderMetadataResolver metadataResolver;

    private SAML2Configuration configuration;

    @Before
    public void setUp() {
        configuration = new SAML2Configuration();
        configuration.setKeystorePath("target/keystore.jks");
        configuration.setKeystorePassword("pac4j");
        configuration.setPrivateKeyPassword("pac4j");
        configuration.setSignMetadata(true);
        configuration.setForceKeystoreGeneration(true);
        configuration.setForceServiceProviderMetadataGeneration(true);
        configuration.setServiceProviderMetadataResource(new FileSystemResource("target/out.xml"));
        configuration.setIdentityProviderMetadataResource(new ClassPathResource("idp-metadata.xml"));
        configuration.getRequestedServiceProviderAttributes().add(
            new SAML2ServiceProvicerRequestedAttribute("urn:oid:1.3.6.1.4.1.5923.1.1.1.6", "eduPersonPrincipalName"));
        configuration.init();
    }

    @Test
    public void resolveServiceProviderMetadata() {
        metadataResolver = new SAML2ServiceProviderMetadataResolver(configuration, "http://localhost",
            new KeyStoreCredentialProvider(configuration));
        //assertTrue(configuration.getServiceProviderMetadataResource().exists());
        assertNotNull(metadataResolver.resolve());
    }
}
