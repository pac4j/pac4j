package org.pac4j.saml.client;

import org.junit.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.io.Resource;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.crypto.KeyStoreCredentialProvider;
import org.pac4j.saml.util.Configuration;

import java.io.File;

import static org.junit.Assert.*;

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
        internalTestIdpMetadataParsing("resource:testshib-providers.xml");
    }

    @Test
    public void testIdpMetadataParsing_fromUrl() {
        internalTestIdpMetadataParsing("http://www.pac4j.org/testshib-providers.xml");
    }

    @Test
    public void testSaml2ConfigurationOfKeyStore() throws Exception {
        final Resource rs = CommonHelper.getResource("testKeystore.jks");
        if (rs.exists() && !rs.getFile().delete()) {
            throw new TechnicalException("File could not be deleted");
        }

        final SAML2ClientConfiguration cfg =
                new SAML2ClientConfiguration("testKeystore.jks",
                        "pac4j-test-passwd",
                        "pac4j-test-passwd",
                        "resource:testshib-providers.xml");
        assertNotNull(cfg.getKeyStore());
        assertTrue(cfg.getKeyStore().size() == 1);
        if (!rs.getFile().delete()) {
            throw new TechnicalException("File could not be deleted");
        }

        final KeyStoreCredentialProvider p = new KeyStoreCredentialProvider(cfg);
        assertNotNull(p.getKeyInfoGenerator());
        assertNotNull(p.getCredentialResolver());
        assertNotNull(p.getKeyInfo());
        assertNotNull(p.getKeyInfoCredentialResolver());
        assertNotNull(p.getCredential());
    }


    private void internalTestIdpMetadataParsing(final String metadata) {
        final SAML2Client client = getClient();
        client.getConfiguration().setIdentityProviderMetadataPath(metadata);
        client.init(null);

        client.getIdentityProviderMetadataResolver().resolve();
        final String id = client.getIdentityProviderMetadataResolver().getEntityId();
        assertEquals("https://idp.testshib.org/idp/shibboleth", id);
    }

    protected SAML2Client getClient() {
        final SAML2ClientConfiguration cfg =
                new SAML2ClientConfiguration("resource:samlKeystore.jks",
                        "pac4j-demo-passwd",
                        "pac4j-demo-passwd",
                        "resource:testshib-providers.xml");
        cfg.setMaximumAuthenticationLifetime(3600);
        cfg.setServiceProviderEntityId("urn:mace:saml:pac4j.org");
        cfg.setServiceProviderMetadataPath(new File("target", "sp-metadata.xml").getAbsolutePath());

        final SAML2Client saml2Client = new SAML2Client(cfg);
        saml2Client.setCallbackUrl("http://localhost:8080/something");
        return saml2Client;
    }
}
