/*
  Copyright 2012 -2014 Michael Remond

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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import org.apache.velocity.app.VelocityEngine;
import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.binding.encoding.HTTPPostEncoder;
import org.opensaml.saml2.binding.encoding.HTTPRedirectDeflateEncoder;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.EncryptedAttribute;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.encryption.Decrypter;
import org.opensaml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.provider.AbstractMetadataProvider;
import org.opensaml.saml2.metadata.provider.ChainingMetadataProvider;
import org.opensaml.saml2.metadata.provider.DOMMetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.saml2.metadata.provider.ResourceBackedMetadataProvider;
import org.opensaml.util.resource.ClasspathResource;
import org.opensaml.util.resource.FilesystemResource;
import org.opensaml.util.resource.Resource;
import org.opensaml.util.resource.ResourceException;
import org.opensaml.ws.message.decoder.MessageDecoder;
import org.opensaml.ws.message.encoder.MessageEncoder;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.encryption.DecryptionException;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.parse.ParserPool;
import org.opensaml.xml.parse.StaticBasicParserPool;
import org.opensaml.xml.parse.XMLParserException;
import org.opensaml.xml.security.keyinfo.NamedKeyInfoGeneratorManager;
import org.opensaml.xml.security.x509.X509KeyInfoGeneratorFactory;
import org.opensaml.xml.signature.SignatureTrustEngine;
import org.pac4j.core.client.BaseClient;
import org.pac4j.core.client.Mechanism;
import org.pac4j.core.client.RedirectAction;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.context.ExtendedSAMLMessageContext;
import org.pac4j.saml.context.Saml2ContextProvider;
import org.pac4j.saml.credentials.Saml2Credentials;
import org.pac4j.saml.crypto.CredentialProvider;
import org.pac4j.saml.crypto.EncryptionProvider;
import org.pac4j.saml.crypto.SignatureTrustEngineProvider;
import org.pac4j.saml.exceptions.SamlException;
import org.pac4j.saml.metadata.Saml2MetadataGenerator;
import org.pac4j.saml.profile.Saml2Profile;
import org.pac4j.saml.sso.Saml2AuthnRequestBuilder;
import org.pac4j.saml.sso.Saml2ResponseValidator;
import org.pac4j.saml.sso.Saml2WebSSOProfileHandler;
import org.pac4j.saml.transport.Pac4jHTTPPostDecoder;
import org.pac4j.saml.transport.SimpleResponseAdapter;
import org.pac4j.saml.util.VelocityEngineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class is the client to authenticate users with a SAML2 Identity Provider. This implementation relies on the Web
 * Browser SSO profile with HTTP-POST binding. (http://docs.oasis-open.org/security/saml/v2.0/saml-profiles-2.0-os.pdf).
 * 
 * @author Michael Remond
 * @since 1.5.0
 */
public class Saml2Client extends BaseClient<Saml2Credentials, Saml2Profile> {

    protected static final Logger logger = LoggerFactory.getLogger(Saml2Client.class);

    // Identify the KeyInfoGenerator factory created during opensaml boostrap
    public static final String SAML_METADATA_KEY_INFO_GENERATOR = "MetadataKeyInfoGenerator";

    public static final String SAML_RELAY_STATE_ATTRIBUTE = "samlRelayState";

    private String keystorePath;

    private String keystorePassword;

    private String privateKeyPassword;

    private String idpMetadata;

    private String idpMetadataPath;

    private String idpEntityId;

    private String spEntityId;

    private Integer maximumAuthenticationLifetime;

    private CredentialProvider credentialProvider;

    private Saml2ContextProvider contextProvider;

    private Saml2AuthnRequestBuilder authnRequestBuilder;

    private Saml2WebSSOProfileHandler handler;

    private Saml2ResponseValidator responseValidator;

    private SignatureTrustEngineProvider signatureTrustEngineProvider;

    private Decrypter decrypter;

    private String spMetadata;

    private boolean forceAuth = false;

    private String comparisonType = null;

    private String destinationBindingType = SAMLConstants.SAML2_POST_BINDING_URI;

    private String authnContextClassRef = null;

    private String nameIdPolicyFormat = null;

