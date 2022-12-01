package org.pac4j.saml.client;

import lombok.val;
import org.junit.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.crypto.KeyStoreCredentialProvider;
import org.pac4j.saml.util.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Generic tests on the {@link SAML2Client}.
 */
public final class SAML2ClientTests {

    public SAML2ClientTests() {
        assertNotNull(Configuration.getParserPool());
        assertNotNull(Configuration.getMarshallerFactory());
        assertNotNull(Configuration.getUnmarshallerFactory());
        assertNotNull(Configuration.getBuilderFactory());
    }

    @Test
    public void testIdpMetadataParsing_fromFile() {
        internalTestIdpMetadataParsing(new ClassPathResource("testshib-providers.xml"));
    }

    @Test
    public void testIdpMetadataParsing_fromUrl() throws MalformedURLException {
        internalTestIdpMetadataParsing(new UrlResource("https://www.pac4j.org/testshib-providers.xml"));
    }

    @Test
    public void testSaml2ConfigurationOfKeyStore() throws IOException {
        final Resource rs = new FileSystemResource("testKeystore.jks");
        if (rs.exists() && !rs.getFile().delete()) {
            throw new TechnicalException("File could not be deleted");
        }

        val cfg =
                new SAML2Configuration("testKeystore.jks",
                        "pac4j-test-passwd",
                        "pac4j-test-passwd",
                        "resource:testshib-providers.xml");
        cfg.init();

        val p = new KeyStoreCredentialProvider(cfg);
        assertNotNull(p.getKeyInfoGenerator());
        assertNotNull(p.getCredentialResolver());
        assertNotNull(p.getKeyInfo());
        assertNotNull(p.getKeyInfoCredentialResolver());
        assertNotNull(p.getCredential());
    }
    @Test
    public void testSaml2ConfigurationOfKeyStoreUsingResource() throws IOException {
        final Resource rs = new FileSystemResource("testKeystore.jks");
        if (rs.exists() && !rs.getFile().delete()) {
            throw new TechnicalException("File could not be deleted");
        }

        val cfg =
                new SAML2Configuration(new FileSystemResource("testKeystore.jks"),
                        "pac4j-test-passwd",
                        "pac4j-test-passwd",
                        new ClassPathResource("testshib-providers.xml"));
        cfg.init();

        val p = new KeyStoreCredentialProvider(cfg);
        assertNotNull(p.getKeyInfoGenerator());
        assertNotNull(p.getCredentialResolver());
        assertNotNull(p.getKeyInfo());
        assertNotNull(p.getKeyInfoCredentialResolver());
        assertNotNull(p.getCredential());
    }

    private void internalTestIdpMetadataParsing(final Resource resource) {
        val client = getClient();
        client.getConfiguration().setIdentityProviderMetadataResource(resource);
        client.init();

        client.getIdentityProviderMetadataResolver().resolve();
        val id = client.getIdentityProviderMetadataResolver().getEntityId();
        assertEquals("https://idp.testshib.org/idp/shibboleth", id);
    }

    protected SAML2Client getClient() {
        val cfg =
                new SAML2Configuration(new ClassPathResource("samlKeystore.jks"),
                        "pac4j-demo-passwd",
                        "pac4j-demo-passwd",
                        new ClassPathResource("testshib-providers.xml"));
        cfg.setMaximumAuthenticationLifetime(3600);
        cfg.setServiceProviderEntityId("urn:mace:saml:pac4j.org");
        cfg.setServiceProviderMetadataResource(new FileSystemResource(new File("target", "sp-metadata.xml").getAbsolutePath()));

        val saml2Client = new SAML2Client(cfg);
        saml2Client.setCallbackUrl("http://localhost:8080/something");
        return saml2Client;
    }
}
