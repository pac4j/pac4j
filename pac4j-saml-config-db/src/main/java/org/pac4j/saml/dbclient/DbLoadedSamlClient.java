/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.pac4j.saml.dbclient;

import java.util.ArrayList;
import java.util.List;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.metadata.resolver.ChainingMetadataResolver;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.xmlsec.config.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.impl.BasicSignatureSigningConfiguration;
import org.pac4j.core.client.BaseClient;
import org.pac4j.core.client.ClientType;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.client.RedirectAction;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.client.SAML2ClientConfiguration;
import org.pac4j.saml.context.SAML2ContextProvider;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.context.SAMLContextProvider;
import org.pac4j.saml.credentials.SAML2Credentials;
import org.pac4j.saml.crypto.CredentialProvider;
import org.pac4j.saml.crypto.DefaultSignatureSigningParametersProvider;
import org.pac4j.saml.crypto.ExplicitSignatureTrustEngineProvider;
import org.pac4j.saml.crypto.KeyStoreDecryptionProvider;
import org.pac4j.saml.crypto.SAML2SignatureTrustEngineProvider;
import org.pac4j.saml.crypto.SignatureSigningParametersProvider;
import org.pac4j.saml.dbcrypto.KeyStoreCredentialProvider2;
import org.pac4j.saml.dbmetadata.SAML2IdentityProviderMetadataResolver2;
import org.pac4j.saml.metadata.SAML2MetadataResolver;
import org.pac4j.saml.metadata.SAML2ServiceProviderMetadataResolver;
import org.pac4j.saml.profile.SAML2Profile;
import org.pac4j.saml.sso.SAML2ObjectBuilder;
import org.pac4j.saml.sso.SAML2ProfileHandler;
import org.pac4j.saml.sso.SAML2ResponseValidator;
import org.pac4j.saml.sso.impl.SAML2AuthnRequestBuilder;
import org.pac4j.saml.sso.impl.SAML2DefaultResponseValidator;
import org.pac4j.saml.sso.impl.SAML2WebSSOMessageReceiver;
import org.pac4j.saml.sso.impl.SAML2WebSSOMessageSender;
import org.pac4j.saml.sso.impl.SAML2WebSSOProfileHandler;
import org.pac4j.saml.transport.Pac4jSAMLResponse;
import org.pac4j.saml.util.Configuration;
import org.w3c.dom.Element;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.resolver.ResolverException;


/**
 * An alternative to {@link SAML2Client}. It accepts {@link DbLoadedSamlClientConfiguration} instead of {@link SAML2ClientConfiguration}.
 * 
 * TODO: We could slightly rewrite SAML2Client in order to extract common code from SAML2Client and this class. At the moment, most code is
 * duplicated. {@link SAML2Client} cannot be extended because its constructor only accepts {@link SAML2ClientConfiguration}. Maybe a new
 * constructor plus a few init methods would be OK.
 * 
 * @author jkacer
 */
public class DbLoadedSamlClient extends IndirectClient<SAML2Credentials, SAML2Profile> {

    public static final String SAML_RELAY_STATE_ATTRIBUTE = "samlRelayState";

    protected CredentialProvider credentialProvider;

    protected SAMLContextProvider contextProvider;

    protected SAML2ObjectBuilder<AuthnRequest> saml2ObjectBuilder;

    protected SignatureSigningParametersProvider signatureSigningParametersProvider;

    protected SAML2ProfileHandler<AuthnRequest> profileHandler;

    protected SAML2ResponseValidator responseValidator;

    protected SAML2SignatureTrustEngineProvider signatureTrustEngineProvider;

    protected SAML2MetadataResolver idpMetadataResolver;

    protected SAML2MetadataResolver spMetadataResolver;

    protected Decrypter decrypter;

    /** Configuration loaded from a database. */
    protected final DbLoadedSamlClientConfiguration configuration;

    // ------------------------------------------------------------------------------------------------------------------------------------
    
    static {
        CommonHelper.assertNotNull("parserPool", Configuration.getParserPool());
        CommonHelper.assertNotNull("marshallerFactory", Configuration.getMarshallerFactory());
        CommonHelper.assertNotNull("unmarshallerFactory", Configuration.getUnmarshallerFactory());
        CommonHelper.assertNotNull("builderFactory", Configuration.getBuilderFactory());
    }

    
    public DbLoadedSamlClient(final DbLoadedSamlClientConfiguration configuration) {
        this.configuration = configuration;
        setName(configuration.getClientName());
    }

    // Just copied
    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotBlank("callbackUrl", this.callbackUrl);
        if (!this.callbackUrl.startsWith("http")) {
            throw new TechnicalException("SAML callbackUrl must be absolute");
        }

