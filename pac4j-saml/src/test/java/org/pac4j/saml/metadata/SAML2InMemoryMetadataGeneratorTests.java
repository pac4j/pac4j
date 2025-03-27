package org.pac4j.saml.metadata;

import lombok.val;
import org.junit.Test;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.util.DefaultConfigurationManager;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;

import static org.junit.Assert.assertNotNull;

/**
 * This is {@link SAML2InMemoryMetadataGeneratorTests}.
 *
 * @author Misagh Moayyed
 */
public class SAML2InMemoryMetadataGeneratorTests {
    @Test
    public void verifyGeneration() throws Exception {
        var mgr = new DefaultConfigurationManager();
        mgr.configure();

        val configuration = new SAML2Configuration();
        configuration.setForceKeystoreGeneration(true);
        configuration.setKeystorePath("target/keystore.jks");
        configuration.setKeystorePassword("pac4j");
        configuration.setPrivateKeyPassword("pac4j");
        configuration.setSignMetadata(true);
        configuration.setMetadataGenerator(new SAML2InMemoryMetadataGenerator());
        configuration.setIdentityProviderMetadataResource(new ClassPathResource("idp-metadata.xml"));
        configuration.init();

        var metadataGenerator = configuration.getMetadataGenerator();
        val entity = metadataGenerator.buildEntityDescriptor();
        assertNotNull(entity);
        val metadata = metadataGenerator.getMetadata(entity);
        assertNotNull(metadata);

        metadataGenerator.storeMetadata(metadata, true);
        assertNotNull(metadataGenerator.buildMetadataResolver());
    }

}
