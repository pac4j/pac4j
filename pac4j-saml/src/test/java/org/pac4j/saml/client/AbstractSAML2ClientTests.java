package org.pac4j.saml.client;

import org.pac4j.core.util.TestsConstants;
import org.pac4j.saml.storage.HttpSessionStorageFactory;
import org.pac4j.saml.util.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Abstract class to test the {@link SAML2Client}.
 */
public abstract class AbstractSAML2ClientTests implements TestsConstants {

    protected AbstractSAML2ClientTests() {
        assertNotNull(Configuration.getParserPool());
        assertNotNull(Configuration.getMarshallerFactory());
        assertNotNull(Configuration.getUnmarshallerFactory());
        assertNotNull(Configuration.getBuilderFactory());
    }

    protected final SAML2Client getClient() {
        final SAML2ClientConfiguration cfg =
                new SAML2ClientConfiguration(new FileSystemResource("target/samlKeystore.jks"),
                        "pac4j-demo-passwd",
                        "pac4j-demo-passwd",
                        new ClassPathResource("testshib-providers.xml"));

        cfg.setMaximumAuthenticationLifetime(3600);
        cfg.setDestinationBindingType(getDestinationBindingType());
        cfg.setServiceProviderEntityId("urn:mace:saml:pac4j.org");
        cfg.setForceServiceProviderMetadataGeneration(true);
        cfg.setForceKeystoreGeneration(true);
        cfg.setServiceProviderMetadataResource(new FileSystemResource(new File("target", "sp-metadata.xml").getAbsolutePath()));
        cfg.setSamlMessageStorageFactory(new HttpSessionStorageFactory());
        final SAML2Client saml2Client = new SAML2Client(cfg);
        saml2Client.setCallbackUrl(getCallbackUrl());
        return saml2Client;
    }

    protected abstract String getCallbackUrl();

    protected abstract String getDestinationBindingType();
}
