package org.pac4j.saml.metadata;

import net.shibboleth.utilities.java.support.xml.SerializeSupport;
import org.apache.commons.lang3.StringUtils;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.io.MarshallerFactory;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.ext.saml2alg.DigestMethod;
import org.opensaml.saml.ext.saml2alg.SigningMethod;
import org.opensaml.saml.ext.saml2mdreqinit.RequestInitiator;
import org.opensaml.saml.ext.saml2mdui.Description;
import org.opensaml.saml.ext.saml2mdui.DisplayName;
import org.opensaml.saml.ext.saml2mdui.InformationURL;
import org.opensaml.saml.ext.saml2mdui.Keywords;
import org.opensaml.saml.ext.saml2mdui.Logo;
import org.opensaml.saml.ext.saml2mdui.PrivacyStatementURL;
import org.opensaml.saml.ext.saml2mdui.UIInfo;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.AbstractBatchMetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.DOMMetadataResolver;
import org.opensaml.saml.saml2.core.NameIDType;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.AttributeConsumingService;
import org.opensaml.saml.saml2.metadata.Company;
import org.opensaml.saml.saml2.metadata.ContactPerson;
import org.opensaml.saml.saml2.metadata.ContactPersonTypeEnumeration;
import org.opensaml.saml.saml2.metadata.EmailAddress;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.metadata.GivenName;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.NameIDFormat;
import org.opensaml.saml.saml2.metadata.RequestedAttribute;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml.saml2.metadata.ServiceName;
import org.opensaml.saml.saml2.metadata.SingleLogoutService;
import org.opensaml.saml.saml2.metadata.SurName;
import org.opensaml.saml.saml2.metadata.TelephoneNumber;
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

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Generates metadata object with standard values and overridden user defined values.
 *
 * @author Misagh Moayyed
 * @since 4.0.1
 */
@SuppressWarnings("unchecked")
public abstract class BaseSAML2MetadataGenerator implements SAML2MetadataGenerator {

    protected static final Logger logger = LoggerFactory.getLogger(BaseSAML2MetadataGenerator.class);

    protected final XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();

    protected final MarshallerFactory marshallerFactory = Configuration.getMarshallerFactory();

    protected final AlgorithmRegistry globalAlgorithmRegistry = AlgorithmSupport.getGlobalAlgorithmRegistry();

    protected CredentialProvider credentialProvider;

    protected String entityId;

    protected String assertionConsumerServiceUrl;

    protected String responseBindingType = SAMLConstants.SAML2_POST_BINDING_URI;

    protected String singleLogoutServiceUrl;

    protected boolean authnRequestSigned = false;

    protected boolean wantAssertionSigned = true;

    protected boolean signMetadata = false;

    protected int defaultACSIndex = 0;

    protected String requestInitiatorLocation = null;

    protected String nameIdPolicyFormat = null;

    protected List<SAML2ServiceProviderRequestedAttribute> requestedAttributes = new ArrayList<>();

    protected SignatureSigningConfiguration defaultSignatureSigningConfiguration =
        DefaultSecurityConfigurationBootstrap.buildDefaultSignatureSigningConfiguration();

    protected List<String> blackListedSignatureSigningAlgorithms = null;

    protected List<String> signatureAlgorithms = null;

    protected List<String> signatureReferenceDigestMethods = null;

    private List<SAML2MetadataContactPerson> contactPersons = new ArrayList<>();

    private List<SAML2MetadataUIInfo> metadataUIInfos = new ArrayList<>();

    private List<String> supportedProtocols = new ArrayList<>(Arrays.asList(SAMLConstants.SAML20P_NS,
        SAMLConstants.SAML10P_NS, SAMLConstants.SAML11P_NS));

    private SAML2MetadataSigner metadataSigner;

    @Override
    public MetadataResolver buildMetadataResolver(final Resource metadataResource) throws Exception {
        final AbstractBatchMetadataResolver resolver;
        if (metadataResource != null) {
            resolver = createMetadataResolver(metadataResource);
        } else {
            final var md = buildEntityDescriptor();
            final var entityDescriptorElement = this.marshallerFactory.getMarshaller(md).marshall(md);
            resolver = new DOMMetadataResolver(entityDescriptorElement);
        }
        resolver.setRequireValidMetadata(true);
        resolver.setFailFastInitialization(true);
        resolver.setId(resolver.getClass().getCanonicalName());
        resolver.setParserPool(Configuration.getParserPool());
        resolver.initialize();
        return resolver;
    }