        initCredentialProvider();
        initDecrypter();
        initSignatureSigningParametersProvider();
        final MetadataResolver metadataManager = initChainingMetadataResolver(initIdentityProviderMetadataResolver(), initServiceProviderMetadataResolver());
        initSAMLContextProvider(metadataManager);
        initSAMLObjectBuilder();
        initSignatureTrustEngineProvider(metadataManager);
        initSAMLResponseValidator();
        initSAMLProfileHandler();
    }

    
    // Just copied
    private void initSAMLProfileHandler() {
        this.profileHandler = new SAML2WebSSOProfileHandler(
                new SAML2WebSSOMessageSender(this.signatureSigningParametersProvider, this.configuration.getDestinationBindingType(), false),
                new SAML2WebSSOMessageReceiver(this.responseValidator, this.credentialProvider));
    }

    
    // Just copied
    protected final void initSAMLResponseValidator() {
        this.responseValidator = new SAML2DefaultResponseValidator(
                this.signatureTrustEngineProvider,
                this.decrypter,
                this.configuration.getMaximumAuthenticationLifetime());
    }

    
    // Just copied
    protected final void initSignatureTrustEngineProvider(final MetadataResolver metadataManager) {
        this.signatureTrustEngineProvider = new ExplicitSignatureTrustEngineProvider(metadataManager);
    }

    
    // Just copied
    protected final void initSAMLObjectBuilder() {
        this.saml2ObjectBuilder = new SAML2AuthnRequestBuilder(
        		this.configuration.isForceAuth(),
                this.configuration.getComparisonType(),
                this.configuration.getDestinationBindingType(),
                this.configuration.getAuthnContextClassRef(),
                this.configuration.getNameIdPolicyFormat());
    }

    
    // Just copied
    protected final void initSAMLContextProvider(final MetadataResolver metadataManager) {
        this.contextProvider = new SAML2ContextProvider(
        		metadataManager,
                this.idpMetadataResolver,
                this.spMetadataResolver,
                this.configuration.getSamlMessageStorageFactory());
    }


    
    /**
     * Initializes the SP metadata resolver.
     * 
     * @return A resolver.
     */
    protected final MetadataResolver initServiceProviderMetadataResolver() {
        this.spMetadataResolver = new SAML2ServiceProviderMetadataResolver(
        		null, // SP Metadata Path is for GENERATION of SP metadata. It can be null. We don't need it.
                getCallbackUrl(),
                this.configuration.getServiceProviderEntityId(),
                this.configuration.isForceServiceProviderMetadataGeneration(),
                this.credentialProvider);
        return this.spMetadataResolver.resolve();
    }

    
    /**
     * Initializes the IdP metadata resolver.
     * 
     * @return A resolver.
     */
    protected final MetadataResolver initIdentityProviderMetadataResolver() {
        this.idpMetadataResolver = new SAML2IdentityProviderMetadataResolver2(
        		this.configuration.getIdentityProviderMetadata(),
                this.configuration.getIdentityProviderEntityId());
        return this.idpMetadataResolver.resolve();
    }

    
    /**
     * Initializes the credential provider.
     */
    protected final void initCredentialProvider() {
        this.credentialProvider = new KeyStoreCredentialProvider2(
        		this.configuration.getKeystoreBinaryData(),
                this.configuration.getKeystorePassword(),
                this.configuration.getPrivateKeyPassword());
    }

    
    // Just copied
    protected final void initDecrypter() {
        this.decrypter = new KeyStoreDecryptionProvider(this.credentialProvider).build();
    }

    
    protected final void initSignatureSigningParametersProvider() {
    	// New in 1.8.x: The default signature signing parameters provider now requires a configuration; the configuration should supply 4 parameters.
    	// The database-based configuration does not have these parameters (yet).
    	// So let's read the default values from OpenSaml, create a fake configuration and pass ti to the provider.
    	BasicSignatureSigningConfiguration openSamlDefaultConfig = DefaultSecurityConfigurationBootstrap.buildDefaultSignatureSigningConfiguration();
    	SAML2ClientConfiguration configWithDefaultSigningParameters = new SAML2ClientConfiguration();
    	configWithDefaultSigningParameters.setBlackListedSignatureSigningAlgorithms(openSamlDefaultConfig.getBlacklistedAlgorithms());
    	configWithDefaultSigningParameters.setSignatureAlgorithms(openSamlDefaultConfig.getSignatureAlgorithms());
    	configWithDefaultSigningParameters.setSignatureCanonicalizationAlgorithm(openSamlDefaultConfig.getSignatureCanonicalizationAlgorithm());
    	configWithDefaultSigningParameters.setSignatureReferenceDigestMethods(openSamlDefaultConfig.getSignatureReferenceDigestMethods());
        
    	this.signatureSigningParametersProvider = new DefaultSignatureSigningParametersProvider(this.credentialProvider, configWithDefaultSigningParameters);
    }

    
    // Just copied
	protected final ChainingMetadataResolver initChainingMetadataResolver(final MetadataResolver idpMetadataProvider, final MetadataResolver spMetadataProvider) {
		final ChainingMetadataResolver metadataManager = new ChainingMetadataResolver();
		metadataManager.setId(ChainingMetadataResolver.class.getCanonicalName());
		try {
			final List<MetadataResolver> list = new ArrayList<MetadataResolver>();
			list.add(idpMetadataProvider);
			list.add(spMetadataProvider);
			metadataManager.setResolvers(list);
			metadataManager.initialize();
		} catch (final ResolverException e) {
			throw new TechnicalException("Error adding idp or sp metadatas to manager", e);
		} catch (final ComponentInitializationException e) {
			throw new TechnicalException("Error initializing manager", e);
		}
		return metadataManager;
	}


	/* (non-Javadoc)
	 * @see org.pac4j.core.client.BaseClient#newClient()
	 */
	@Override
	protected BaseClient<SAML2Credentials, SAML2Profile> newClient() {
        try {
			return new DbLoadedSamlClient(this.configuration.clone());
		} catch (CloneNotSupportedException cnse) {
            throw new RuntimeException("Cannot clone SAML Configuration for a new SAML Client.", cnse);
		}
	}


    // Just copied
	@Override
	protected boolean isDirectRedirection() {
        return false;
	}


    // Just copied
    @Override
    protected final RedirectAction retrieveRedirectAction(final WebContext wc) {
        final SAML2MessageContext context = this.contextProvider.buildContext(wc);
        final String relayState = getStateParameter(wc);

        final AuthnRequest authnRequest = this.saml2ObjectBuilder.build(context);
        this.profileHandler.send(context, authnRequest, relayState); // <AuthnRequest> added to the declaration

        final Pac4jSAMLResponse adapter = context.getProfileRequestContextOutboundMessageTransportResponse();
        if (this.configuration.getDestinationBindingType().equalsIgnoreCase(SAMLConstants.SAML2_POST_BINDING_URI)) {
            final String content = adapter.getOutgoingContent();
            return RedirectAction.success(content);
        }
        final String location = adapter.getRedirectUrl();
        return RedirectAction.redirect(location);
    }


    // Just copied
    @Override
    protected final SAML2Credentials retrieveCredentials(final WebContext wc) throws RequiresHttpAction {
        final SAML2MessageContext context = this.contextProvider.buildContext(wc);
        final SAML2Credentials credentials = (SAML2Credentials) this.profileHandler.receive(context);
        credentials.setClientName(getName()); // The profile handler sets a hard-coded client name, we need the real one.
        return credentials;
    }


    // Just copied
    @Override
    protected final SAML2Profile retrieveUserProfile(final SAML2Credentials credentials, final WebContext context) {
        final SAML2Profile profile = new SAML2Profile();
        profile.setId(credentials.getNameId().getValue());
        for (final Attribute attribute : credentials.getAttributes()) {
            logger.debug("Processing profile attribute {}", attribute);

            final List<String> values = new ArrayList<String>();
            for (final XMLObject attributeValue : attribute.getAttributeValues()) {
                final Element attributeValueElement = attributeValue.getDOM();
                if (attributeValueElement != null) {
                    final String value = attributeValueElement.getTextContent();
                    logger.debug("Adding attribute value {} for attribute {}", value, attribute.getFriendlyName());
                    values.add(value);
                } else {
                    logger.warn("Attribute value DOM element is null for {}", attribute);
                }
            }

            if (!values.isEmpty()) {
                profile.addAttribute(attribute.getName(), values);
            } else {
                logger.debug("No attribute values found for {}", attribute.getName());
            }
        }

        return profile;
    }


    // Just copied
    @Override
    protected final String getStateParameter(final WebContext webContext) {
        final String relayState = (String) webContext.getSessionAttribute(SAML_RELAY_STATE_ATTRIBUTE);
        return (relayState == null) ? computeFinalCallbackUrl(webContext) : relayState;
    }

    
    @Override
    public final ClientType getClientType() {
        return ClientType.SAML_PROTOCOL;
    }

    public final SAML2ResponseValidator getResponseValidator() {
        return this.responseValidator;
    }

    public final SAML2MetadataResolver getServiceProviderMetadataResolver() {
        return this.spMetadataResolver;
    }

    public final SAML2MetadataResolver getIdentityProviderMetadataResolver() {
        return this.idpMetadataResolver;
    }

    public final String getIdentityProviderResolvedEntityId() {
        return this.idpMetadataResolver.getEntityId();
    }

    public final String getServiceProviderResolvedEntityId() {
        return this.spMetadataResolver.getEntityId();
    }

    public final DbLoadedSamlClientConfiguration getConfiguration() {
        return this.configuration;
    }
	
}
