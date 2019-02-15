package org.pac4j.saml.metadata;

import net.shibboleth.utilities.java.support.xml.SerializeSupport;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.io.MarshallerFactory;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.ext.saml2alg.DigestMethod;
import org.opensaml.saml.ext.saml2alg.SigningMethod;
import org.opensaml.saml.ext.saml2mdreqinit.RequestInitiator;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.AbstractBatchMetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.DOMMetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.FilesystemMetadataResolver;
import org.opensaml.saml.saml2.core.NameIDType;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.AttributeConsumingService;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.NameIDFormat;
import org.opensaml.saml.saml2.metadata.RequestedAttribute;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SingleLogoutService;
import org.opensaml.security.credential.UsageType;
import org.opensaml.xmlsec.SignatureSigningConfiguration;
import org.opensaml.xmlsec.algorithm.AlgorithmRegistry;
import org.opensaml.xmlsec.algorithm.AlgorithmSupport;
import org.opensaml.xmlsec.config.impl.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.pac4j.saml.crypto.CredentialProvider;
import org.pac4j.saml.util.Configuration;
import org.pac4j.saml.util.SAML2Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generates metadata object with standard values and overriden user defined values.
 *
 * @author Michael Remond
 * @since 1.5.0
 */
@SuppressWarnings("unchecked")
public class SAML2MetadataGenerator implements SAMLMetadataGenerator {

    protected static final Logger logger = LoggerFactory.getLogger(SAML2MetadataGenerator.class);

    protected final XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();

    protected final MarshallerFactory marshallerFactory = Configuration.getMarshallerFactory();

    protected final AlgorithmRegistry globalAlgorithmRegistry = AlgorithmSupport.getGlobalAlgorithmRegistry();

    protected CredentialProvider credentialProvider;

    protected String entityId;

    protected String assertionConsumerServiceUrl;

    protected String singleLogoutServiceUrl;

    protected boolean authnRequestSigned = false;

    protected boolean wantAssertionSigned = true;

    protected int defaultACSIndex = 0;

    protected String requestInitiatorLocation = null;

    protected String nameIdPolicyFormat = null;

    protected List<SAML2ServiceProvicerRequestedAttribute> requestedAttributes = new ArrayList<>();

    protected SignatureSigningConfiguration defaultSignatureSigningConfiguration =
            DefaultSecurityConfigurationBootstrap.buildDefaultSignatureSigningConfiguration();

    protected List<String> blackListedSignatureSigningAlgorithms = null;

    protected List<String> signatureAlgorithms = null;

    protected List<String> signatureReferenceDigestMethods = null;

    @Override
    public final MetadataResolver buildMetadataResolver(final Resource metadataResource) throws Exception {
        final AbstractBatchMetadataResolver resolver;
        if (metadataResource != null) {
            resolver = new FilesystemMetadataResolver(metadataResource.getFile());
        } else {
            final EntityDescriptor md = buildEntityDescriptor();
            final Element entityDescriptorElement = this.marshallerFactory.getMarshaller(md).marshall(md);
            resolver = new DOMMetadataResolver(entityDescriptorElement);
        }
        resolver.setRequireValidMetadata(true);
        resolver.setFailFastInitialization(true);
        resolver.setId(resolver.getClass().getCanonicalName());
        resolver.setParserPool(Configuration.getParserPool());
        resolver.initialize();
        return resolver;
    }

    @Override
    public final String getMetadata(final EntityDescriptor entityDescriptor) throws Exception {
        final Element entityDescriptorElement = this.marshallerFactory
            .getMarshaller(EntityDescriptor.DEFAULT_ELEMENT_NAME).marshall(entityDescriptor);
        return SerializeSupport.nodeToString(entityDescriptorElement);
    }

    @Override
    public final EntityDescriptor buildEntityDescriptor() {
        final SAMLObjectBuilder<EntityDescriptor> builder = (SAMLObjectBuilder<EntityDescriptor>)
            this.builderFactory.getBuilder(EntityDescriptor.DEFAULT_ELEMENT_NAME);
        final EntityDescriptor descriptor = builder.buildObject();
        descriptor.setEntityID(this.entityId);
        descriptor.setValidUntil(DateTime.now(DateTimeZone.UTC).plusYears(20));
        descriptor.setID(SAML2Utils.generateID());
        descriptor.setExtensions(generateMetadataExtensions());
        descriptor.getRoleDescriptors().add(buildSPSSODescriptor());
        return descriptor;
    }

