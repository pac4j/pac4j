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
        for (int i = 0; i <= MAX_NUM_CLIENTS; i++) {
            final String keystorePassword = getProperty(SAML_KEYSTORE_PASSWORD, i);
            final String privateKeyPassword = getProperty(SAML_PRIVATE_KEY_PASSWORD, i);
            final String keystorePath = getProperty(SAML_KEYSTORE_PATH, i);
            final String identityProviderMetadataPath = getProperty(SAML_IDENTITY_PROVIDER_METADATA_PATH, i);

            if (isNotBlank(keystorePassword) && isNotBlank(privateKeyPassword)
                    && isNotBlank(keystorePath) && isNotBlank(identityProviderMetadataPath)) {

                final String maximumAuthenticationLifetime = getProperty(SAML_MAXIMUM_AUTHENTICATION_LIFETIME, i);
                final String serviceProviderEntityId = getProperty(SAML_SERVICE_PROVIDER_ENTITY_ID, i);
                final String serviceProviderMetadataPath = getProperty(SAML_SERVICE_PROVIDER_METADATA_PATH, i);
                final String destinationBindingType = getProperty(SAML_AUTHN_REQUEST_BINDING_TYPE, i);
                final String keystoreAlias = getProperty(SAML_KEYSTORE_ALIAS, i);

                final SAML2Configuration cfg = new SAML2Configuration(keystorePath, keystorePassword,
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
                final SAML2Client saml2Client = new SAML2Client(cfg);
                saml2Client.setName(concat(saml2Client.getName(), i));

                clients.add(saml2Client);
            }
        }
    }
}
