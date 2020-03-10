package org.pac4j.saml.metadata;

import net.shibboleth.utilities.java.support.xml.SerializeSupport;
import org.apache.commons.lang.StringUtils;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.io.MarshallerFactory;
import org.opensaml.core.xml.io.MarshallingException;
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
import org.opensaml.saml.metadata.resolver.impl.FilesystemMetadataResolver;
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
import org.opensaml.saml.saml2.metadata.SingleLogoutService;
import org.opensaml.saml.saml2.metadata.SurName;
import org.opensaml.saml.saml2.metadata.TelephoneNumber;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.UsageType;
import org.opensaml.xmlsec.SignatureSigningConfiguration;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.algorithm.AlgorithmRegistry;
import org.opensaml.xmlsec.algorithm.AlgorithmSupport;
import org.opensaml.xmlsec.config.impl.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignatureSupport;
import org.pac4j.saml.crypto.CredentialProvider;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.util.Configuration;
import org.pac4j.saml.util.SAML2Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.w3c.dom.Element;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
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

    protected String responseBindingType = SAMLConstants.SAML2_POST_BINDING_URI;

    protected String singleLogoutServiceUrl;

    protected boolean authnRequestSigned = false;

    protected boolean wantAssertionSigned = true;
    
    protected boolean signMetadata = false;

    protected int defaultACSIndex = 0;

    protected String requestInitiatorLocation = null;

    protected String nameIdPolicyFormat = null;

    protected List<SAML2ServiceProvicerRequestedAttribute> requestedAttributes = new ArrayList<>();

    protected SignatureSigningConfiguration defaultSignatureSigningConfiguration =
            DefaultSecurityConfigurationBootstrap.buildDefaultSignatureSigningConfiguration();

    protected List<String> blackListedSignatureSigningAlgorithms = null;

    protected List<String> signatureAlgorithms = null;

    protected List<String> signatureReferenceDigestMethods = null;

    private List<SAML2MetadataContactPerson> contactPersons = new ArrayList<>();

    private List<SAML2MetadataUIInfo> metadataUIInfos = new ArrayList<>();

    private List<String> supportedProtocols = new ArrayList<>(Arrays.asList(SAMLConstants.SAML20P_NS,
        SAMLConstants.SAML10P_NS, SAMLConstants.SAML11P_NS));

    @Override
    public MetadataResolver buildMetadataResolver(final Resource metadataResource) throws Exception {
        final AbstractBatchMetadataResolver resolver;
        if (metadataResource != null) {
            resolver = createMetadataResolver(metadataResource);
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
    
    protected AbstractBatchMetadataResolver createMetadataResolver(final Resource metadataResource) throws Exception {
        return new FilesystemMetadataResolver(metadataResource.getFile());
    }

    @Override
    public String getMetadata(final EntityDescriptor entityDescriptor) throws Exception {
        final Element entityDescriptorElement = this.marshallerFactory
            .getMarshaller(EntityDescriptor.DEFAULT_ELEMENT_NAME).marshall(entityDescriptor);
        return SerializeSupport.nodeToString(entityDescriptorElement);
    }

    @Override
    public EntityDescriptor buildEntityDescriptor() {
        final SAMLObjectBuilder<EntityDescriptor> builder = (SAMLObjectBuilder<EntityDescriptor>)
            this.builderFactory.getBuilder(EntityDescriptor.DEFAULT_ELEMENT_NAME);
        final EntityDescriptor descriptor = Objects.requireNonNull(builder).buildObject();
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
        final SignatureSigningParameters signingParameters = new SignatureSigningParameters();
        signingParameters.setKeyInfoGenerator(credentialProvider.getKeyInfoGenerator());
        signingParameters.setSigningCredential(credentialProvider.getCredential());
        signingParameters.setSignatureAlgorithm(getSignatureAlgorithms().get(0));
        signingParameters.setSignatureReferenceDigestMethod(getSignatureReferenceDigestMethods().get(0));
        signingParameters.setSignatureCanonicalizationAlgorithm(        
                SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);    

        try {
            SignatureSupport.signObject(descriptor, signingParameters);
        } catch (final SecurityException | MarshallingException | SignatureException e) {
            throw new SAMLException(e.getMessage(), e);
        }
    }

    protected Extensions generateMetadataExtensions() {
        final SAMLObjectBuilder<Extensions> builderExt = (SAMLObjectBuilder<Extensions>)
            this.builderFactory.getBuilder(Extensions.DEFAULT_ELEMENT_NAME);

        final Extensions extensions = builderExt.buildObject();
        extensions.getNamespaceManager().registerAttributeName(SigningMethod.TYPE_NAME);
        extensions.getNamespaceManager().registerAttributeName(DigestMethod.TYPE_NAME);

        final SAMLObjectBuilder<SigningMethod> signingMethodBuilder = (SAMLObjectBuilder<SigningMethod>)
                this.builderFactory.getBuilder(SigningMethod.DEFAULT_ELEMENT_NAME);

        final List<String> filteredSignatureAlgorithms = filterSignatureAlgorithms(getSignatureAlgorithms());
        filteredSignatureAlgorithms.forEach(signingMethod -> {
            final SigningMethod method = Objects.requireNonNull(signingMethodBuilder).buildObject();
            method.setAlgorithm(signingMethod);
            extensions.getUnknownXMLObjects().add(method);
        });

        final SAMLObjectBuilder<DigestMethod> digestMethodBuilder = (SAMLObjectBuilder<DigestMethod>)
            this.builderFactory.getBuilder(DigestMethod.DEFAULT_ELEMENT_NAME);

        final List<String> filteredSignatureReferenceDigestMethods = filterSignatureAlgorithms(getSignatureReferenceDigestMethods());
        filteredSignatureReferenceDigestMethods.forEach(digestMethod -> {
            final DigestMethod method = Objects.requireNonNull(digestMethodBuilder).buildObject();
            method.setAlgorithm(digestMethod);
            extensions.getUnknownXMLObjects().add(method);
        });

        return extensions;
    }

    protected SPSSODescriptor buildSPSSODescriptor() {
        final SAMLObjectBuilder<SPSSODescriptor> builder = (SAMLObjectBuilder<SPSSODescriptor>)
            this.builderFactory.getBuilder(SPSSODescriptor.DEFAULT_ELEMENT_NAME);
        final SPSSODescriptor spDescriptor = Objects.requireNonNull(builder).buildObject();

        spDescriptor.setAuthnRequestsSigned(this.authnRequestSigned);
        spDescriptor.setWantAssertionsSigned(this.wantAssertionSigned);
        supportedProtocols.forEach(spDescriptor::addSupportedProtocol);

        final SAMLObjectBuilder<Extensions> builderExt = (SAMLObjectBuilder<Extensions>)
            this.builderFactory.getBuilder(Extensions.DEFAULT_ELEMENT_NAME);

        final Extensions extensions = Objects.requireNonNull(builderExt).buildObject();
        extensions.getNamespaceManager().registerAttributeName(RequestInitiator.DEFAULT_ELEMENT_NAME);

        final SAMLObjectBuilder<RequestInitiator> builderReq = (SAMLObjectBuilder<RequestInitiator>)
            this.builderFactory.getBuilder(RequestInitiator.DEFAULT_ELEMENT_NAME);

        final RequestInitiator requestInitiator = Objects.requireNonNull(builderReq).buildObject();
        requestInitiator.setLocation(this.requestInitiatorLocation);
        requestInitiator.setBinding(RequestInitiator.DEFAULT_ELEMENT_NAME.getNamespaceURI());

        extensions.getUnknownXMLObjects().add(requestInitiator);
        spDescriptor.setExtensions(extensions);

        spDescriptor.getNameIDFormats().addAll(buildNameIDFormat());

        int index = 0;
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
                
                attributeService.getRequestedAttributes().add(requestAttribute);
            }
            spDescriptor.getAttributeConsumingServices().add(attributeService);
        }

        final SAMLObjectBuilder<ContactPerson> contactPersonBuilder =
            (SAMLObjectBuilder<ContactPerson>) this.builderFactory
                .getBuilder(ContactPerson.DEFAULT_ELEMENT_NAME);
        this.contactPersons.forEach(p -> {
            final ContactPerson person = Objects.requireNonNull(contactPersonBuilder).buildObject();
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
                final SAMLObjectBuilder<SurName> surnameBuilder =
                    (SAMLObjectBuilder<SurName>) this.builderFactory
                        .getBuilder(SurName.DEFAULT_ELEMENT_NAME);
                final SurName surName = Objects.requireNonNull(surnameBuilder).buildObject();
                surName.setValue(p.getSurname());
                person.setSurName(surName);
            }

            if (StringUtils.isNotBlank(p.getGivenName())) {
                final SAMLObjectBuilder<GivenName> givenNameBuilder =
                    (SAMLObjectBuilder<GivenName>) this.builderFactory
                        .getBuilder(GivenName.DEFAULT_ELEMENT_NAME);
                final GivenName givenName = Objects.requireNonNull(givenNameBuilder).buildObject();
                givenName.setValue(p.getGivenName());
                person.setGivenName(givenName);
            }

            if (StringUtils.isNotBlank(p.getCompanyName())) {
                final SAMLObjectBuilder<Company> companyBuilder =
                    (SAMLObjectBuilder<Company>) this.builderFactory
                        .getBuilder(Company.DEFAULT_ELEMENT_NAME);
                final Company company = Objects.requireNonNull(companyBuilder).buildObject();
                company.setValue(p.getCompanyName());
                person.setCompany(company);
            }

            if (!p.getEmailAddresses().isEmpty()) {
                final SAMLObjectBuilder<EmailAddress> emailBuilder =
                    (SAMLObjectBuilder<EmailAddress>) this.builderFactory
                        .getBuilder(EmailAddress.DEFAULT_ELEMENT_NAME);
                p.getEmailAddresses().forEach(email -> {
                    final EmailAddress emailAddr = Objects.requireNonNull(emailBuilder).buildObject();
                    emailAddr.setURI(email);
                    person.getEmailAddresses().add(emailAddr);
                });
            }

            if (!p.getTelephoneNumbers().isEmpty()) {
                final SAMLObjectBuilder<TelephoneNumber> phoneBuilder =
                    (SAMLObjectBuilder<TelephoneNumber>) this.builderFactory
                        .getBuilder(TelephoneNumber.DEFAULT_ELEMENT_NAME);
                p.getTelephoneNumbers().forEach(ph -> {
                    final TelephoneNumber phone = Objects.requireNonNull(phoneBuilder).buildObject();
                    phone.setValue(ph);
                    person.getTelephoneNumbers().add(phone);
                });
            }
            
            spDescriptor.getContactPersons().add(person);
        });

        if (!metadataUIInfos.isEmpty()) {
            final SAMLObjectBuilder<UIInfo> uiInfoBuilder =
                (SAMLObjectBuilder<UIInfo>) this.builderFactory
                    .getBuilder(UIInfo.DEFAULT_ELEMENT_NAME);

            final UIInfo uiInfo = uiInfoBuilder.buildObject();

            metadataUIInfos.forEach(info -> {

                info.getDescriptions().forEach(desc -> {
                    final SAMLObjectBuilder<Description> uiBuilder =
                        (SAMLObjectBuilder<Description>) this.builderFactory
                            .getBuilder(Description.DEFAULT_ELEMENT_NAME);
                    final Description description = uiBuilder.buildObject();
                    description.setValue(desc);
                    uiInfo.getDescriptions().add(description);
                });

                info.getDisplayNames().forEach(name -> {
                    final SAMLObjectBuilder<DisplayName> uiBuilder =
                        (SAMLObjectBuilder<DisplayName>) this.builderFactory
                            .getBuilder(DisplayName.DEFAULT_ELEMENT_NAME);
                    final DisplayName displayName = uiBuilder.buildObject();
                    displayName.setValue(name);
                    uiInfo.getDisplayNames().add(displayName);
                });

                info.getInformationUrls().forEach(url -> {
                    final SAMLObjectBuilder<InformationURL> uiBuilder =
                        (SAMLObjectBuilder<InformationURL>) this.builderFactory
                            .getBuilder(InformationURL.DEFAULT_ELEMENT_NAME);
                    final InformationURL informationURL = uiBuilder.buildObject();
                    informationURL.setURI(url);
                    uiInfo.getInformationURLs().add(informationURL);
                });

                info.getPrivacyUrls().forEach(privacy -> {
                    final SAMLObjectBuilder<PrivacyStatementURL> uiBuilder =
                        (SAMLObjectBuilder<PrivacyStatementURL>) this.builderFactory
                            .getBuilder(PrivacyStatementURL.DEFAULT_ELEMENT_NAME);
                    final PrivacyStatementURL privacyStatementURL = uiBuilder.buildObject();
                    privacyStatementURL.setURI(privacy);
                    uiInfo.getPrivacyStatementURLs().add(privacyStatementURL);
                });

                info.getKeywords().forEach(kword -> {
                    final SAMLObjectBuilder<Keywords> uiBuilder =
                        (SAMLObjectBuilder<Keywords>) this.builderFactory
                            .getBuilder(Keywords.DEFAULT_ELEMENT_NAME);
                    final Keywords keyword = uiBuilder.buildObject();
                    keyword.setKeywords(new ArrayList<>(org.springframework.util.StringUtils.commaDelimitedListToSet(kword)));
                    uiInfo.getKeywords().add(keyword);
                });

                info.getLogos().forEach(lg -> {
                    final SAMLObjectBuilder<Logo> uiBuilder =
                        (SAMLObjectBuilder<Logo>) this.builderFactory
                            .getBuilder(Logo.DEFAULT_ELEMENT_NAME);
                    final Logo logo = uiBuilder.buildObject();
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

        final SAMLObjectBuilder<NameIDFormat> builder = (SAMLObjectBuilder<NameIDFormat>) this.builderFactory
            .getBuilder(NameIDFormat.DEFAULT_ELEMENT_NAME);
        final Collection<NameIDFormat> formats = new ArrayList<>();

        if (this.nameIdPolicyFormat != null) {
            final NameIDFormat nameID = Objects.requireNonNull(builder).buildObject();
            nameID.setURI(this.nameIdPolicyFormat);
            formats.add(nameID);
        } else {
            final NameIDFormat transientNameID = Objects.requireNonNull(builder).buildObject();
            transientNameID.setURI(NameIDType.TRANSIENT);
            formats.add(transientNameID);
            final NameIDFormat persistentNameID = builder.buildObject();
            persistentNameID.setURI(NameIDType.PERSISTENT);
            formats.add(persistentNameID);
            final NameIDFormat emailNameID = builder.buildObject();
            emailNameID.setURI(NameIDType.EMAIL);
            formats.add(emailNameID);
            final NameIDFormat unspecNameID = builder.buildObject();
            unspecNameID.setURI(NameIDType.UNSPECIFIED);
            formats.add(unspecNameID);
        }
        return formats;
    }

    protected AssertionConsumerService getAssertionConsumerService(final String binding, final int index,
                                                                         final boolean isDefault) {
        final SAMLObjectBuilder<AssertionConsumerService> builder = (SAMLObjectBuilder<AssertionConsumerService>) this.builderFactory
            .getBuilder(AssertionConsumerService.DEFAULT_ELEMENT_NAME);
        final AssertionConsumerService consumer = Objects.requireNonNull(builder).buildObject();
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
        final SingleLogoutService logoutService = Objects.requireNonNull(builder).buildObject();
        logoutService.setLocation(this.singleLogoutServiceUrl);
        logoutService.setBinding(binding);
        return logoutService;
    }

    protected KeyDescriptor getKeyDescriptor(final UsageType type, final KeyInfo key) {
        final SAMLObjectBuilder<KeyDescriptor> builder = (SAMLObjectBuilder<KeyDescriptor>)
            Configuration.getBuilderFactory()
                .getBuilder(KeyDescriptor.DEFAULT_ELEMENT_NAME);
        final KeyDescriptor descriptor = Objects.requireNonNull(builder).buildObject();
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

    public void setBlackListedSignatureSigningAlgorithms(final List<String> blackListedSignatureSigningAlgorithms) {
        this.blackListedSignatureSigningAlgorithms = blackListedSignatureSigningAlgorithms;
    }

    public List<String> getSignatureAlgorithms() {
        if (signatureAlgorithms == null) {
            this.signatureAlgorithms = defaultSignatureSigningConfiguration.getSignatureAlgorithms();
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
        return filteredAlgorithms.stream().filter(uri -> globalAlgorithmRegistry.isRuntimeSupported(uri)).collect(Collectors.toList());
    }

    private List<String> filterSignatureAlgorithms(final List<String> algorithms) {
        final List<String> filteredAlgorithms = filterForRuntimeSupportedAlgorithms(algorithms);
        this.signatureAlgorithms.removeAll(this.blackListedSignatureSigningAlgorithms);
        return filteredAlgorithms;
    }
}
