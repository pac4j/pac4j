package org.pac4j.saml.metadata;

import lombok.val;
import org.junit.Test;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.util.ConfigurationManager;
import org.pac4j.saml.util.DefaultConfigurationManager;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import static org.junit.Assert.assertNotNull;

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

        val configuration = new SAML2Configuration();
        configuration.setForceKeystoreGeneration(true);
        configuration.setKeystorePath("target/keystore.jks");
        configuration.setKeystorePassword("pac4j");
        configuration.setPrivateKeyPassword("pac4j");
        configuration.setSignMetadata(true);
        configuration.setServiceProviderMetadataResource(new FileSystemResource("target/out.xml"));
        configuration.setIdentityProviderMetadataResource(new ClassPathResource("idp-metadata.xml"));
        configuration.init();

        SAML2MetadataGenerator metadataGenerator = new SAML2FileSystemMetadataGenerator(configuration.getServiceProviderMetadataResource());
        val entity = metadataGenerator.buildEntityDescriptor();
        assertNotNull(entity);
        val metadata = metadataGenerator.getMetadata(entity);
        assertNotNull(metadata);

        metadataGenerator.storeMetadata(metadata, true);
        assertNotNull(metadataGenerator.buildMetadataResolver());
    }

}
