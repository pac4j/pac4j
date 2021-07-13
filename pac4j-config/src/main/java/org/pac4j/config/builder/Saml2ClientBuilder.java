package org.pac4j.config.builder;

import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.client.Client;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.config.SAML2Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

                final var acceptedSkew = getProperty(SAML_ACCEPTED_SKEW, i);
                if (isNotBlank(acceptedSkew)) {
                    cfg.setAcceptedSkew(Long.parseLong(acceptedSkew));
                }

                final var assertionConsumerServiceIndex = getProperty(SAML_ASSERTION_CONSUMER_SERVICE_INDEX, i);
                if (isNotBlank(assertionConsumerServiceIndex)) {
                    cfg.setAssertionConsumerServiceIndex(Integer.parseInt(assertionConsumerServiceIndex));
                }

                final var forceAuth = getProperty(SAML_FORCE_AUTH, i);
                if (isNotBlank(forceAuth)) {
                    cfg.setForceAuth(Boolean.parseBoolean(forceAuth));
                }

                final var attributeAsId = getProperty(SAML_ATTRIBUTE_AS_ID, i);
                if (isNotBlank(attributeAsId)) {
                    cfg.setAttributeAsId(attributeAsId);
                }

                final var authnContextClassRefs = getProperty(SAML_AUTHN_CONTEXT_CLASS_REFS, i);
                if (isNotBlank(authnContextClassRefs)) {
                    cfg.setAuthnContextClassRefs(Arrays.stream(authnContextClassRefs.split(",")).collect(Collectors.toList()));
                }

                final var comparisonType = getProperty(SAML_COMPARISON_TYPE, i);
                if (isNotBlank(comparisonType)) {
                    cfg.setComparisonType(comparisonType);
                }

                final var issuerFormat = getProperty(SAML_ISSUER_FORMAT, i);
                if (isNotBlank(issuerFormat)) {
                    cfg.setIssuerFormat(issuerFormat);
                }

                final var authnRequestSigned = getProperty(SAML_AUTHN_REQUEST_SIGNED, i);
                if (isNotBlank(authnRequestSigned)) {
                    cfg.setAuthnRequestSigned(Boolean.parseBoolean(authnRequestSigned));
                }

                final var mappedAttributes = getProperty(SAML_MAPPED_ATTRIBUTES, i);
                if (isNotBlank(mappedAttributes)) {
                    var mapped = Arrays.stream(mappedAttributes.split(","))
                        .collect(Collectors.toMap(key -> key.split(":")[0],
                            value -> value.split(":")[1]));
                    cfg.setMappedAttributes(mapped);
                }

                final var nameIdAttribute = getProperty(SAML_NAMEID_ATTRIBUTE, i);
                if (isNotBlank(nameIdAttribute)) {
                    cfg.setNameIdAttribute(nameIdAttribute);
                }

                final var passive = getProperty(SAML_PASSIVE, i);
                if (isNotBlank(passive)) {
                    cfg.setPassive(Boolean.parseBoolean(passive));
                }

                final var responseBindingType = getProperty(SAML_RESPONSE_BINDING_TYPE, i);
                if (isNotBlank(responseBindingType)) {
                    cfg.setResponseBindingType(responseBindingType);
                }

                final var wantsAssertionsSigned = getProperty(SAML_WANTS_ASSERTIONS_SIGNED, i);
                if (isNotBlank(wantsAssertionsSigned)) {
                    cfg.setWantsAssertionsSigned(Boolean.parseBoolean(wantsAssertionsSigned));
                }

                final var wantsResponsesSigned = getProperty(SAML_WANTS_RESPONSES_SIGNED, i);
                if (isNotBlank(wantsResponsesSigned)) {
                    cfg.setWantsResponsesSigned(Boolean.parseBoolean(wantsResponsesSigned));
                }

                final var saml2Client = new SAML2Client(cfg);
                final var clientName = StringUtils.defaultString(getProperty(CLIENT_NAME, i),
                    concat(saml2Client.getName(), i));
                saml2Client.setName(clientName);

                clients.add(saml2Client);
            }
        }
    }
}