    protected abstract AbstractBatchMetadataResolver createMetadataResolver(final Resource metadataResource) throws Exception;

    @Override
    public String getMetadata(final EntityDescriptor entityDescriptor) throws Exception {
        final var entityDescriptorElement = this.marshallerFactory
            .getMarshaller(EntityDescriptor.DEFAULT_ELEMENT_NAME).marshall(entityDescriptor);
        return SerializeSupport.nodeToString(entityDescriptorElement);
    }

    @Override
    public EntityDescriptor buildEntityDescriptor() {
        final var builder = (SAMLObjectBuilder<EntityDescriptor>)
            this.builderFactory.getBuilder(EntityDescriptor.DEFAULT_ELEMENT_NAME);
        final var descriptor = Objects.requireNonNull(builder).buildObject();
        descriptor.setEntityID(this.entityId);
        descriptor.setValidUntil(ZonedDateTime.now(ZoneOffset.UTC).plusYears(20).toInstant());
        descriptor.setID(SAML2Utils.generateID());
        descriptor.setExtensions(generateMetadataExtensions());
        descriptor.getRoleDescriptors().add(buildSPSSODescriptor());
        if (signMetadata) {
            signMetadata(descriptor);
        }
        return descriptor;
    }

    protected void signMetadata(final EntityDescriptor descriptor) {
        if (this.metadataSigner == null) {
            this.metadataSigner = new DefaultSAML2MetadataSigner(this.credentialProvider,
                getSignatureAlgorithms().get(0),
                getSignatureReferenceDigestMethods().get(0));
        }
        this.metadataSigner.sign(descriptor);
    }

    protected Extensions generateMetadataExtensions() {
        final var builderExt = (SAMLObjectBuilder<Extensions>)
            this.builderFactory.getBuilder(Extensions.DEFAULT_ELEMENT_NAME);

        final var extensions = builderExt.buildObject();
        extensions.getNamespaceManager().registerAttributeName(SigningMethod.TYPE_NAME);
        extensions.getNamespaceManager().registerAttributeName(DigestMethod.TYPE_NAME);

        final var signingMethodBuilder = (SAMLObjectBuilder<SigningMethod>)
            this.builderFactory.getBuilder(SigningMethod.DEFAULT_ELEMENT_NAME);

        final var filteredSignatureAlgorithms = filterSignatureAlgorithms(getSignatureAlgorithms());
        filteredSignatureAlgorithms.forEach(signingMethod -> {
            final var method = Objects.requireNonNull(signingMethodBuilder).buildObject();
            method.setAlgorithm(signingMethod);
            extensions.getUnknownXMLObjects().add(method);
        });

        final var digestMethodBuilder = (SAMLObjectBuilder<DigestMethod>)
            this.builderFactory.getBuilder(DigestMethod.DEFAULT_ELEMENT_NAME);

        final var filteredSignatureReferenceDigestMethods = filterSignatureAlgorithms(getSignatureReferenceDigestMethods());
        filteredSignatureReferenceDigestMethods.forEach(digestMethod -> {
            final var method = Objects.requireNonNull(digestMethodBuilder).buildObject();
            method.setAlgorithm(digestMethod);
            extensions.getUnknownXMLObjects().add(method);
        });

        return extensions;
    }