    protected final Extensions generateMetadataExtensions() {
        final SAMLObjectBuilder<Extensions> builderExt = (SAMLObjectBuilder<Extensions>)
            this.builderFactory.getBuilder(Extensions.DEFAULT_ELEMENT_NAME);

        final Extensions extensions = builderExt.buildObject();
        extensions.getNamespaceManager().registerAttributeName(SigningMethod.TYPE_NAME);
        extensions.getNamespaceManager().registerAttributeName(DigestMethod.TYPE_NAME);

        List<String> filteredSignatureAlgorithms = filterSignatureAlgorithms(getSignatureAlgorithms());
        List<String> filteredSignatureReferenceDigestMethods = filterSignatureAlgorithms(getSignatureReferenceDigestMethods());

        final SAMLObjectBuilder<SigningMethod> signingMethodBuilder = (SAMLObjectBuilder<SigningMethod>)
                this.builderFactory.getBuilder(SigningMethod.DEFAULT_ELEMENT_NAME);

        for (String signingMethod : filteredSignatureAlgorithms) {
            SigningMethod method = signingMethodBuilder.buildObject();
            method.setAlgorithm(signingMethod);
            extensions.getUnknownXMLObjects().add(method);
        }

        final SAMLObjectBuilder<DigestMethod> digestMethodBuilder = (SAMLObjectBuilder<DigestMethod>)
            this.builderFactory.getBuilder(DigestMethod.DEFAULT_ELEMENT_NAME);

        for (String digestMethod : filteredSignatureReferenceDigestMethods) {
            DigestMethod method = digestMethodBuilder.buildObject();
            method.setAlgorithm(digestMethod);
            extensions.getUnknownXMLObjects().add(method);
        }

        return extensions;
    }

    protected final SPSSODescriptor buildSPSSODescriptor() {
        final SAMLObjectBuilder<SPSSODescriptor> builder = (SAMLObjectBuilder<SPSSODescriptor>)
            this.builderFactory.getBuilder(SPSSODescriptor.DEFAULT_ELEMENT_NAME);
        final SPSSODescriptor spDescriptor = builder.buildObject();

        spDescriptor.setAuthnRequestsSigned(this.authnRequestSigned);
        spDescriptor.setWantAssertionsSigned(this.wantAssertionSigned);
        spDescriptor.addSupportedProtocol(SAMLConstants.SAML20P_NS);
        spDescriptor.addSupportedProtocol(SAMLConstants.SAML10P_NS);
        spDescriptor.addSupportedProtocol(SAMLConstants.SAML11P_NS);

        final SAMLObjectBuilder<Extensions> builderExt = (SAMLObjectBuilder<Extensions>)
            this.builderFactory.getBuilder(Extensions.DEFAULT_ELEMENT_NAME);

        final Extensions extensions = builderExt.buildObject();
        extensions.getNamespaceManager().registerAttributeName(RequestInitiator.DEFAULT_ELEMENT_NAME);

        final SAMLObjectBuilder<RequestInitiator> builderReq = (SAMLObjectBuilder<RequestInitiator>)
            this.builderFactory.getBuilder(RequestInitiator.DEFAULT_ELEMENT_NAME);

        final RequestInitiator requestInitiator = builderReq.buildObject();
        requestInitiator.setLocation(this.requestInitiatorLocation);
        requestInitiator.setBinding(RequestInitiator.DEFAULT_ELEMENT_NAME.getNamespaceURI());

        extensions.getUnknownXMLObjects().add(requestInitiator);
        spDescriptor.setExtensions(extensions);

        spDescriptor.getNameIDFormats().addAll(buildNameIDFormat());

        int index = 0;
        spDescriptor.getAssertionConsumerServices()
            .add(getAssertionConsumerService(SAMLConstants.SAML2_POST_BINDING_URI, index++, this.defaultACSIndex == index));
        spDescriptor.getSingleLogoutServices().add(getSingleLogoutService(SAMLConstants.SAML2_POST_BINDING_URI));
        spDescriptor.getSingleLogoutServices().add(getSingleLogoutService(SAMLConstants.SAML2_REDIRECT_BINDING_URI));
        spDescriptor.getSingleLogoutServices().add(getSingleLogoutService(SAMLConstants.SAML2_SOAP11_BINDING_URI));

        if (credentialProvider != null) {
            spDescriptor.getKeyDescriptors().add(getKeyDescriptor(UsageType.SIGNING, this.credentialProvider.getKeyInfo()));
            spDescriptor.getKeyDescriptors().add(getKeyDescriptor(UsageType.ENCRYPTION, this.credentialProvider.getKeyInfo()));
        }

        if (!requestedAttributes.isEmpty()) {
            final SAMLObjectBuilder<AttributeConsumingService> attrServiceBuilder =
                (SAMLObjectBuilder<AttributeConsumingService>) this.builderFactory
                    .getBuilder(AttributeConsumingService.DEFAULT_ELEMENT_NAME);
            final AttributeConsumingService attributeService =
                attrServiceBuilder.buildObject(AttributeConsumingService.DEFAULT_ELEMENT_NAME);
            for (final SAML2ServiceProvicerRequestedAttribute attr : this.requestedAttributes) {
                final SAMLObjectBuilder<RequestedAttribute> attrBuilder = (SAMLObjectBuilder<RequestedAttribute>) this.builderFactory
                    .getBuilder(RequestedAttribute.DEFAULT_ELEMENT_NAME);
                final RequestedAttribute requestAttribute = attrBuilder.buildObject(RequestedAttribute.DEFAULT_ELEMENT_NAME);
                requestAttribute.setIsRequired(attr.isRequired());
                requestAttribute.setName(attr.getName());
                requestAttribute.setFriendlyName(attr.getFriendlyName());
                requestAttribute.setNameFormat(attr.getNameFormat());

                attributeService.getRequestAttributes().add(requestAttribute);
            }
            spDescriptor.getAttributeConsumingServices().add(attributeService);
        }
        return spDescriptor;

    }

