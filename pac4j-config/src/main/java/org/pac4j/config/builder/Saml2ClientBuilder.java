package org.pac4j.config.builder;

import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.client.Client;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.config.SAML2Configuration;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Builder for SAML2 clients.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class Saml2ClientBuilder extends AbstractBuilder {

    /**
     * <p>Constructor for Saml2ClientBuilder.</p>
     *
     * @param properties a {@link Map} object
     */
    public Saml2ClientBuilder(final Map<String, String> properties) {
        super(properties);
    }

    /**
     * <p>tryCreateSaml2Client.</p>
     *
     * @param clients a {@link java.util.List} object
     */
    public void tryCreateSaml2Client(final Collection<Client> clients) {
        for (var i = 0; i <= MAX_NUM_CLIENTS; i++) {
            val keystorePassword = getProperty(SAML_KEYSTORE_PASSWORD, i);
            val privateKeyPassword = getProperty(SAML_PRIVATE_KEY_PASSWORD, i);
            val keystorePath = getProperty(SAML_KEYSTORE_PATH, i);
            val identityProviderMetadataPath = getProperty(SAML_IDENTITY_PROVIDER_METADATA_PATH, i);


            if (StringUtils.isNotBlank(keystorePassword) && StringUtils.isNotBlank(privateKeyPassword)
                    && StringUtils.isNotBlank(keystorePath) && StringUtils.isNotBlank(identityProviderMetadataPath)) {

                val maximumAuthenticationLifetime = getProperty(SAML_MAXIMUM_AUTHENTICATION_LIFETIME, i);
                val serviceProviderEntityId = getProperty(SAML_SERVICE_PROVIDER_ENTITY_ID, i);
                val serviceProviderMetadataPath = getProperty(SAML_SERVICE_PROVIDER_METADATA_PATH, i);
                val destinationBindingType = getProperty(SAML_AUTHN_REQUEST_BINDING_TYPE, i);
                val keystoreAlias = getProperty(SAML_KEYSTORE_ALIAS, i);

                val cfg = new SAML2Configuration(keystorePath, keystorePassword,
                        privateKeyPassword, identityProviderMetadataPath);
                if (StringUtils.isNotBlank(maximumAuthenticationLifetime)) {
                    cfg.setMaximumAuthenticationLifetime(Integer.parseInt(maximumAuthenticationLifetime));
                }
                if (StringUtils.isNotBlank(serviceProviderEntityId)) {
                    cfg.setServiceProviderEntityId(serviceProviderEntityId);
                }
                if (StringUtils.isNotBlank(serviceProviderMetadataPath)) {
                    cfg.setServiceProviderMetadataPath(serviceProviderMetadataPath);
                }
                if (StringUtils.isNotBlank(destinationBindingType)) {
                    cfg.setAuthnRequestBindingType(destinationBindingType);
                }
                if (StringUtils.isNotBlank(keystoreAlias)) {
                    cfg.setKeyStoreAlias(keystoreAlias);
                }

                val acceptedSkew = getProperty(SAML_ACCEPTED_SKEW, i);
                if (StringUtils.isNotBlank(acceptedSkew)) {
                    cfg.setAcceptedSkew(Long.parseLong(acceptedSkew));
                }

                val assertionConsumerServiceIndex = getProperty(SAML_ASSERTION_CONSUMER_SERVICE_INDEX, i);
                if (StringUtils.isNotBlank(assertionConsumerServiceIndex)) {
                    cfg.setAssertionConsumerServiceIndex(Integer.parseInt(assertionConsumerServiceIndex));
                }

                val forceAuth = getProperty(SAML_FORCE_AUTH, i);
                if (StringUtils.isNotBlank(forceAuth)) {
                    cfg.setForceAuth(Boolean.parseBoolean(forceAuth));
                }

                val attributeAsId = getProperty(SAML_ATTRIBUTE_AS_ID, i);
                if (StringUtils.isNotBlank(attributeAsId)) {
                    cfg.setAttributeAsId(attributeAsId);
                }

                val authnContextClassRefs = getProperty(SAML_AUTHN_CONTEXT_CLASS_REFS, i);
                if (StringUtils.isNotBlank(authnContextClassRefs)) {
                    cfg.setAuthnContextClassRefs(Arrays.stream(authnContextClassRefs.split(",")).collect(Collectors.toList()));
                }

                val comparisonType = getProperty(SAML_COMPARISON_TYPE, i);
                if (StringUtils.isNotBlank(comparisonType)) {
                    cfg.setComparisonType(comparisonType);
                }

                val issuerFormat = getProperty(SAML_ISSUER_FORMAT, i);
                if (StringUtils.isNotBlank(issuerFormat)) {
                    cfg.setIssuerFormat(issuerFormat);
                }

                val authnRequestSigned = getProperty(SAML_AUTHN_REQUEST_SIGNED, i);
                if (StringUtils.isNotBlank(authnRequestSigned)) {
                    cfg.setAuthnRequestSigned(Boolean.parseBoolean(authnRequestSigned));
                }

                val mappedAttributes = getProperty(SAML_MAPPED_ATTRIBUTES, i);
                if (StringUtils.isNotBlank(mappedAttributes)) {
                    var mapped = Arrays.stream(mappedAttributes.split(","))
                        .collect(Collectors.toMap(key -> key.split(":")[0],
                            value -> value.split(":")[1]));
                    cfg.setMappedAttributes(mapped);
                }

                val nameIdAttribute = getProperty(SAML_NAMEID_ATTRIBUTE, i);
                if (StringUtils.isNotBlank(nameIdAttribute)) {
                    cfg.setNameIdAttribute(nameIdAttribute);
                }

                val passive = getProperty(SAML_PASSIVE, i);
                if (StringUtils.isNotBlank(passive)) {
                    cfg.setPassive(Boolean.parseBoolean(passive));
                }

                val responseBindingType = getProperty(SAML_RESPONSE_BINDING_TYPE, i);
                if (StringUtils.isNotBlank(responseBindingType)) {
                    cfg.setResponseBindingType(responseBindingType);
                }

                val wantsAssertionsSigned = getProperty(SAML_WANTS_ASSERTIONS_SIGNED, i);
                if (StringUtils.isNotBlank(wantsAssertionsSigned)) {
                    cfg.setWantsAssertionsSigned(Boolean.parseBoolean(wantsAssertionsSigned));
                }

                val wantsResponsesSigned = getProperty(SAML_WANTS_RESPONSES_SIGNED, i);
                if (StringUtils.isNotBlank(wantsResponsesSigned)) {
                    cfg.setWantsResponsesSigned(Boolean.parseBoolean(wantsResponsesSigned));
                }

                val saml2Client = new SAML2Client(cfg);
                val clientName = StringUtils.defaultString(getProperty(CLIENT_NAME, i),
                    concat(saml2Client.getName(), i));
                saml2Client.setName(clientName);

                clients.add(saml2Client);
            }
        }
    }
}
