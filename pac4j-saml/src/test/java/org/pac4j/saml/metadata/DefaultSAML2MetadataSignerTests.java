package org.pac4j.saml.metadata;

import org.junit.Test;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.util.DefaultConfigurationManager;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * This is {@link DefaultSAML2MetadataSignerTests}.
 *
 * @author Misagh Moayyed
 * @since 5.4.4
 */
public class DefaultSAML2MetadataSignerTests {
    @Test
    public void verifySigningWithConfigurationDefaults() throws Exception {
        final var configuration = new SAML2Configuration();
        verifyMetadataSigning(configuration);
    }

    @Test
    public void verifySigningWithConfigurationOverride() throws Exception {
        final var configuration = new SAML2Configuration();
        configuration.setSignatureAlgorithms(List.of("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256"));
        configuration.setSignatureReferenceDigestMethods(List.of("http://www.w3.org/2001/04/xmlenc#sha256"));
        verifyMetadataSigning(configuration);
    }

    private static void verifyMetadataSigning(SAML2Configuration configuration) throws Exception {
        configuration.setForceKeystoreGeneration(true);
        configuration.setKeystorePath("target/keystore.jks");
        configuration.setKeystorePassword("pac4j");
        configuration.setPrivateKeyPassword("pac4j");
        configuration.setSignMetadata(true);
        configuration.setServiceProviderMetadataResource(new FileSystemResource("target/out.xml"));
        configuration.setIdentityProviderMetadataResource(new ClassPathResource("idp-metadata.xml"));
        configuration.setMetadataSigner(new DefaultSAML2MetadataSigner(configuration));
        configuration.init();

        final var mgr = new DefaultConfigurationManager();
        mgr.configure();
        final var metadataGenerator = configuration.toMetadataGenerator();
        final var entity = metadataGenerator.buildEntityDescriptor();
        assertNotNull(entity);
        final var metadata = metadataGenerator.getMetadata(entity);
        assertNotNull(metadata);

        metadataGenerator.storeMetadata(metadata, true);
        assertNotNull(metadataGenerator.buildMetadataResolver());
    }
}
