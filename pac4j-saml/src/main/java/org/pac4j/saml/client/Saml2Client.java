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

import net.shibboleth.ext.spring.resource.ResourceHelper;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import net.shibboleth.utilities.java.support.resource.Resource;
import net.shibboleth.utilities.java.support.xml.ParserPool;
import net.shibboleth.utilities.java.support.xml.XMLParserException;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.metadata.resolver.ChainingMetadataResolver;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.DOMMetadataResolver;
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
import org.pac4j.saml.metadata.SAML2MetadataGenerator;
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
import org.pac4j.saml.util.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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

    private String idpMetadata;

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

    private Decrypter decrypter;

    private String spMetadata;

    private boolean forceAuth = false;

    private String comparisonType = null;

    private String destinationBindingType = SAMLConstants.SAML2_POST_BINDING_URI;

    private String authnContextClassRef = null;

    private String nameIdPolicyFormat = null;

    private String spMeadataPath;

    private boolean forceSpMetadataGeneration;

    @Override
    protected void internalInit() {

        CommonHelper.assertTrue(
                CommonHelper.isNotBlank(this.idpMetadata) || CommonHelper.isNotBlank(this.idpMetadataPath),
                "Either idpMetadata or idpMetadataPath must be provided");
        CommonHelper.assertNotBlank("callbackUrl", this.callbackUrl);
        if (!this.callbackUrl.startsWith("http")) {
            throw new TechnicalException("SAML callbackUrl must be absolute");
        }

        initialCredentialProviderAndDecryption();

        // Required parserPool for XML processing
        final MetadataResolver idpMetadataProvider = generateIdentityProviderMetadata(Configuration.getParserPool());

        // Generate our Service Provider metadata
        final MetadataResolver spMetadataProvider = generateServiceProviderMetadata();

        // Put IDP and SP metadata together
        final ChainingMetadataResolver metadataManager = getChainingMetadataResolver(idpMetadataProvider, spMetadataProvider);

        // Build the contextProvider
        this.contextProvider = new SAML2ContextProvider(metadataManager, this.idpEntityId, this.spEntityId);
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

    private MetadataResolver generateServiceProviderMetadata() {
        try {
            final SAML2MetadataGenerator metadataGenerator = new SAML2MetadataGenerator();
            if (this.credentialProvider != null) {
                metadataGenerator.setCredentialProvider(this.credentialProvider);
                metadataGenerator.setAuthnRequestSigned(true);
            }
            // If the spEntityId is blank, use the callback url
            if (CommonHelper.isBlank(this.spEntityId)) {
                final URL url = new URL(getCallbackUrl());
                if (url.getQuery() != null) {
                    this.spEntityId = url.toString().replace("?" + url.getQuery(), "");
                } else {
                    this.spEntityId = url.toString();
                }
            }
            metadataGenerator.setEntityId(this.spEntityId);
            metadataGenerator.setRequestInitiatorLocation(this.getCallbackUrl());
            // Assertion consumer service url is the callback url
            metadataGenerator.setAssertionConsumerServiceUrl(getCallbackUrl());
            // for now same for logout url
            metadataGenerator.setSingleLogoutServiceUrl(getCallbackUrl());
            final MetadataResolver spMetadataProvider = metadataGenerator.buildMetadataResolver();

            // Initialize metadata provider for our SP and get the XML as a String
            this.spMetadata = metadataGenerator.getMetadata();
            if (this.spMeadataPath != null) {

                final File file = new File(this.spMeadataPath);
                if (file.exists() && !this.forceSpMetadataGeneration) {
                    logger.info("Metadata file already exists at {}.", this.spMeadataPath);
                } else {
                    logger.info("Writing sp metadata to {}", this.spMeadataPath);

                    final Transformer transformer = TransformerFactory.newInstance().newTransformer();
                    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                    StreamResult result = new StreamResult(new StringWriter());
                    final StreamSource source = new StreamSource(new StringReader(this.spMetadata));
                    transformer.transform(source, result);
                    final FileWriter writer = new FileWriter(this.spMeadataPath);
                    writer.write(result.getWriter().toString());
                    writer.close();
                }
            }
            return spMetadataProvider;
        } catch (ComponentInitializationException e) {
            throw new TechnicalException("Error initializing spMetadataProvider", e);
        } catch (MarshallingException e) {
            logger.warn("Unable to marshal SP metadata", e);
        } catch (IOException e) {
            logger.warn("Unable to print SP metadata", e);
        } catch (Exception e) {
            logger.warn("Unable to transform metadata", e);
        }
        return null;
    }

    @Override
    protected BaseClient<Saml2Credentials, Saml2Profile> newClient() {
        final SAML2Client client = new SAML2Client();
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
        final ExtendedSAMLMessageContext context = this.contextProvider.buildServiceProviderContext(wc);
        context.setAssertionConsumerUrl(getCallbackUrl());
        return (Saml2Credentials) this.handler.receive(context);
    }

    protected MetadataResolver generateIdentityProviderMetadata(ParserPool parserPool) {
        DOMMetadataResolver idpMetadataProvider;
        try {
            InputStream in;

            if (idpMetadataPath != null) {
                Resource resource;
                if (this.idpMetadataPath.startsWith(CommonHelper.RESOURCE_PREFIX)) {
                    String path = this.idpMetadataPath.substring(CommonHelper.RESOURCE_PREFIX.length());
                    if (!path.startsWith("/")) {
                        path = "/" + path;
                    }
                    resource = ResourceHelper.of(new ClassPathResource(path));
                } else {
                    resource = ResourceHelper.of(new FileSystemResource(this.idpMetadataPath));
                }
                in = resource.getInputStream();
            } else {
                in = new ByteArrayInputStream(idpMetadata.getBytes());
            }

            final Document inCommonMDDoc = parserPool.parse(in);
            final Element metadataRoot = inCommonMDDoc.getDocumentElement();
            idpMetadataProvider = new DOMMetadataResolver(metadataRoot);

            idpMetadataProvider.setParserPool(parserPool);
            idpMetadataProvider.setFailFastInitialization(true);
            idpMetadataProvider.setRequireValidMetadata(true);
            idpMetadataProvider.setId(idpMetadataProvider.getClass().getCanonicalName());
            idpMetadataProvider.initialize();


            // If no idpEntityId declared, select first EntityDescriptor entityId as our IDP entityId
            if (this.idpEntityId == null) {
                idpMetadataProvider.forEach(new Consumer<EntityDescriptor>() {
                    @Override
                    public void accept(final EntityDescriptor entityDescriptor) {
                        if (SAML2Client.this.idpEntityId == null) {
                            SAML2Client.this.idpEntityId = entityDescriptor.getEntityID();
                        }
                    }
                });
            }

            if (this.idpEntityId == null) {
                throw new SAMLException("No idp entityId found");
            }

        } catch (ComponentInitializationException e) {
            throw new SAMLException("Error initializing idpMetadataProvider", e);
        } catch (XMLParserException e) {
            throw new TechnicalException("Error parsing idp Metadata", e);
        } catch (IOException e) {
            throw new TechnicalException("Error getting idp Metadata resource", e);
        }
        return idpMetadataProvider;
    }

    protected EntityDescriptor getXmlObject(MetadataResolver idpMetadataProvider, String entityId) {
        try {
            return idpMetadataProvider.resolveSingle(new CriteriaSet(new EntityIdCriterion(entityId)));
        } catch (ResolverException e) {
            throw new SAMLException("Error initializing idpMetadataProvider", e);
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
        throw new SAMLException("No idp entityId found");
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

    public void setMaximumAuthenticationLifetime(final Integer maximumAuthenticationLifetime) {
        this.maximumAuthenticationLifetime = maximumAuthenticationLifetime;
    }

    public String printClientMetadata() {
        init();
        return this.spMetadata;
    }

    public String getIdpMetadata() {
        return idpMetadata;
    }

    public String getIdpMetadataPath() {
        return idpMetadataPath;
    }

    public String getIdpEntityId() {
        return idpEntityId;
    }

    public String getSpEntityId() {
        return spEntityId;
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