    protected final Collection<NameIDFormat> buildNameIDFormat() {

        final SAMLObjectBuilder<NameIDFormat> builder = (SAMLObjectBuilder<NameIDFormat>) this.builderFactory
            .getBuilder(NameIDFormat.DEFAULT_ELEMENT_NAME);
        final Collection<NameIDFormat> formats = new ArrayList<>();

        if (this.nameIdPolicyFormat != null) {
            final NameIDFormat nameID = builder.buildObject();
            nameID.setFormat(this.nameIdPolicyFormat);
            formats.add(nameID);
        } else {
            final NameIDFormat transientNameID = builder.buildObject();
            transientNameID.setFormat(NameIDType.TRANSIENT);
            formats.add(transientNameID);
            final NameIDFormat persistentNameID = builder.buildObject();
            persistentNameID.setFormat(NameIDType.PERSISTENT);
            formats.add(persistentNameID);
            final NameIDFormat emailNameID = builder.buildObject();
            emailNameID.setFormat(NameIDType.EMAIL);
            formats.add(emailNameID);
            final NameIDFormat unspecNameID = builder.buildObject();
            unspecNameID.setFormat(NameIDType.UNSPECIFIED);
            formats.add(unspecNameID);
        }
        return formats;
    }

    protected AssertionConsumerService getAssertionConsumerService(final String binding, final int index,
                                                                         final boolean isDefault) {
        final SAMLObjectBuilder<AssertionConsumerService> builder = (SAMLObjectBuilder<AssertionConsumerService>) this.builderFactory
            .getBuilder(AssertionConsumerService.DEFAULT_ELEMENT_NAME);
        final AssertionConsumerService consumer = builder.buildObject();
        consumer.setLocation(this.assertionConsumerServiceUrl);
        consumer.setBinding(binding);
        if (isDefault) {
            consumer.setIsDefault(true);
        }
        consumer.setIndex(index);
        return consumer;
    }

    protected SingleLogoutService getSingleLogoutService(final String binding) {
        final SAMLObjectBuilder<SingleLogoutService> builder = (SAMLObjectBuilder<SingleLogoutService>) this.builderFactory
            .getBuilder(SingleLogoutService.DEFAULT_ELEMENT_NAME);
        final SingleLogoutService logoutService = builder.buildObject();
        logoutService.setLocation(this.singleLogoutServiceUrl);
        logoutService.setBinding(binding);
        return logoutService;
    }

    protected final KeyDescriptor getKeyDescriptor(final UsageType type, final KeyInfo key) {
        final SAMLObjectBuilder<KeyDescriptor> builder = (SAMLObjectBuilder<KeyDescriptor>)
            Configuration.getBuilderFactory()
                .getBuilder(KeyDescriptor.DEFAULT_ELEMENT_NAME);
        final KeyDescriptor descriptor = builder.buildObject();
        descriptor.setUse(type);
        descriptor.setKeyInfo(key);
        return descriptor;
    }