    @Override
    protected void internalInit() {

        CommonHelper.assertTrue(
                CommonHelper.isNotBlank(this.idpMetadata) || CommonHelper.isNotBlank(this.idpMetadataPath),
                "Either idpMetadata or idpMetadataPath must be provided");
        CommonHelper.assertNotBlank("callbackUrl", this.callbackUrl);
        if (!this.callbackUrl.startsWith("http")) {
            throw new TechnicalException("SAML callbackUrl must be absolute");
        }

        if (CommonHelper.isNotBlank(this.keystorePath) || CommonHelper.isNotBlank(this.keystorePassword)
                || CommonHelper.isNotBlank(this.privateKeyPassword)) {
            CommonHelper.assertNotBlank("keystorePath", this.keystorePath);
            CommonHelper.assertNotBlank("keystorePassword", this.keystorePassword);
            CommonHelper.assertNotBlank("privateKeyPassword", this.privateKeyPassword);

            // load private key from the keystore and provide it as OpenSAML credentials
            this.credentialProvider = new CredentialProvider(this.keystorePath, this.keystorePassword,
                    this.privateKeyPassword);
            this.decrypter = new EncryptionProvider(this.credentialProvider).buildDecrypter();
        }

        // Bootstrap OpenSAML
        try {
            DefaultBootstrap.bootstrap();
            NamedKeyInfoGeneratorManager manager = Configuration.getGlobalSecurityConfiguration()
                    .getKeyInfoGeneratorManager();
            X509KeyInfoGeneratorFactory generator = new X509KeyInfoGeneratorFactory();
            generator.setEmitEntityCertificate(true);
            generator.setEmitEntityCertificateChain(true);
            manager.registerFactory(Saml2Client.SAML_METADATA_KEY_INFO_GENERATOR, generator);
        } catch (ConfigurationException e) {
            throw new SamlException("Error bootstrapping OpenSAML", e);
        }

        // required parserPool for XML processing
        final StaticBasicParserPool parserPool = newStaticBasicParserPool();
        final AbstractMetadataProvider idpMetadataProvider = idpMetadataProvider(parserPool);

        final XMLObject md;
        try {
            md = idpMetadataProvider.getMetadata();
        } catch (MetadataProviderException e) {
            throw new SamlException("Error initializing idpMetadataProvider", e);
        }

        // If no idpEntityId declared, select first EntityDescriptor entityId as our IDP entityId
        if (this.idpEntityId == null) {
            this.idpEntityId = getIdpEntityId(md);
        }

        // Generate our Service Provider metadata
        Saml2MetadataGenerator metadataGenerator = new Saml2MetadataGenerator();
        if (this.credentialProvider != null) {
            metadataGenerator.setCredentialProvider(this.credentialProvider);
            metadataGenerator.setAuthnRequestSigned(true);
        }
        // If the spEntityId is blank, use the callback url
        if (CommonHelper.isBlank(this.spEntityId)) {
            this.spEntityId = getCallbackUrl();
        }
        metadataGenerator.setEntityId(this.spEntityId);
        // Assertion consumer service url is the callback url
        metadataGenerator.setAssertionConsumerServiceUrl(getCallbackUrl());
        // for now same for logout url
        metadataGenerator.setSingleLogoutServiceUrl(getCallbackUrl());
        AbstractMetadataProvider spMetadataProvider = metadataGenerator.buildMetadataProvider();

        // Initialize metadata provider for our SP and get the XML as a String
        try {
            spMetadataProvider.initialize();
            this.spMetadata = metadataGenerator.printMetadata();
        } catch (MetadataProviderException e) {
            throw new TechnicalException("Error initializing spMetadataProvider", e);
        } catch (MarshallingException e) {
            logger.warn("Unable to print SP metadata", e);
        }

        // Put IDP and SP metadata together
        ChainingMetadataProvider metadataManager = new ChainingMetadataProvider();
        try {
            metadataManager.addMetadataProvider(idpMetadataProvider);
            metadataManager.addMetadataProvider(spMetadataProvider);
        } catch (MetadataProviderException e) {
            throw new TechnicalException("Error adding idp or sp metadatas to manager", e);
        }

        // Build the contextProvider
        this.contextProvider = new Saml2ContextProvider(metadataManager, this.idpEntityId, this.spEntityId);

        // Get an AuthnRequest builder
        this.authnRequestBuilder = new Saml2AuthnRequestBuilder(forceAuth, comparisonType, destinationBindingType, 
                authnContextClassRef, nameIdPolicyFormat);

        // Build the WebSSO handler for sending and receiving SAML2 messages
        MessageEncoder encoder = null;
        if (SAMLConstants.SAML2_POST_BINDING_URI.equals(destinationBindingType)) {
            // Get a velocity engine for the HTTP-POST binding (building of an HTML document)
            VelocityEngine velocityEngine = VelocityEngineFactory.getEngine();
            encoder = new HTTPPostEncoder(velocityEngine, "/templates/saml2-post-binding.vm");
        } else if (SAMLConstants.SAML2_REDIRECT_BINDING_URI.equals(destinationBindingType)) {
            encoder = new HTTPRedirectDeflateEncoder();
        } else {
            throw new UnsupportedOperationException("Binding type - " + destinationBindingType + " is not supported");
        }

        // Do we need binding specific decoder?
        MessageDecoder decoder = new Pac4jHTTPPostDecoder(parserPool);
        this.handler = new Saml2WebSSOProfileHandler(this.credentialProvider, encoder, decoder, parserPool,
                destinationBindingType);

        // Build provider for digital signature validation and encryption
        this.signatureTrustEngineProvider = new SignatureTrustEngineProvider(metadataManager);

        // Build the SAML response validator
        this.responseValidator = new Saml2ResponseValidator();
        if (this.maximumAuthenticationLifetime != null) {
            this.responseValidator.setMaximumAuthenticationLifetime(this.maximumAuthenticationLifetime);
        }

    }

