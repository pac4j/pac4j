package org.pac4j.config.builder;

import org.pac4j.core.client.Client;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.config.SAML2Configuration;

import java.util.List;
import java.util.Map;

import static org.pac4j.core.util.CommonHelper.isNotBlank;

/**
 * Builder for SAML2 clients.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class Saml2ClientBuilder extends AbstractBuilder {

    public Saml2ClientBuilder(final Map<String, String> properties) {
        super(properties);
    }

    public void tryCreateSaml2Client(final List<Client> clients) {
        for (var i = 0; i <= MAX_NUM_CLIENTS; i++) {
            final var keystorePassword = getProperty(SAML_KEYSTORE_PASSWORD, i);
            final var privateKeyPassword = getProperty(SAML_PRIVATE_KEY_PASSWORD, i);
            final var keystorePath = getProperty(SAML_KEYSTORE_PATH, i);
            final var identityProviderMetadataPath = getProperty(SAML_IDENTITY_PROVIDER_METADATA_PATH, i);

            if (isNotBlank(keystorePassword) && isNotBlank(privateKeyPassword)
                    && isNotBlank(keystorePath) && isNotBlank(identityProviderMetadataPath)) {

                final var maximumAuthenticationLifetime = getProperty(SAML_MAXIMUM_AUTHENTICATION_LIFETIME, i);
                final var serviceProviderEntityId = getProperty(SAML_SERVICE_PROVIDER_ENTITY_ID, i);
                final var serviceProviderMetadataPath = getProperty(SAML_SERVICE_PROVIDER_METADATA_PATH, i);
                final var destinationBindingType = getProperty(SAML_AUTHN_REQUEST_BINDING_TYPE, i);
                final var keystoreAlias = getProperty(SAML_KEYSTORE_ALIAS, i);

                final var cfg = new SAML2Configuration(keystorePath, keystorePassword,
                        privateKeyPassword, identityProviderMetadataPath);
                if (isNotBlank(maximumAuthenticationLifetime)) {
                    cfg.setMaximumAuthenticationLifetime(Integer.parseInt(maximumAuthenticationLifetime));
                }
                if (isNotBlank(serviceProviderEntityId)) {
                    cfg.setServiceProviderEntityId(serviceProviderEntityId);
                }
                if (isNotBlank(serviceProviderMetadataPath)) {
                    cfg.setServiceProviderMetadataPath(serviceProviderMetadataPath);
                }
                if (isNotBlank(destinationBindingType)) {
                    cfg.setAuthnRequestBindingType(destinationBindingType);
                }
                if (isNotBlank(keystoreAlias)) {
                    cfg.setKeystoreAlias(keystoreAlias);
                }
                final var saml2Client = new SAML2Client(cfg);
                saml2Client.setName(concat(saml2Client.getName(), i));

                clients.add(saml2Client);
            }
        }
    }
}
