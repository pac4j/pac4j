package org.pac4j.saml.metadata;

import org.junit.Test;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.util.ConfigurationManager;
import org.pac4j.saml.util.DefaultConfigurationManager;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import static org.junit.Assert.assertNotNull;

/**
 * This is {@link XMLSecSAML2MetadataSignerTests}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
public class XMLSecSAML2MetadataSignerTests {
    @Test
    public void verifyGeneration() throws Exception {
        final ConfigurationManager mgr = new DefaultConfigurationManager();
        mgr.configure();

        final var configuration = new SAML2Configuration();
        configuration.setForceKeystoreGeneration(true);
        configuration.setKeystorePath("target/keystore.jks");
        configuration.setKeystorePassword("pac4j");
        configuration.setPrivateKeyPassword("pac4j");
        configuration.setSignMetadata(true);
        configuration.setServiceProviderMetadataResource(new FileSystemResource("target/out.xml"));
        configuration.setIdentityProviderMetadataResource(new ClassPathResource("idp-metadata.xml"));
        configuration.setMetadataSigner(new XMLSecSAML2MetadataSigner(configuration));
        configuration.init();


        final var metadataGenerator = configuration.toMetadataGenerator();
        final var entity = metadataGenerator.buildEntityDescriptor();
        assertNotNull(entity);
        final var metadata = metadataGenerator.getMetadata(entity);
        assertNotNull(metadata);

        metadataGenerator.storeMetadata(metadata, configuration.getServiceProviderMetadataResource(), true);
        assertNotNull(metadataGenerator.buildMetadataResolver(configuration.getServiceProviderMetadataResource()));
    }
}
