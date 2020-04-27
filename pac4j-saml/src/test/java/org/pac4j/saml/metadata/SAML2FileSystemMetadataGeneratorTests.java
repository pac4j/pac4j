package org.pac4j.saml.metadata;

import org.junit.Test;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.util.ConfigurationManager;
import org.pac4j.saml.util.DefaultConfigurationManager;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import static org.junit.Assert.*;

/**
 * This is {@link SAML2FileSystemMetadataGeneratorTests}.
 *
 * @author Misagh Moayyed
 */
public class SAML2FileSystemMetadataGeneratorTests {
    @Test
    public void verifyGeneration() throws Exception {
        final ConfigurationManager mgr = new DefaultConfigurationManager();
        mgr.configure();

        final SAML2Configuration configuration = new SAML2Configuration();
        configuration.setForceKeystoreGeneration(true);
        configuration.setKeystorePath("target/keystore.jks");
        configuration.setKeystorePassword("pac4j");
        configuration.setPrivateKeyPassword("pac4j");
        configuration.setSignMetadata(true);
        configuration.setServiceProviderMetadataResource(new FileSystemResource("target/out.xml"));
        configuration.setIdentityProviderMetadataResource(new ClassPathResource("idp-metadata.xml"));
        configuration.init();

        final SAMLMetadataGenerator metadataGenerator = new SAML2FileSystemMetadataGenerator();
        final EntityDescriptor entity = metadataGenerator.buildEntityDescriptor();
        assertNotNull(entity);
        final String metadata = metadataGenerator.getMetadata(entity);
        assertNotNull(metadata);

        metadataGenerator.storeMetadata(metadata, configuration.getServiceProviderMetadataResource(), true);
        assertNotNull(metadataGenerator.buildMetadataResolver(configuration.getServiceProviderMetadataResource()));
    }

}
