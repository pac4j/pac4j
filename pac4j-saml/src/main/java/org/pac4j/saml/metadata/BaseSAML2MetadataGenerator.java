package org.pac4j.saml.metadata;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import net.shibboleth.shared.xml.SerializeSupport;
import org.apache.commons.lang3.StringUtils;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.io.MarshallerFactory;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.ext.saml2alg.DigestMethod;
import org.opensaml.saml.ext.saml2alg.SigningMethod;
import org.opensaml.saml.ext.saml2mdreqinit.RequestInitiator;
import org.opensaml.saml.ext.saml2mdui.*;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.AbstractMetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.DOMMetadataResolver;
import org.opensaml.saml.saml2.core.NameIDType;
import org.opensaml.saml.saml2.metadata.*;
import org.opensaml.security.credential.UsageType;
import org.opensaml.xmlsec.SignatureSigningConfiguration;
import org.opensaml.xmlsec.algorithm.AlgorithmRegistry;
import org.opensaml.xmlsec.algorithm.AlgorithmSupport;
import org.opensaml.xmlsec.config.impl.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.crypto.CredentialProvider;
import org.pac4j.saml.util.Configuration;
import org.pac4j.saml.util.SAML2Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Generates metadata object with standard values and overridden user defined values.
 *
 * @author Misagh Moayyed
 * @since 4.0.1
 */
@Getter
@Setter
public abstract class BaseSAML2MetadataGenerator implements SAML2MetadataGenerator {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

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

    /** {@inheritDoc} */
    @Override
    public MetadataResolver buildMetadataResolver() throws Exception {
        var resolver = createMetadataResolver();
        if (resolver == null) {
            val md = buildEntityDescriptor();
            val entityDescriptorElement = this.marshallerFactory.getMarshaller(md).marshall(md);
            resolver = new DOMMetadataResolver(entityDescriptorElement);
        }

        resolver.setRequireValidMetadata(true);
        resolver.setFailFastInitialization(true);
        resolver.setId(resolver.getClass().getCanonicalName());
        resolver.setParserPool(Configuration.getParserPool());
        resolver.initialize();
        return resolver;
    }

    /**
     * <p>createMetadataResolver.</p>
     *
     * @return a {@link org.opensaml.saml.metadata.resolver.impl.AbstractMetadataResolver} object
     * @throws java.lang.Exception if any.
     */
    protected abstract AbstractMetadataResolver createMetadataResolver() throws Exception;

    /** {@inheritDoc} */
    @Override
    public String getMetadata(final EntityDescriptor entityDescriptor) throws Exception {
        val entityDescriptorElement = this.marshallerFactory
            .getMarshaller(EntityDescriptor.DEFAULT_ELEMENT_NAME).marshall(entityDescriptor);
        return SerializeSupport.nodeToString(entityDescriptorElement);
    }

