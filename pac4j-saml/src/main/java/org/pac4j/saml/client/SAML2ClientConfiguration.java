package org.pac4j.saml.client;

import org.pac4j.saml.config.SAML2Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

/**
 * Use {@link SAML2Configuration}
 */
@Deprecated
public class SAML2ClientConfiguration extends SAML2Configuration {

    private static final Logger LOGGER = LoggerFactory.getLogger(SAML2ClientConfiguration.class);

    public SAML2ClientConfiguration() {
        warn();
    }

    public SAML2ClientConfiguration(final String keystorePath, final String keystorePassword, final String privateKeyPassword,
                              final String identityProviderMetadataPath) {
        super(null, null, mapPathToResource(keystorePath), keystorePassword, privateKeyPassword,
            mapPathToResource(identityProviderMetadataPath), null, null,
            DEFAULT_PROVIDER_NAME, null, null);
        warn();
    }

    public SAML2ClientConfiguration(final Resource keystoreResource, final String keystorePassword, final String privateKeyPassword,
                              final Resource identityProviderMetadataResource) {
        super(null, null, keystoreResource, keystorePassword, privateKeyPassword,
            identityProviderMetadataResource, null, null,
            DEFAULT_PROVIDER_NAME, null, null);
        warn();
    }

    public SAML2ClientConfiguration(final Resource keystoreResource, final String keyStoreAlias,
                              final String keyStoreType, final String keystorePassword, final String privateKeyPassword,
                              final Resource identityProviderMetadataResource) {
        super(keyStoreAlias, keyStoreType, keystoreResource, keystorePassword,
            privateKeyPassword, identityProviderMetadataResource, null,
            null, DEFAULT_PROVIDER_NAME, null, null);
        warn();
    }

    protected void warn() {
        LOGGER.warn("Deprecated: use the SAML2Configuration component instead of the SAML2ClientConfiguration component");
    }
}
