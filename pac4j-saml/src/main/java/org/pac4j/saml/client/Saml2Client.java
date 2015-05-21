/*
  Copyright 2012 -2014 pac4j organization

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

package org.pac4j.saml.client;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.metadata.resolver.ChainingMetadataResolver;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.pac4j.core.client.BaseClient;
import org.pac4j.core.client.Mechanism;
import org.pac4j.core.client.RedirectAction;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.context.ExtendedSAMLMessageContext;
import org.pac4j.saml.context.SAML2ContextProvider;
import org.pac4j.saml.context.SAMLContextProvider;
import org.pac4j.saml.credentials.Saml2Credentials;
import org.pac4j.saml.crypto.CredentialProvider;
import org.pac4j.saml.crypto.DefaultSignatureSigningParametersProvider;
import org.pac4j.saml.crypto.ExplicitSignatureTrustEngineProvider;
import org.pac4j.saml.crypto.KeyStoreCredentialProvider;
import org.pac4j.saml.crypto.KeyStoreDecryptionProvider;
import org.pac4j.saml.crypto.SAML2SignatureTrustEngineProvider;
import org.pac4j.saml.crypto.SignatureSigningParametersProvider;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.metadata.SAML2IdentityProviderMetadataResolver;
import org.pac4j.saml.metadata.SAML2MetadataResolver;
import org.pac4j.saml.metadata.SAML2ServiceProviderMetadataResolver;
import org.pac4j.saml.profile.Saml2Profile;
import org.pac4j.saml.sso.SAML2ObjectBuilder;
import org.pac4j.saml.sso.SAML2ProfileHandler;
import org.pac4j.saml.sso.SAML2ResponseValidator;
import org.pac4j.saml.sso.impl.SAML2AuthnRequestBuilder;
import org.pac4j.saml.sso.impl.SAML2DefaultResponseValidator;
import org.pac4j.saml.sso.impl.SAML2WebSSOMessageReceiver;
import org.pac4j.saml.sso.impl.SAML2WebSSOMessageSender;
import org.pac4j.saml.sso.impl.SAML2WebSSOProfileHandler;
import org.pac4j.saml.transport.SimpleResponseAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is the client to authenticate users with a SAML2 Identity Provider. This implementation relies on the Web
 * Browser SSO profile with HTTP-POST binding. (http://docs.oasis-open.org/security/saml/v2.0/saml-profiles-2.0-os.pdf).
 * 
 * @author Michael Remond
 * @since 1.5.0
 */
public class SAML2Client extends BaseClient<Saml2Credentials, Saml2Profile> {

    protected static final Logger logger = LoggerFactory.getLogger(SAML2Client.class);

    public static final String SAML_RELAY_STATE_ATTRIBUTE = "samlRelayState";

    private String keystorePath;

    private String keystorePassword;

    private String privateKeyPassword;

    private String idpMetadataPath;

    private String idpEntityId;

    private String spEntityId;

    private Integer maximumAuthenticationLifetime;

    private CredentialProvider credentialProvider;

    private SAMLContextProvider contextProvider;

    private SAML2ObjectBuilder<AuthnRequest> saml2ObjectBuilder;

    private SignatureSigningParametersProvider signatureSigningParametersProvider;

    private SAML2ProfileHandler handler;

    private SAML2ResponseValidator responseValidator;

    private SAML2SignatureTrustEngineProvider signatureTrustEngineProvider;

    private SAML2MetadataResolver idpMetadataResolver;

    private SAML2MetadataResolver spMetadataResolver;

    private Decrypter decrypter;

    private boolean forceAuth = false;

    private String comparisonType = null;

    private String destinationBindingType = SAMLConstants.SAML2_POST_BINDING_URI;

    private String authnContextClassRef = null;

    private String nameIdPolicyFormat = null;

    private String spMeadataPath;

    private boolean forceSpMetadataGeneration;