    protected SPSSODescriptor buildSPSSODescriptor() {
        final var builder = (SAMLObjectBuilder<SPSSODescriptor>)
            this.builderFactory.getBuilder(SPSSODescriptor.DEFAULT_ELEMENT_NAME);
        final var spDescriptor = Objects.requireNonNull(builder).buildObject();

        spDescriptor.setAuthnRequestsSigned(this.authnRequestSigned);
        spDescriptor.setWantAssertionsSigned(this.wantAssertionSigned);
        supportedProtocols.forEach(spDescriptor::addSupportedProtocol);

        final var builderExt = (SAMLObjectBuilder<Extensions>)
            this.builderFactory.getBuilder(Extensions.DEFAULT_ELEMENT_NAME);

        final var extensions = Objects.requireNonNull(builderExt).buildObject();
        extensions.getNamespaceManager().registerAttributeName(RequestInitiator.DEFAULT_ELEMENT_NAME);

        final var builderReq = (SAMLObjectBuilder<RequestInitiator>)
            this.builderFactory.getBuilder(RequestInitiator.DEFAULT_ELEMENT_NAME);

        final var requestInitiator = Objects.requireNonNull(builderReq).buildObject();
        requestInitiator.setLocation(this.requestInitiatorLocation);
        requestInitiator.setBinding(RequestInitiator.DEFAULT_ELEMENT_NAME.getNamespaceURI());

        extensions.getUnknownXMLObjects().add(requestInitiator);
        spDescriptor.setExtensions(extensions);

        spDescriptor.getNameIDFormats().addAll(buildNameIDFormat());

        var index = 0;
        spDescriptor.getAssertionConsumerServices()
            .add(getAssertionConsumerService(responseBindingType, index++, this.defaultACSIndex == index));
        spDescriptor.getSingleLogoutServices().add(getSingleLogoutService(SAMLConstants.SAML2_POST_BINDING_URI));
        spDescriptor.getSingleLogoutServices().add(getSingleLogoutService(SAMLConstants.SAML2_POST_SIMPLE_SIGN_BINDING_URI));
        spDescriptor.getSingleLogoutServices().add(getSingleLogoutService(SAMLConstants.SAML2_REDIRECT_BINDING_URI));
        spDescriptor.getSingleLogoutServices().add(getSingleLogoutService(SAMLConstants.SAML2_SOAP11_BINDING_URI));

        if (credentialProvider != null) {
            spDescriptor.getKeyDescriptors().add(getKeyDescriptor(UsageType.SIGNING, this.credentialProvider.getKeyInfo()));
            spDescriptor.getKeyDescriptors().add(getKeyDescriptor(UsageType.ENCRYPTION, this.credentialProvider.getKeyInfo()));
        }

        if (!requestedAttributes.isEmpty()) {
            final var attrServiceBuilder =
                (SAMLObjectBuilder<AttributeConsumingService>) this.builderFactory
                    .getBuilder(AttributeConsumingService.DEFAULT_ELEMENT_NAME);

            final var attributeService =
                attrServiceBuilder.buildObject(AttributeConsumingService.DEFAULT_ELEMENT_NAME);
            for (final var attr : this.requestedAttributes) {
                final var attrBuilder = (SAMLObjectBuilder<RequestedAttribute>) this.builderFactory
                    .getBuilder(RequestedAttribute.DEFAULT_ELEMENT_NAME);
                final var requestAttribute = attrBuilder.buildObject(RequestedAttribute.DEFAULT_ELEMENT_NAME);
                requestAttribute.setIsRequired(attr.isRequired());
                requestAttribute.setName(attr.getName());
                requestAttribute.setFriendlyName(attr.getFriendlyName());
                requestAttribute.setNameFormat(attr.getNameFormat());

                attributeService.getRequestedAttributes().add(requestAttribute);

                if (StringUtils.isNotBlank(attr.getServiceName())) {
                    final var serviceBuilder = (SAMLObjectBuilder<ServiceName>)
                        this.builderFactory.getBuilder(ServiceName.DEFAULT_ELEMENT_NAME);
                    final var serviceName = Objects.requireNonNull(serviceBuilder).buildObject();
                    serviceName.setValue(attr.getServiceName());
                    if (StringUtils.isNotBlank(attr.getServiceLang())) {
                        serviceName.setXMLLang(attr.getServiceLang());
                    }
                    attributeService.getNames().add(serviceName);
                }
            }

            spDescriptor.getAttributeConsumingServices().add(attributeService);
        }

        final var contactPersonBuilder =
            (SAMLObjectBuilder<ContactPerson>) this.builderFactory
                .getBuilder(ContactPerson.DEFAULT_ELEMENT_NAME);
        this.contactPersons.forEach(p -> {
            final var person = Objects.requireNonNull(contactPersonBuilder).buildObject();
            switch (p.getType().toLowerCase()) {
                case "technical":
                    person.setType(ContactPersonTypeEnumeration.TECHNICAL);
                    break;
                case "administrative":
                    person.setType(ContactPersonTypeEnumeration.ADMINISTRATIVE);
                    break;
                case "billing":
                    person.setType(ContactPersonTypeEnumeration.BILLING);
                    break;
                case "support":
                    person.setType(ContactPersonTypeEnumeration.SUPPORT);
                    break;
                default:
                    person.setType(ContactPersonTypeEnumeration.OTHER);
                    break;
            }

            if (StringUtils.isNotBlank(p.getSurname())) {
                final var surnameBuilder =
                    (SAMLObjectBuilder<SurName>) this.builderFactory
                        .getBuilder(SurName.DEFAULT_ELEMENT_NAME);
                final var surName = Objects.requireNonNull(surnameBuilder).buildObject();
                surName.setValue(p.getSurname());
                person.setSurName(surName);
            }

            if (StringUtils.isNotBlank(p.getGivenName())) {
                final var givenNameBuilder =
                    (SAMLObjectBuilder<GivenName>) this.builderFactory
                        .getBuilder(GivenName.DEFAULT_ELEMENT_NAME);
                final var givenName = Objects.requireNonNull(givenNameBuilder).buildObject();
                givenName.setValue(p.getGivenName());
                person.setGivenName(givenName);
            }

            if (StringUtils.isNotBlank(p.getCompanyName())) {
                final var companyBuilder =
                    (SAMLObjectBuilder<Company>) this.builderFactory
                        .getBuilder(Company.DEFAULT_ELEMENT_NAME);
                final var company = Objects.requireNonNull(companyBuilder).buildObject();
                company.setValue(p.getCompanyName());
                person.setCompany(company);
            }

            if (!p.getEmailAddresses().isEmpty()) {
                final var emailBuilder =
                    (SAMLObjectBuilder<EmailAddress>) this.builderFactory
                        .getBuilder(EmailAddress.DEFAULT_ELEMENT_NAME);
                p.getEmailAddresses().forEach(email -> {
                    final var emailAddr = Objects.requireNonNull(emailBuilder).buildObject();
                    emailAddr.setURI(email);
                    person.getEmailAddresses().add(emailAddr);
                });
            }

            if (!p.getTelephoneNumbers().isEmpty()) {
                final var phoneBuilder =
                    (SAMLObjectBuilder<TelephoneNumber>) this.builderFactory
                        .getBuilder(TelephoneNumber.DEFAULT_ELEMENT_NAME);
                p.getTelephoneNumbers().forEach(ph -> {
                    final var phone = Objects.requireNonNull(phoneBuilder).buildObject();
                    phone.setValue(ph);
                    person.getTelephoneNumbers().add(phone);
                });
            }

            spDescriptor.getContactPersons().add(person);
        });

        if (!metadataUIInfos.isEmpty()) {
            final var uiInfoBuilder =
                (SAMLObjectBuilder<UIInfo>) this.builderFactory
                    .getBuilder(UIInfo.DEFAULT_ELEMENT_NAME);

            final var uiInfo = uiInfoBuilder.buildObject();

            metadataUIInfos.forEach(info -> {

                info.getDescriptions().forEach(desc -> {
                    final var uiBuilder =
                        (SAMLObjectBuilder<Description>) this.builderFactory
                            .getBuilder(Description.DEFAULT_ELEMENT_NAME);
                    final var description = uiBuilder.buildObject();
                    description.setValue(desc);
                    uiInfo.getDescriptions().add(description);
                });

                info.getDisplayNames().forEach(name -> {
                    final var uiBuilder =
                        (SAMLObjectBuilder<DisplayName>) this.builderFactory
                            .getBuilder(DisplayName.DEFAULT_ELEMENT_NAME);
                    final var displayName = uiBuilder.buildObject();
                    displayName.setValue(name);
                    uiInfo.getDisplayNames().add(displayName);
                });

                info.getInformationUrls().forEach(url -> {
                    final var uiBuilder =
                        (SAMLObjectBuilder<InformationURL>) this.builderFactory
                            .getBuilder(InformationURL.DEFAULT_ELEMENT_NAME);
                    final var informationURL = uiBuilder.buildObject();
                    informationURL.setURI(url);
                    uiInfo.getInformationURLs().add(informationURL);
                });

                info.getPrivacyUrls().forEach(privacy -> {
                    final var uiBuilder =
                        (SAMLObjectBuilder<PrivacyStatementURL>) this.builderFactory
                            .getBuilder(PrivacyStatementURL.DEFAULT_ELEMENT_NAME);
                    final var privacyStatementURL = uiBuilder.buildObject();
                    privacyStatementURL.setURI(privacy);
                    uiInfo.getPrivacyStatementURLs().add(privacyStatementURL);
                });

                info.getKeywords().forEach(kword -> {
                    final var uiBuilder =
                        (SAMLObjectBuilder<Keywords>) this.builderFactory
                            .getBuilder(Keywords.DEFAULT_ELEMENT_NAME);
                    final var keyword = uiBuilder.buildObject();
                    keyword.setKeywords(new ArrayList<>(org.springframework.util.StringUtils.commaDelimitedListToSet(kword)));
                    uiInfo.getKeywords().add(keyword);
                });

                info.getLogos().forEach(lg -> {
                    final var uiBuilder =
                        (SAMLObjectBuilder<Logo>) this.builderFactory
                            .getBuilder(Logo.DEFAULT_ELEMENT_NAME);
                    final var logo = uiBuilder.buildObject();
                    logo.setURI(lg.getUrl());
                    logo.setHeight(lg.getHeight());
                    logo.setWidth(lg.getWidth());
                    uiInfo.getLogos().add(logo);
                });

            });

            extensions.getUnknownXMLObjects().add(uiInfo);
        }

        return spDescriptor;

    }