    @Override
    protected BaseClient<Saml2Credentials, Saml2Profile> newClient() {
        Saml2Client client = new Saml2Client();
        client.setKeystorePath(this.keystorePath);
        client.setKeystorePassword(this.keystorePassword);
        client.setPrivateKeyPassword(this.privateKeyPassword);
        client.setIdpMetadata(this.idpMetadata);
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

        ExtendedSAMLMessageContext context = this.contextProvider.buildSpAndIdpContext(wc);
        final String relayState = getStateParameter(wc);

        AuthnRequest authnRequest = this.authnRequestBuilder.build(context);

        this.handler.sendMessage(context, authnRequest, relayState);

        if (destinationBindingType.equalsIgnoreCase(SAMLConstants.SAML2_POST_BINDING_URI)) {
            String content = ((SimpleResponseAdapter) context.getOutboundMessageTransport()).getOutgoingContent();
            return RedirectAction.success(content);
        } else {
            String location = ((SimpleResponseAdapter) context.getOutboundMessageTransport()).getRedirectUrl();
            return RedirectAction.redirect(location);
        }

    }

    @Override
    protected Saml2Credentials retrieveCredentials(final WebContext wc) throws RequiresHttpAction {

        ExtendedSAMLMessageContext context = this.contextProvider.buildSpContext(wc);
        // assertion consumer url is pac4j callback url
        context.setAssertionConsumerUrl(getCallbackUrl());

        SignatureTrustEngine trustEngine = this.signatureTrustEngineProvider.build();

        this.handler.receiveMessage(context, trustEngine);

        this.responseValidator.validateSamlResponse(context, trustEngine, decrypter);

        return buildSaml2Credentials(context);

    }

    protected StaticBasicParserPool newStaticBasicParserPool() {
        StaticBasicParserPool parserPool = new StaticBasicParserPool();
        try {
            parserPool.initialize();
        } catch (XMLParserException e) {
            throw new SamlException("Error initializing parserPool", e);
        }
        return parserPool;
    }

    protected AbstractMetadataProvider idpMetadataProvider(ParserPool parserPool) {
        AbstractMetadataProvider idpMetadataProvider;
        try {
            if (idpMetadataPath != null) {
                Resource resource = null;
                if (this.idpMetadataPath.startsWith(CommonHelper.RESOURCE_PREFIX)) {
                    String path = this.idpMetadataPath.substring(CommonHelper.RESOURCE_PREFIX.length());
                    if (!path.startsWith("/")) {
                        path = "/" + path;
                    }
                    resource = new ClasspathResource(path);
                } else {
                    resource = new FilesystemResource(this.idpMetadataPath);
                }
                idpMetadataProvider = new ResourceBackedMetadataProvider(new Timer(true), resource);
            } else {
                InputStream in = new ByteArrayInputStream(idpMetadata.getBytes());
                Document inCommonMDDoc = parserPool.parse(in);
                Element metadataRoot = inCommonMDDoc.getDocumentElement();
                idpMetadataProvider = new DOMMetadataProvider(metadataRoot);
            }
            idpMetadataProvider.setParserPool(parserPool);
            idpMetadataProvider.initialize();
        } catch (MetadataProviderException e) {
            throw new SamlException("Error initializing idpMetadataProvider", e);
        } catch (XMLParserException e) {
            throw new TechnicalException("Error parsing idp Metadata", e);
        } catch (ResourceException e) {
            throw new TechnicalException("Error getting idp Metadata resource", e);
        }
        return idpMetadataProvider;
    }