    @Override
    protected void internalInit() {

        CommonHelper.assertTrue(CommonHelper.isNotBlank(this.idpMetadataPath),
                "idpMetadataPath must be provided");
        CommonHelper.assertNotBlank("callbackUrl", this.callbackUrl);
        if (!this.callbackUrl.startsWith("http")) {
            throw new TechnicalException("SAML callbackUrl must be absolute");
        }

        initialCredentialProviderAndDecryption();

        // Required parserPool for XML processing
        this.idpMetadataResolver = new SAML2IdentityProviderMetadataResolver(this.idpMetadataPath, this.idpEntityId);
        final MetadataResolver idpMetadataProvider = this.idpMetadataResolver.resolve();

        // Generate our Service Provider metadata
        this.spMetadataResolver = new SAML2ServiceProviderMetadataResolver(this.spMeadataPath, getCallbackUrl(),
                this.spEntityId, this.forceSpMetadataGeneration, this.credentialProvider);
        final MetadataResolver spMetadataProvider = this.spMetadataResolver.resolve();

        // Put IDP and SP metadata together
        final ChainingMetadataResolver metadataManager = getChainingMetadataResolver(idpMetadataProvider, spMetadataProvider);

        // Build the contextProvider
        this.contextProvider = new SAML2ContextProvider(metadataManager, this.idpMetadataResolver, this.spMetadataResolver);
        // Get an AuthnRequest builder
        this.saml2ObjectBuilder = new SAML2AuthnRequestBuilder(forceAuth, comparisonType, destinationBindingType,
                authnContextClassRef, nameIdPolicyFormat);

        // Build provider for digital signature validation and encryption
        this.signatureTrustEngineProvider = new ExplicitSignatureTrustEngineProvider(metadataManager);


        // Build the SAML response validator
        this.responseValidator = new SAML2DefaultResponseValidator(
                this.signatureTrustEngineProvider,
                this.decrypter,
                this.maximumAuthenticationLifetime);


        this.handler = new SAML2WebSSOProfileHandler(
                new SAML2WebSSOMessageSender(this.signatureSigningParametersProvider, this.destinationBindingType, false),
                new SAML2WebSSOMessageReceiver(this.responseValidator, this.credentialProvider));

    }

    private void initialCredentialProviderAndDecryption() {
        if (CommonHelper.isNotBlank(this.keystorePath) || CommonHelper.isNotBlank(this.keystorePassword)
                || CommonHelper.isNotBlank(this.privateKeyPassword)) {
            CommonHelper.assertNotBlank("keystorePath", this.keystorePath);
            CommonHelper.assertNotBlank("keystorePassword", this.keystorePassword);
            CommonHelper.assertNotBlank("privateKeyPassword", this.privateKeyPassword);

            // load private key from the keystore and provide it as OpenSAML credentials
            this.credentialProvider = new KeyStoreCredentialProvider(this.keystorePath, this.keystorePassword,
                    this.privateKeyPassword);
            this.decrypter = new KeyStoreDecryptionProvider(this.credentialProvider).build();
            this.signatureSigningParametersProvider = new DefaultSignatureSigningParametersProvider(this.credentialProvider);
        }
    }