    protected Collection<NameIDFormat> buildNameIDFormat() {

        final var builder = (SAMLObjectBuilder<NameIDFormat>) this.builderFactory
            .getBuilder(NameIDFormat.DEFAULT_ELEMENT_NAME);
        final Collection<NameIDFormat> formats = new ArrayList<>();

        if (this.nameIdPolicyFormat != null) {
            final var nameID = Objects.requireNonNull(builder).buildObject();
            nameID.setURI(this.nameIdPolicyFormat);
            formats.add(nameID);
        } else {
            final var transientNameID = Objects.requireNonNull(builder).buildObject();
            transientNameID.setURI(NameIDType.TRANSIENT);
            formats.add(transientNameID);
            final var persistentNameID = builder.buildObject();
            persistentNameID.setURI(NameIDType.PERSISTENT);
            formats.add(persistentNameID);
            final var emailNameID = builder.buildObject();
            emailNameID.setURI(NameIDType.EMAIL);
            formats.add(emailNameID);
            final var unspecNameID = builder.buildObject();
            unspecNameID.setURI(NameIDType.UNSPECIFIED);
            formats.add(unspecNameID);
        }
        return formats;
    }

    protected AssertionConsumerService getAssertionConsumerService(final String binding, final int index,
                                                                   final boolean isDefault) {
        final var builder = (SAMLObjectBuilder<AssertionConsumerService>) this.builderFactory
            .getBuilder(AssertionConsumerService.DEFAULT_ELEMENT_NAME);
        final var consumer = Objects.requireNonNull(builder).buildObject();
        consumer.setLocation(this.assertionConsumerServiceUrl);
        consumer.setBinding(binding);
        if (isDefault) {
            consumer.setIsDefault(true);
        }
        consumer.setIndex(index);
        return consumer;
    }