    /** {@inheritDoc} */
    @Override
    public EntityDescriptor buildEntityDescriptor() {
        val builder = (SAMLObjectBuilder<EntityDescriptor>)
            this.builderFactory.getBuilder(EntityDescriptor.DEFAULT_ELEMENT_NAME);
        val descriptor = Objects.requireNonNull(builder).buildObject();
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

    /**
     * <p>signMetadata.</p>
     *
     * @param descriptor a {@link org.opensaml.saml.saml2.metadata.EntityDescriptor} object
     */
    protected void signMetadata(final EntityDescriptor descriptor) {
        if (this.metadataSigner == null) {
            this.metadataSigner = new DefaultSAML2MetadataSigner(this.credentialProvider,
                getSignatureAlgorithms().get(0),
                getSignatureReferenceDigestMethods().get(0));
        }
        this.metadataSigner.sign(descriptor);
    }

    /**
     * <p>generateMetadataExtensions.</p>
     *
     * @return a {@link org.opensaml.saml.saml2.metadata.Extensions} object
     */
    protected Extensions generateMetadataExtensions() {
        val builderExt = (SAMLObjectBuilder<Extensions>)
            this.builderFactory.getBuilder(Extensions.DEFAULT_ELEMENT_NAME);

        val extensions = builderExt.buildObject();
        extensions.getNamespaceManager().registerAttributeName(SigningMethod.TYPE_NAME);
        extensions.getNamespaceManager().registerAttributeName(DigestMethod.TYPE_NAME);

        val signingMethodBuilder = (SAMLObjectBuilder<SigningMethod>)
            this.builderFactory.getBuilder(SigningMethod.DEFAULT_ELEMENT_NAME);

        val filteredSignatureAlgorithms = filterSignatureAlgorithms(getSignatureAlgorithms());
        filteredSignatureAlgorithms.forEach(signingMethod -> {
            val method = Objects.requireNonNull(signingMethodBuilder).buildObject();
            method.setAlgorithm(signingMethod);
            extensions.getUnknownXMLObjects().add(method);
        });

        val digestMethodBuilder = (SAMLObjectBuilder<DigestMethod>)
            this.builderFactory.getBuilder(DigestMethod.DEFAULT_ELEMENT_NAME);

        val filteredSignatureReferenceDigestMethods = filterSignatureAlgorithms(getSignatureReferenceDigestMethods());
        filteredSignatureReferenceDigestMethods.forEach(digestMethod -> {
            val method = Objects.requireNonNull(digestMethodBuilder).buildObject();
            method.setAlgorithm(digestMethod);
            extensions.getUnknownXMLObjects().add(method);
        });

        return extensions;
    }

    /**
     * <p>buildSPSSODescriptor.</p>
     *
     * @return a {@link org.opensaml.saml.saml2.metadata.SPSSODescriptor} object
     */
    protected SPSSODescriptor buildSPSSODescriptor() {
        val builder = (SAMLObjectBuilder<SPSSODescriptor>)
            this.builderFactory.getBuilder(SPSSODescriptor.DEFAULT_ELEMENT_NAME);
        val spDescriptor = Objects.requireNonNull(builder).buildObject();

        spDescriptor.setAuthnRequestsSigned(this.authnRequestSigned);
        spDescriptor.setWantAssertionsSigned(this.wantAssertionSigned);
        supportedProtocols.forEach(spDescriptor::addSupportedProtocol);

        val builderExt = (SAMLObjectBuilder<Extensions>)
            this.builderFactory.getBuilder(Extensions.DEFAULT_ELEMENT_NAME);

        val extensions = Objects.requireNonNull(builderExt).buildObject();
        extensions.getNamespaceManager().registerAttributeName(RequestInitiator.DEFAULT_ELEMENT_NAME);

        val builderReq = (SAMLObjectBuilder<RequestInitiator>)
            this.builderFactory.getBuilder(RequestInitiator.DEFAULT_ELEMENT_NAME);

        val requestInitiator = Objects.requireNonNull(builderReq).buildObject();
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
            val attrServiceBuilder =
                (SAMLObjectBuilder<AttributeConsumingService>) this.builderFactory
                    .getBuilder(AttributeConsumingService.DEFAULT_ELEMENT_NAME);

            val attributeService =
                attrServiceBuilder.buildObject(AttributeConsumingService.DEFAULT_ELEMENT_NAME);
            for (val attr : this.requestedAttributes) {
                val attrBuilder = (SAMLObjectBuilder<RequestedAttribute>) this.builderFactory
                    .getBuilder(RequestedAttribute.DEFAULT_ELEMENT_NAME);
                val requestAttribute = attrBuilder.buildObject(RequestedAttribute.DEFAULT_ELEMENT_NAME);
                requestAttribute.setIsRequired(attr.isRequired());
                requestAttribute.setName(attr.getName());
                requestAttribute.setFriendlyName(attr.getFriendlyName());
                requestAttribute.setNameFormat(attr.getNameFormat());

                attributeService.getRequestedAttributes().add(requestAttribute);

                if (StringUtils.isNotBlank(attr.getServiceName())) {
                    val serviceBuilder = (SAMLObjectBuilder<ServiceName>)
                        this.builderFactory.getBuilder(ServiceName.DEFAULT_ELEMENT_NAME);
                    val serviceName = Objects.requireNonNull(serviceBuilder).buildObject();
                    serviceName.setValue(attr.getServiceName());
                    if (StringUtils.isNotBlank(attr.getServiceLang())) {
                        serviceName.setXMLLang(attr.getServiceLang());
                    }
                    attributeService.getNames().add(serviceName);
                }
            }

            spDescriptor.getAttributeConsumingServices().add(attributeService);
        }

        val contactPersonBuilder =
            (SAMLObjectBuilder<ContactPerson>) this.builderFactory
                .getBuilder(ContactPerson.DEFAULT_ELEMENT_NAME);
        this.contactPersons.forEach(p -> {
            val person = Objects.requireNonNull(contactPersonBuilder).buildObject();
            switch (p.getType().toLowerCase()) {
                case "technical" -> person.setType(ContactPersonTypeEnumeration.TECHNICAL);
                case "administrative" -> person.setType(ContactPersonTypeEnumeration.ADMINISTRATIVE);
                case "billing" -> person.setType(ContactPersonTypeEnumeration.BILLING);
                case "support" -> person.setType(ContactPersonTypeEnumeration.SUPPORT);
                default -> person.setType(ContactPersonTypeEnumeration.OTHER);
            }

            if (CommonHelper.isNotBlank(p.getSurname())) {
                val surnameBuilder =
                    (SAMLObjectBuilder<SurName>) this.builderFactory
                        .getBuilder(SurName.DEFAULT_ELEMENT_NAME);
                val surName = Objects.requireNonNull(surnameBuilder).buildObject();
                surName.setValue(p.getSurname());
                person.setSurName(surName);
            }

            if (CommonHelper.isNotBlank(p.getGivenName())) {
                val givenNameBuilder =
                    (SAMLObjectBuilder<GivenName>) this.builderFactory
                        .getBuilder(GivenName.DEFAULT_ELEMENT_NAME);
                val givenName = Objects.requireNonNull(givenNameBuilder).buildObject();
                givenName.setValue(p.getGivenName());
                person.setGivenName(givenName);
            }

            if (StringUtils.isNotBlank(p.getCompanyName())) {
                val companyBuilder =
                    (SAMLObjectBuilder<Company>) this.builderFactory
                        .getBuilder(Company.DEFAULT_ELEMENT_NAME);
                val company = Objects.requireNonNull(companyBuilder).buildObject();
                company.setValue(p.getCompanyName());
                person.setCompany(company);
            }

            if (!p.getEmailAddresses().isEmpty()) {
                val emailBuilder =
                    (SAMLObjectBuilder<EmailAddress>) this.builderFactory
                        .getBuilder(EmailAddress.DEFAULT_ELEMENT_NAME);
                p.getEmailAddresses().forEach(email -> {
                    val emailAddr = Objects.requireNonNull(emailBuilder).buildObject();
                    emailAddr.setURI(email);
                    person.getEmailAddresses().add(emailAddr);
                });
            }

            if (!p.getTelephoneNumbers().isEmpty()) {
                val phoneBuilder =
                    (SAMLObjectBuilder<TelephoneNumber>) this.builderFactory
                        .getBuilder(TelephoneNumber.DEFAULT_ELEMENT_NAME);
                p.getTelephoneNumbers().forEach(ph -> {
                    val phone = Objects.requireNonNull(phoneBuilder).buildObject();
                    phone.setValue(ph);
                    person.getTelephoneNumbers().add(phone);
                });
            }

            spDescriptor.getContactPersons().add(person);
        });

        if (!metadataUIInfos.isEmpty()) {
            val uiInfoBuilder =
                (SAMLObjectBuilder<UIInfo>) this.builderFactory
                    .getBuilder(UIInfo.DEFAULT_ELEMENT_NAME);

            val uiInfo = uiInfoBuilder.buildObject();

            metadataUIInfos.forEach(info -> {

                info.getDescriptions().forEach(desc -> {
                    val uiBuilder =
                        (SAMLObjectBuilder<Description>) this.builderFactory
                            .getBuilder(Description.DEFAULT_ELEMENT_NAME);
                    val description = uiBuilder.buildObject();
                    description.setValue(desc);
                    uiInfo.getDescriptions().add(description);
                });

                info.getDisplayNames().forEach(name -> {
                    val uiBuilder =
                        (SAMLObjectBuilder<DisplayName>) this.builderFactory
                            .getBuilder(DisplayName.DEFAULT_ELEMENT_NAME);
                    val displayName = uiBuilder.buildObject();
                    displayName.setValue(name);
                    uiInfo.getDisplayNames().add(displayName);
                });

                info.getInformationUrls().forEach(url -> {
                    val uiBuilder =
                        (SAMLObjectBuilder<InformationURL>) this.builderFactory
                            .getBuilder(InformationURL.DEFAULT_ELEMENT_NAME);
                    val informationURL = uiBuilder.buildObject();
                    informationURL.setURI(url);
                    uiInfo.getInformationURLs().add(informationURL);
                });

                info.getPrivacyUrls().forEach(privacy -> {
                    val uiBuilder =
                        (SAMLObjectBuilder<PrivacyStatementURL>) this.builderFactory
                            .getBuilder(PrivacyStatementURL.DEFAULT_ELEMENT_NAME);
                    val privacyStatementURL = uiBuilder.buildObject();
                    privacyStatementURL.setURI(privacy);
                    uiInfo.getPrivacyStatementURLs().add(privacyStatementURL);
                });

                info.getKeywords().forEach(kword -> {
                    val uiBuilder =
                        (SAMLObjectBuilder<Keywords>) this.builderFactory
                            .getBuilder(Keywords.DEFAULT_ELEMENT_NAME);
                    val keyword = uiBuilder.buildObject();
                    keyword.setKeywords(new ArrayList<>(org.springframework.util.StringUtils.commaDelimitedListToSet(kword)));
                    uiInfo.getKeywords().add(keyword);
                });

                info.getLogos().forEach(lg -> {
                    val uiBuilder =
                        (SAMLObjectBuilder<Logo>) this.builderFactory
                            .getBuilder(Logo.DEFAULT_ELEMENT_NAME);
                    val logo = uiBuilder.buildObject();
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

    /**
     * <p>buildNameIDFormat.</p>
     *
     * @return a {@link java.util.Collection} object
     */
    protected Collection<NameIDFormat> buildNameIDFormat() {

        val builder = (SAMLObjectBuilder<NameIDFormat>) this.builderFactory
            .getBuilder(NameIDFormat.DEFAULT_ELEMENT_NAME);
        final Collection<NameIDFormat> formats = new ArrayList<>();

        if (this.nameIdPolicyFormat != null) {
            val nameID = Objects.requireNonNull(builder).buildObject();
            nameID.setURI(this.nameIdPolicyFormat);
            formats.add(nameID);
        } else {
            val transientNameID = Objects.requireNonNull(builder).buildObject();
            transientNameID.setURI(NameIDType.TRANSIENT);
            formats.add(transientNameID);
            val persistentNameID = builder.buildObject();
            persistentNameID.setURI(NameIDType.PERSISTENT);
            formats.add(persistentNameID);
            val emailNameID = builder.buildObject();
            emailNameID.setURI(NameIDType.EMAIL);
            formats.add(emailNameID);
            val unspecNameID = builder.buildObject();
            unspecNameID.setURI(NameIDType.UNSPECIFIED);
            formats.add(unspecNameID);
        }
        return formats;
    }

    /**
     * <p>getAssertionConsumerService.</p>
     *
     * @param binding a {@link java.lang.String} object
     * @param index a int
     * @param isDefault a boolean
     * @return a {@link org.opensaml.saml.saml2.metadata.AssertionConsumerService} object
     */
    protected AssertionConsumerService getAssertionConsumerService(final String binding, final int index,
                                                                   final boolean isDefault) {
        val builder = (SAMLObjectBuilder<AssertionConsumerService>) this.builderFactory
            .getBuilder(AssertionConsumerService.DEFAULT_ELEMENT_NAME);
        val consumer = Objects.requireNonNull(builder).buildObject();
        consumer.setLocation(this.assertionConsumerServiceUrl);
        consumer.setBinding(binding);
        if (isDefault) {
            consumer.setIsDefault(true);
        }
        consumer.setIndex(index);
        return consumer;
    }

    /**
     * <p>getSingleLogoutService.</p>
     *
     * @param binding a {@link java.lang.String} object
     * @return a {@link org.opensaml.saml.saml2.metadata.SingleLogoutService} object
     */
    protected SingleLogoutService getSingleLogoutService(final String binding) {
        val builder = (SAMLObjectBuilder<SingleLogoutService>) this.builderFactory
            .getBuilder(SingleLogoutService.DEFAULT_ELEMENT_NAME);
        val logoutService = Objects.requireNonNull(builder).buildObject();
        logoutService.setLocation(this.singleLogoutServiceUrl);
        logoutService.setBinding(binding);
        return logoutService;
    }

    /**
     * <p>getKeyDescriptor.</p>
     *
     * @param type a {@link org.opensaml.security.credential.UsageType} object
     * @param key a {@link org.opensaml.xmlsec.signature.KeyInfo} object
     * @return a {@link org.opensaml.saml.saml2.metadata.KeyDescriptor} object
     */
    protected KeyDescriptor getKeyDescriptor(final UsageType type, final KeyInfo key) {
        val builder = (SAMLObjectBuilder<KeyDescriptor>)
            Configuration.getBuilderFactory()
                .getBuilder(KeyDescriptor.DEFAULT_ELEMENT_NAME);
        val descriptor = Objects.requireNonNull(builder).buildObject();
        descriptor.setUse(type);
        descriptor.setKeyInfo(key);
        return descriptor;
    }

    /**
     * <p>Getter for the field <code>blackListedSignatureSigningAlgorithms</code>.</p>
     *
     * @return a {@link java.util.List} object
     */
    public List<String> getBlackListedSignatureSigningAlgorithms() {
        if (blackListedSignatureSigningAlgorithms == null) {
            this.blackListedSignatureSigningAlgorithms =
                new ArrayList<>(defaultSignatureSigningConfiguration.getExcludedAlgorithms());
        }

        return blackListedSignatureSigningAlgorithms;
    }

    /**
     * <p>Getter for the field <code>signatureAlgorithms</code>.</p>
     *
     * @return a {@link java.util.List} object
     */
    public List<String> getSignatureAlgorithms() {
        if (signatureAlgorithms == null) {
            this.signatureAlgorithms = new ArrayList<>(defaultSignatureSigningConfiguration.getSignatureAlgorithms());
        }

        return signatureAlgorithms;
    }

    /**
     * <p>Getter for the field <code>signatureReferenceDigestMethods</code>.</p>
     *
     * @return a {@link java.util.List} object
     */
    public List<String> getSignatureReferenceDigestMethods() {
        if (signatureReferenceDigestMethods == null) {
            this.signatureReferenceDigestMethods = defaultSignatureSigningConfiguration.getSignatureReferenceDigestMethods();
        }
        return signatureReferenceDigestMethods;
    }

    private List<String> filterForRuntimeSupportedAlgorithms(final List<String> algorithms) {
        final List<String> filteredAlgorithms = new ArrayList<>(algorithms);
        return filteredAlgorithms
            .stream()
            .filter(uri -> Objects.requireNonNull(globalAlgorithmRegistry).isRuntimeSupported(uri))
            .collect(Collectors.toList());
    }

    private List<String> filterSignatureAlgorithms(final List<String> algorithms) {
        val filteredAlgorithms = filterForRuntimeSupportedAlgorithms(algorithms);
        if (blackListedSignatureSigningAlgorithms != null) {
            filteredAlgorithms.removeAll(this.blackListedSignatureSigningAlgorithms);
        }
        return filteredAlgorithms;
    }
}