    public CredentialProvider getCredentialProvider() {
        return this.credentialProvider;
    }

    public final void setCredentialProvider(final CredentialProvider credentialProvider) {
        this.credentialProvider = credentialProvider;
    }

    public String getEntityId() {
        return this.entityId;
    }

    public final void setEntityId(final String entityId) {
        this.entityId = entityId;
    }

    public boolean isAuthnRequestSigned() {
        return this.authnRequestSigned;
    }

    public final void setAuthnRequestSigned(final boolean authnRequestSigned) {
        this.authnRequestSigned = authnRequestSigned;
    }

    public boolean isWantAssertionSigned() {
        return this.wantAssertionSigned;
    }

    public void setWantAssertionSigned(final boolean wantAssertionSigned) {
        this.wantAssertionSigned = wantAssertionSigned;
    }

    public int getDefaultACSIndex() {
        return this.defaultACSIndex;
    }

    public void setDefaultACSIndex(final int defaultACSIndex) {
        this.defaultACSIndex = defaultACSIndex;
    }

    public final void setAssertionConsumerServiceUrl(final String assertionConsumerServiceUrl) {
        this.assertionConsumerServiceUrl = assertionConsumerServiceUrl;
    }

    public final void setSingleLogoutServiceUrl(final String singleLogoutServiceUrl) {
        this.singleLogoutServiceUrl = singleLogoutServiceUrl;
    }

    public final void setRequestInitiatorLocation(final String requestInitiatorLocation) {
        this.requestInitiatorLocation = requestInitiatorLocation;
    }

    public String getNameIdPolicyFormat() {
        return this.nameIdPolicyFormat;
    }

    public void setNameIdPolicyFormat(final String nameIdPolicyFormat) {
        this.nameIdPolicyFormat = nameIdPolicyFormat;
    }

    public List<SAML2ServiceProvicerRequestedAttribute> getRequestedAttributes() {
        return requestedAttributes;
    }

    public void setRequestedAttributes(final List<SAML2ServiceProvicerRequestedAttribute> requestedAttributes) {
        this.requestedAttributes = requestedAttributes;
    }

    public List<String> getBlackListedSignatureSigningAlgorithms() {
        if (blackListedSignatureSigningAlgorithms == null) {
            this.blackListedSignatureSigningAlgorithms =
              new ArrayList<>(defaultSignatureSigningConfiguration.getBlacklistedAlgorithms());
        }

        return blackListedSignatureSigningAlgorithms;
    }

    public void setBlackListedSignatureSigningAlgorithms(List<String> blackListedSignatureSigningAlgorithms) {
        this.blackListedSignatureSigningAlgorithms = blackListedSignatureSigningAlgorithms;
    }

    public List<String> getSignatureAlgorithms() {
        if (signatureAlgorithms == null) {
            this.signatureAlgorithms = defaultSignatureSigningConfiguration.getSignatureAlgorithms();
        }

        return signatureAlgorithms;
    }

    public void setSignatureAlgorithms(List<String> signatureAlgorithms) {
        this.signatureAlgorithms = signatureAlgorithms;
    }

    public List<String> getSignatureReferenceDigestMethods() {
        if (signatureReferenceDigestMethods == null) {
            this.signatureReferenceDigestMethods = defaultSignatureSigningConfiguration.getSignatureReferenceDigestMethods();
        }
        return signatureReferenceDigestMethods;
    }

    public void setSignatureReferenceDigestMethods(List<String> signatureReferenceDigestMethods) {
        this.signatureReferenceDigestMethods = signatureReferenceDigestMethods;
    }

    private List<String> filterForRuntimeSupportedAlgorithms(final List<String> algorithms) {
        final List<String> filteredAlgorithms = new ArrayList<>(algorithms);
        return filteredAlgorithms.stream().filter(uri -> globalAlgorithmRegistry.isRuntimeSupported(uri)).collect(Collectors.toList());
    }

    private List<String> filterSignatureAlgorithms(final List<String> algorithms) {
        final List<String> filteredAlgorithms = filterForRuntimeSupportedAlgorithms(algorithms);
        this.signatureAlgorithms.removeAll(this.blackListedSignatureSigningAlgorithms);
        return filteredAlgorithms;
    }
}