    protected XMLObject getXmlObject(AbstractMetadataProvider idpMetadataProvider) {
        try {
            return idpMetadataProvider.getMetadata();
        } catch (MetadataProviderException e) {
            throw new SamlException("Error initializing idpMetadataProvider", e);
        }
    }

    protected String getIdpEntityId(XMLObject md) {
        if (md instanceof EntitiesDescriptor) {
            for (EntityDescriptor entity : ((EntitiesDescriptor) md).getEntityDescriptors()) {
                return entity.getEntityID();
            }
        } else if (md instanceof EntityDescriptor) {
            return ((EntityDescriptor) md).getEntityID();
        }
        throw new SamlException("No idp entityId found");
    }

    private Saml2Credentials buildSaml2Credentials(final ExtendedSAMLMessageContext context) {

        NameID nameId = (NameID) context.getSubjectNameIdentifier();
        Assertion subjectAssertion = context.getSubjectAssertion();

        List<Attribute> attributes = new ArrayList<Attribute>();
        for (AttributeStatement attributeStatement : subjectAssertion.getAttributeStatements()) {
            for (Attribute attribute : attributeStatement.getAttributes()) {
                attributes.add(attribute);
            }
            if (attributeStatement.getEncryptedAttributes().size() > 0) {
                logger.warn("Encrypted attributes returned, but no keystore was provided.");
            }
            for (EncryptedAttribute encryptedAttribute : attributeStatement.getEncryptedAttributes()) {
                try {
                    attributes.add(decrypter.decrypt(encryptedAttribute));
                } catch (DecryptionException e) {
                    logger.warn("Decryption of attribute failed, continue with the next one", e);
                }
            }
        }

        return new Saml2Credentials(nameId, attributes, subjectAssertion.getConditions(), getName());
    }

    @Override
    protected Saml2Profile retrieveUserProfile(final Saml2Credentials credentials, final WebContext context) {

        Saml2Profile profile = new Saml2Profile();
        profile.setId(credentials.getNameId().getValue());
        for (Attribute attribute : credentials.getAttributes()) {
            List<String> values = new ArrayList<String>();
            for (XMLObject attributeValue : attribute.getAttributeValues()) {
                Element attributeValueElement = attributeValue.getDOM();
                String value = attributeValueElement.getTextContent();
                values.add(value);
            }
            profile.addAttribute(attribute.getName(), values);
        }

        return profile;
    }

    @Override
    protected String getStateParameter(WebContext webContext) {
        String relayState = (String) webContext.getSessionAttribute(SAML_RELAY_STATE_ATTRIBUTE);
        return (relayState == null) ? getContextualCallbackUrl(webContext) : relayState;
    }

    @Override
    public Mechanism getMechanism() {
        return Mechanism.SAML_PROTOCOL;
    }

    public void setIdpMetadata(final String idpMetadata) {
        this.idpMetadata = idpMetadata;
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

    public void setMaximumAuthenticationLifetime(Integer maximumAuthenticationLifetime) {
        this.maximumAuthenticationLifetime = maximumAuthenticationLifetime;
    }

    public String printClientMetadata() {
        init();
        return this.spMetadata;
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
    public void setForceAuth(boolean forceAuth) {
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
    public void setComparisonType(String comparisonType) {
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
    public void setDestinationBindingType(String destinationBindingType) {
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
    public void setAuthnContextClassRef(String authnContextClassRef) {
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
    public void setNameIdPolicyFormat(String nameIdPolicyFormat) {
        this.nameIdPolicyFormat = nameIdPolicyFormat;
    }

}
