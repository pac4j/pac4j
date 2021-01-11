package org.pac4j.saml.metadata.keystore;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.crypto.CredentialProvider;
import org.pac4j.saml.crypto.KeyStoreCredentialProvider;
import org.pac4j.saml.util.ConfigurationManager;
import org.pac4j.saml.util.DefaultConfigurationManager;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import static org.junit.Assert.*;

/**
 * This is {@link SAML2FileSystemKeystoreGeneratorTests}.
 *
 * @author Misagh Moayyed
 */
public class SAML2FileSystemKeystoreGeneratorTests {

    @Test
    public void verifyKeystoreGenForNewDirectory() throws Exception {
        final ConfigurationManager mgr = new DefaultConfigurationManager();
        mgr.configure();

        final SAML2Configuration configuration = new SAML2Configuration();
        configuration.setForceKeystoreGeneration(true);

        final String path = RandomStringUtils.randomAlphabetic(4);
        configuration.setKeystorePath(String.format("%s/%s/keystore.jks", FileUtils.getTempDirectoryPath(), path));

        configuration.setKeystorePassword("pac4j");
        configuration.setPrivateKeyPassword("pac4j");
        configuration.setServiceProviderMetadataResource(new FileSystemResource("target/out.xml"));
        configuration.setIdentityProviderMetadataResource(new ClassPathResource("idp-metadata.xml"));
        configuration.init();

        final SAML2KeystoreGenerator generator =
            new SAML2FileSystemKeystoreGenerator(configuration);
        generator.generate();
        assertTrue(configuration.getKeystoreResource().getFile().exists());
    }

    @Test
    public void verifyKeystoreGeneration() throws Exception {
        final ConfigurationManager mgr = new DefaultConfigurationManager();
        mgr.configure();

        final SAML2Configuration configuration = new SAML2Configuration();
        configuration.setForceKeystoreGeneration(true);
        configuration.setKeystorePath("target/keystore.jks");
        configuration.setKeystorePassword("pac4j");
        configuration.setPrivateKeyPassword("pac4j");
        configuration.setServiceProviderMetadataResource(new FileSystemResource("target/out.xml"));
        configuration.setIdentityProviderMetadataResource(new ClassPathResource("idp-metadata.xml"));
        configuration.init();

        final SAML2KeystoreGenerator generator =
            new SAML2FileSystemKeystoreGenerator(configuration);
        generator.generate();
        assertTrue(configuration.getKeystoreResource().getFile().exists());

        final CredentialProvider provider = new KeyStoreCredentialProvider(configuration);
        assertNotNull(provider.getCredentialResolver());
        assertNotNull(provider.getCredential());
        assertNotNull(provider.getKeyInfo());
    }
}