    private ChainingMetadataResolver getChainingMetadataResolver(MetadataResolver idpMetadataProvider, MetadataResolver spMetadataProvider) {
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

    @Override
    protected BaseClient<Saml2Credentials, Saml2Profile> newClient() {
        final SAML2Client client = new SAML2Client();
        client.setKeystorePath(this.keystorePath);
        client.setKeystorePassword(this.keystorePassword);
        client.setPrivateKeyPassword(this.privateKeyPassword);
        client.setIdpMetadataPath(this.idpMetadataPath);
        client.setIdpEntityId(this.idpEntityId);
        client.setSpEntityId(this.spEntityId);
        client.setMaximumAuthenticationLifetime(this.maximumAuthenticationLifetime);
        client.setCallbackUrl(this.callbackUrl);
        client.setDestinationBindingType(this.destinationBindingType);
        client.setComparisonType(this.comparisonType);
        client.setAuthnContextClassRef(this.authnContextClassRef);
        client.setNameIdPolicyFormat(this.nameIdPolicyFormat);
        return client;
    }

    @Override
    protected boolean isDirectRedirection() {
        return false;
    }

    @Override
    protected RedirectAction retrieveRedirectAction(final WebContext wc) {

        final ExtendedSAMLMessageContext context = this.contextProvider.buildContext(wc);
        final String relayState = getStateParameter(wc);

        final AuthnRequest authnRequest = this.saml2ObjectBuilder.build(context);
        this.handler.send(context, authnRequest, relayState);

        final SimpleResponseAdapter adapter = context.getProfileRequestContextOutboundMessageTransportResponse();
        if (destinationBindingType.equalsIgnoreCase(SAMLConstants.SAML2_POST_BINDING_URI)) {
            final String content = adapter.getOutgoingContent();
            return RedirectAction.success(content);
        }
        final String location = adapter.getRedirectUrl();
        return RedirectAction.redirect(location);

    }

    @Override
    protected Saml2Credentials retrieveCredentials(final WebContext wc) throws RequiresHttpAction {
        final ExtendedSAMLMessageContext context = this.contextProvider.buildContext(wc);
        return (Saml2Credentials) this.handler.receive(context);
    }

    @Override
    protected Saml2Profile retrieveUserProfile(final Saml2Credentials credentials, final WebContext context) {

        final Saml2Profile profile = new Saml2Profile();
        profile.setId(credentials.getNameId().getValue());
        for (final Attribute attribute : credentials.getAttributes()) {
            final List<String> values = new ArrayList<String>();
            for (final XMLObject attributeValue : attribute.getAttributeValues()) {
                final Element attributeValueElement = attributeValue.getDOM();
                final String value = attributeValueElement.getTextContent();
                values.add(value);
            }
            profile.addAttribute(attribute.getName(), values);
        }

        return profile;
    }

    @Override
    protected String getStateParameter(final WebContext webContext) {
        final String relayState = (String) webContext.getSessionAttribute(SAML_RELAY_STATE_ATTRIBUTE);
        return (relayState == null) ? getContextualCallbackUrl(webContext) : relayState;
    }

    public SAML2ResponseValidator getResponseValidator() {
        return responseValidator;
    }

    public SAML2MetadataResolver getServiceProviderMetadataResolver() {
        return spMetadataResolver;
    }

    public SAML2MetadataResolver getIdentityProviderMetadataResolver() {
        return idpMetadataResolver;
    }

    @Override
    public Mechanism getMechanism() {
        return Mechanism.SAML_PROTOCOL;
    }

    public void setIdpMetadataPath(final String idpMetadataPath) {
        this.idpMetadataPath = idpMetadataPath;
    }

    public void setIdpEntityId(final String idpEntityId) {
        this.idpEntityId = idpEntityId;
    }

    public void setSpEntityId(String spEntityId) {
        this.spEntityId = spEntityId;
    }

    public void setKeystorePath(final String keystorePath) {
        this.keystorePath = keystorePath;
    }

    public void setKeystorePassword(final String keystorePassword) {
        this.keystorePassword = keystorePassword;
    }

    public void setPrivateKeyPassword(final String privateKeyPassword) {
        this.privateKeyPassword = privateKeyPassword;
    }

    public void setMaximumAuthenticationLifetime(final Integer maximumAuthenticationLifetime) {
        this.maximumAuthenticationLifetime = maximumAuthenticationLifetime;
    }

    public String getIdpMetadataPath() {
        return idpMetadataPath;
    }

    public String getIdpEntityId() {
        return this.idpMetadataResolver.getEntityId();
    }

    public String getSpEntityId() {
        return this.spMetadataResolver.getEntityId();
    }

    /**
     * @return the forceAuth
     */
    public boolean isForceAuth() {
        return forceAuth;
    }

    /**
     * @param forceAuth the forceAuth to set
     */
    public void setForceAuth(final boolean forceAuth) {
        this.forceAuth = forceAuth;
    }

    /**
     * @return the comparisonType
     */
    public String getComparisonType() {
        return comparisonType;
    }

    /**
     * @param comparisonType the comparisonType to set
     */
    public void setComparisonType(final String comparisonType) {
        this.comparisonType = comparisonType;
    }

    /**
     * @return the destinationBindingType
     */
    public String getDestinationBindingType() {
        return destinationBindingType;
    }

    /**
     * @param destinationBindingType the destinationBindingType to set
     */
    public void setDestinationBindingType(final String destinationBindingType) {
        this.destinationBindingType = destinationBindingType;
    }

    /**
     * @return the authnContextClassRef
     */
    public String getAuthnContextClassRef() {
        return authnContextClassRef;
    }

    /**
     * @param authnContextClassRef the authnContextClassRef to set
     */
    public void setAuthnContextClassRef(final String authnContextClassRef) {
        this.authnContextClassRef = authnContextClassRef;
    }

    /**
     * @return the nameIdPolicyFormat
     */
    public String getNameIdPolicyFormat() {
        return nameIdPolicyFormat;
    }

    /**
     * @param nameIdPolicyFormat the nameIdPolicyFormat to set
     */
    public void setNameIdPolicyFormat(final String nameIdPolicyFormat) {
        this.nameIdPolicyFormat = nameIdPolicyFormat;
    }

    public void setSpMeadataPath(final String spMeadataPath) {
        this.spMeadataPath = spMeadataPath;
    }

    public void setForceSpMetadataGeneration(boolean forceSpMetadataGeneration) {
        this.forceSpMetadataGeneration = forceSpMetadataGeneration;
    }
}