    protected SingleLogoutService getSingleLogoutService(final String binding) {
        final var builder = (SAMLObjectBuilder<SingleLogoutService>) this.builderFactory
            .getBuilder(SingleLogoutService.DEFAULT_ELEMENT_NAME);
        final var logoutService = Objects.requireNonNull(builder).buildObject();
        logoutService.setLocation(this.singleLogoutServiceUrl);
        logoutService.setBinding(binding);
        return logoutService;
    }

    protected KeyDescriptor getKeyDescriptor(final UsageType type, final KeyInfo key) {
        final var builder = (SAMLObjectBuilder<KeyDescriptor>)
            Configuration.getBuilderFactory()
                .getBuilder(KeyDescriptor.DEFAULT_ELEMENT_NAME);
        final var descriptor = Objects.requireNonNull(builder).buildObject();
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

    public boolean isSignMetadata() {
        return signMetadata;
    }

    public void setSignMetadata(final boolean signMetadata) {
        this.signMetadata = signMetadata;
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

    public void setResponseBindingType(final String responseBindingType) {
        this.responseBindingType = responseBindingType;
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

    public List<SAML2ServiceProviderRequestedAttribute> getRequestedAttributes() {
        return requestedAttributes;
    }

    public void setRequestedAttributes(final List<SAML2ServiceProviderRequestedAttribute> requestedAttributes) {
        this.requestedAttributes = requestedAttributes;
    }

    public List<String> getBlackListedSignatureSigningAlgorithms() {
        if (blackListedSignatureSigningAlgorithms == null) {
            this.blackListedSignatureSigningAlgorithms =
                new ArrayList<>(defaultSignatureSigningConfiguration.getBlacklistedAlgorithms());
        }

        return blackListedSignatureSigningAlgorithms;
    }

    public void setBlackListedSignatureSigningAlgorithms(final List<String> blackListedSignatureSigningAlgorithms) {
        this.blackListedSignatureSigningAlgorithms = blackListedSignatureSigningAlgorithms;
    }

    public List<String> getSignatureAlgorithms() {
        if (signatureAlgorithms == null) {
            this.signatureAlgorithms = new ArrayList<>(defaultSignatureSigningConfiguration.getSignatureAlgorithms());
        }

        return signatureAlgorithms;
    }

    public void setSignatureAlgorithms(final List<String> signatureAlgorithms) {
        this.signatureAlgorithms = signatureAlgorithms;
    }

    public List<String> getSignatureReferenceDigestMethods() {
        if (signatureReferenceDigestMethods == null) {
            this.signatureReferenceDigestMethods = defaultSignatureSigningConfiguration.getSignatureReferenceDigestMethods();
        }
        return signatureReferenceDigestMethods;
    }

    public void setSignatureReferenceDigestMethods(final List<String> signatureReferenceDigestMethods) {
        this.signatureReferenceDigestMethods = signatureReferenceDigestMethods;
    }

    public List<String> getSupportedProtocols() {
        return supportedProtocols;
    }

    public void setSupportedProtocols(final List<String> supportedProtocols) {
        this.supportedProtocols = supportedProtocols;
    }

    public List<SAML2MetadataContactPerson> getContactPersons() {
        return contactPersons;
    }

    public void setContactPersons(final List<SAML2MetadataContactPerson> contactPersons) {
        this.contactPersons = contactPersons;
    }

    public List<SAML2MetadataUIInfo> getMetadataUIInfos() {
        return metadataUIInfos;
    }

    public void setMetadataUIInfos(final List<SAML2MetadataUIInfo> metadataUIInfos) {
        this.metadataUIInfos = metadataUIInfos;
    }

    private List<String> filterForRuntimeSupportedAlgorithms(final List<String> algorithms) {
        final List<String> filteredAlgorithms = new ArrayList<>(algorithms);
        return filteredAlgorithms
            .stream()
            .filter(uri -> Objects.requireNonNull(globalAlgorithmRegistry).isRuntimeSupported(uri))
            .collect(Collectors.toList());
    }

    private List<String> filterSignatureAlgorithms(final List<String> algorithms) {
        final var filteredAlgorithms = filterForRuntimeSupportedAlgorithms(algorithms);
        if (blackListedSignatureSigningAlgorithms != null) {
            filteredAlgorithms.removeAll(this.blackListedSignatureSigningAlgorithms);
        }
        return filteredAlgorithms;
    }

    public SAML2MetadataSigner getMetadataSigner() {
        return metadataSigner;
    }

    public void setMetadataSigner(final SAML2MetadataSigner metadataSigner) {
        this.metadataSigner = metadataSigner;
    }
}
