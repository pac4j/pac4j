package org.pac4j.saml.client;

import org.pac4j.core.util.TestsConstants;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.metadata.SAML2ServiceProviderRequestedAttribute;
import org.pac4j.saml.store.HttpSessionStoreFactory;
import org.pac4j.saml.util.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.io.File;

import static org.junit.Assert.assertNotNull;

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
        final var cfg = getSaml2Configuration();
        final var saml2Client = new SAML2Client(cfg);
        saml2Client.setCallbackUrl(getCallbackUrl());
        return saml2Client;
    }

    protected SAML2Configuration getSaml2Configuration() {
        final var cfg = new SAML2Configuration(new FileSystemResource("target/samlKeystore.jks"),
            "pac4j-demo-passwd",
            "pac4j-demo-passwd",
            new ClassPathResource("testshib-providers.xml"));

        cfg.setMaximumAuthenticationLifetime(3600);
        cfg.setAuthnRequestBindingType(getAuthnRequestBindingType());
        cfg.setServiceProviderEntityId("urn:mace:saml:pac4j.org");
        cfg.setForceServiceProviderMetadataGeneration(true);
        cfg.setForceKeystoreGeneration(true);
        cfg.setServiceProviderMetadataResource(new FileSystemResource(new File("target", "sp-metadata.xml").getAbsolutePath()));
        cfg.setSamlMessageStoreFactory(new HttpSessionStoreFactory());

        final var attribute =
            new SAML2ServiceProviderRequestedAttribute("urn:oid:1.3.6.1.4.1.5923.1.1.1.6", "eduPersonPrincipalName");
        attribute.setServiceLang("fr");
        attribute.setServiceName("MySAML2ServiceProvider");
        cfg.getRequestedServiceProviderAttributes().add(attribute);

        return cfg;
    }

    protected abstract String getCallbackUrl();

    protected abstract String getAuthnRequestBindingType();
}
