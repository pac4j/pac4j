package org.pac4j.saml.config;


import lombok.val;
import net.shibboleth.shared.net.URIComparator;
import net.shibboleth.shared.net.impl.BasicURLComparator;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.xmlsec.config.impl.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.pac4j.core.client.config.BaseClientConfiguration;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.logout.handler.DefaultLogoutHandler;
import org.pac4j.core.logout.handler.LogoutHandler;
import org.pac4j.core.profile.converter.AttributeConverter;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.saml.crypto.CredentialProvider;
import org.pac4j.saml.crypto.KeyStoreCredentialProvider;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.metadata.*;
import org.pac4j.saml.metadata.keystore.SAML2FileSystemKeystoreGenerator;
import org.pac4j.saml.metadata.keystore.SAML2HttpUrlKeystoreGenerator;
import org.pac4j.saml.metadata.keystore.SAML2KeystoreGenerator;
import org.pac4j.saml.profile.converter.SimpleSAML2AttributeConverter;
import org.pac4j.saml.sso.impl.SAML2ScopingIdentityProvider;
import org.pac4j.saml.store.EmptyStoreFactory;
import org.pac4j.saml.store.SAMLMessageStoreFactory;
import org.pac4j.saml.util.SAML2HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Period;
import java.util.*;
import java.util.function.Supplier;

/**
 * The class is responsible for capturing client settings and passing them around.
 *
 * @author Misagh Moayyed
 * @author Jerome Leleu
 * @since 1.7
 */
public class SAML2Configuration extends BaseClientConfiguration {

    protected static final String RESOURCE_PREFIX = "resource:";

    protected static final String CLASSPATH_PREFIX = "classpath:";

    protected static final String FILE_PREFIX = "file:";

    protected static final String DEFAULT_PROVIDER_NAME = "pac4j-saml";

    private static final Logger LOGGER = LoggerFactory.getLogger(SAML2Configuration.class);

    private final List<SAML2ScopingIdentityProvider> scopingIdentityProviders = new ArrayList<>();

    private final List<SAML2ServiceProviderRequestedAttribute> requestedServiceProviderAttributes = new ArrayList<>();

    private HostnameVerifier hostnameVerifier;

    private SSLSocketFactory sslSocketFactory;

    private SAML2MetadataSigner metadataSigner;

    private String singleSignOutServiceUrl;

    private String nameIdAttribute;

    private String callbackUrl;

    private String requestInitiatorUrl;

    private String assertionConsumerServiceUrl;

    private Resource keystoreResource;

    private String keystorePassword;

    private String privateKeyPassword;

    private String certificateNameToAppend;

    private Resource identityProviderMetadataResource;

    private String identityProviderEntityId;

    private String serviceProviderEntityId;

    private long maximumAuthenticationLifetime = 3600;

    private long acceptedSkew = 300;

    private boolean forceAuth = false;

    private boolean passive = false;

    private String comparisonType = null;

    private boolean isPartialLogoutTreatedAsSuccess = true;

    private String authnRequestBindingType = SAMLConstants.SAML2_POST_BINDING_URI;

    private String responseBindingType = SAMLConstants.SAML2_POST_BINDING_URI;

    private String spLogoutRequestBindingType = SAMLConstants.SAML2_POST_BINDING_URI;

    private String spLogoutResponseBindingType = SAMLConstants.SAML2_POST_BINDING_URI;

    private List<String> authnContextClassRefs = new ArrayList<>();

    private String nameIdPolicyFormat = null;

    private boolean useNameQualifier = false;

    private boolean signMetadata;

    private Resource serviceProviderMetadataResource;

    private boolean forceServiceProviderMetadataGeneration;

    private boolean forceKeystoreGeneration;

    private SAMLMessageStoreFactory samlMessageStoreFactory = new EmptyStoreFactory();

    private SAML2KeystoreGenerator keystoreGenerator;

    private SAML2MetadataGenerator metadataGenerator;

    private boolean authnRequestSigned;

    private boolean spLogoutRequestSigned;

    private Collection<String> blackListedSignatureSigningAlgorithms;

    private List<String> signatureAlgorithms;

    private List<String> signatureReferenceDigestMethods;

    private String signatureCanonicalizationAlgorithm;

    private boolean wantsAssertionsSigned = false;

    private boolean wantsResponsesSigned = false;

    private boolean allSignatureValidationDisabled = false;

    private boolean responseDestinationAttributeMandatory = true;

    private String keyStoreAlias;

    private String keyStoreType;

    private int assertionConsumerServiceIndex = -1;

    private int attributeConsumingServiceIndex = -1;

    private String providerName;

    private Supplier<List<XSAny>> authnRequestExtensions;

    private String attributeAsId;

    private Map<String, String> mappedAttributes = new LinkedHashMap<>();

    private URIComparator uriComparator = new BasicURLComparator();

    private LogoutHandler logoutHandler;

    private String postLogoutURL;

    private Period certificateExpirationPeriod = Period.ofYears(20);

    private String certificateSignatureAlg = "SHA1WithRSA";

    private int privateKeySize = 2048;

    private List<SAML2MetadataContactPerson> contactPersons = new ArrayList<>();

    private List<SAML2MetadataUIInfo> metadataUIInfos = new ArrayList<>();

    private String issuerFormat = Issuer.ENTITY;

    private HttpClient httpClient;

    private AttributeConverter samlAttributeConverter = new SimpleSAML2AttributeConverter();

    /**
     * If {@link #nameIdPolicyFormat} is defined, this setting
     * will control whether the allow-create flag is used and set.
     * A {@code null} value will skip setting the allow-create flag altogether.
     */
    private Boolean nameIdPolicyAllowCreate = Boolean.TRUE;

    private List<String> supportedProtocols = new ArrayList<>(Arrays.asList(SAMLConstants.SAML20P_NS,
        SAMLConstants.SAML10P_NS, SAMLConstants.SAML11P_NS));

    private SAML2MetadataResolver identityProviderMetadataResolver;

    private int identityProviderMetadataConnectTimeout = 2500;

    private int identityProviderMetadataReadTimeout = 2500;

    public SAML2Configuration() {
    }

    public SAML2Configuration(final String keystorePath, final String keystorePassword, final String privateKeyPassword,
                              final String identityProviderMetadataPath) {
        this(null, null, mapPathToResource(keystorePath), keystorePassword, privateKeyPassword,
            mapPathToResource(identityProviderMetadataPath), null, null,
            DEFAULT_PROVIDER_NAME, null, null);
    }

    public SAML2Configuration(final Resource keystoreResource, final String keystorePassword, final String privateKeyPassword,
                              final Resource identityProviderMetadataResource) {
        this(null, null, keystoreResource, keystorePassword, privateKeyPassword,
            identityProviderMetadataResource, null, null,
            DEFAULT_PROVIDER_NAME, null, null);
    }

    public SAML2Configuration(final Resource keystoreResource, final String keyStoreAlias,
                              final String keyStoreType, final String keystorePassword, final String privateKeyPassword,
                              final Resource identityProviderMetadataResource) {
        this(keyStoreAlias, keyStoreType, keystoreResource, keystorePassword,
            privateKeyPassword, identityProviderMetadataResource, null,
            null, DEFAULT_PROVIDER_NAME, null, null);
    }

    protected SAML2Configuration(final String keyStoreAlias, final String keyStoreType,
                                 final Resource keystoreResource, final String keystorePassword,
                                 final String privateKeyPassword, final Resource identityProviderMetadataResource,
                                 final String identityProviderEntityId, final String serviceProviderEntityId,
                                 final String providerName, final Supplier<List<XSAny>> authnRequestExtensions,
                                 final String attributeAsId) {
        this.keyStoreAlias = keyStoreAlias;
        this.keyStoreType = keyStoreType;
        this.keystoreResource = keystoreResource;
        this.keystorePassword = keystorePassword;
        this.privateKeyPassword = privateKeyPassword;
        this.identityProviderMetadataResource = identityProviderMetadataResource;
        this.identityProviderEntityId = identityProviderEntityId;
        this.serviceProviderEntityId = serviceProviderEntityId;
        this.providerName = providerName;
        this.authnRequestExtensions = authnRequestExtensions;
        this.attributeAsId = attributeAsId;
    }

    protected static UrlResource newUrlResource(final String url) {
        try {
            return new UrlResource(url);
        } catch (final MalformedURLException e) {
            throw new TechnicalException(e);
        }
    }

    private static Resource mapPathToResource(final String path) {
        CommonHelper.assertNotBlank("path", path);
        try {
            if (path.startsWith(RESOURCE_PREFIX)) {
                return new ClassPathResource(path.substring(RESOURCE_PREFIX.length()));
            }
            if (path.startsWith(CLASSPATH_PREFIX)) {
                return new ClassPathResource(path.substring(CLASSPATH_PREFIX.length()));
            }
            if (path.startsWith(HttpConstants.SCHEME_HTTP) || path.startsWith(HttpConstants.SCHEME_HTTPS)) {
                return new UrlResource(new URL(path));
            }
            if (path.startsWith(FILE_PREFIX)) {
                return new FileSystemResource(path.substring(FILE_PREFIX.length()));
            }
            return new FileSystemResource(path);
        } catch (final Exception e) {
            throw new TechnicalException(e);
        }
    }

    public void setCallbackUrl(final String callbackUrl) {
        this.callbackUrl = callbackUrl;
        try {
            if (CommonHelper.isBlank(getServiceProviderEntityId())) {
                val url = new URL(callbackUrl);
                if (url.getQuery() != null) {
                    setServiceProviderEntityId(url.toString().replace('?' + url.getQuery(), Pac4jConstants.EMPTY_STRING));
                } else {
                    setServiceProviderEntityId(url.toString());
                }
            }
            LOGGER.info("Using service provider entity ID {}", getServiceProviderEntityId());
        } catch (final Exception e) {
            throw new SAMLException(e);
        }
    }

    public String getRequestInitiatorUrl() {
        return requestInitiatorUrl;
    }

    public void setRequestInitiatorUrl(String requestInitiatorUrl) {
        this.requestInitiatorUrl = requestInitiatorUrl;
    }

    public String getAssertionConsumerServiceUrl() {
        return assertionConsumerServiceUrl;
    }

    public void setAssertionConsumerServiceUrl(String assertionConsumerServiceUrl) {
        this.assertionConsumerServiceUrl = assertionConsumerServiceUrl;
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        val keystoreGenerator = getKeystoreGenerator();
        if (keystoreGenerator.shouldGenerate()) {
            LOGGER.warn("Generating keystore one for/via: {}", this.keystoreResource);
            keystoreGenerator.generate();
        }

        if (logoutHandler == null) {
            logoutHandler = new DefaultLogoutHandler();
        }

        initSignatureSigningConfiguration();
    }

    public SAML2KeystoreGenerator getKeystoreGenerator() {
        if (keystoreGenerator == null) {
            if (keystoreResource instanceof UrlResource) {
                return new SAML2HttpUrlKeystoreGenerator(this);
            }
            return new SAML2FileSystemKeystoreGenerator(this);
        }
        return this.keystoreGenerator;
    }

    public void setKeystoreGenerator(final SAML2KeystoreGenerator keystoreGenerator) {
        this.keystoreGenerator = keystoreGenerator;
    }

    public Boolean isNameIdPolicyAllowCreate() {
        return nameIdPolicyAllowCreate;
    }

    public void setNameIdPolicyAllowCreate(final Boolean nameIdPolicyAllowCreate) {
        this.nameIdPolicyAllowCreate = nameIdPolicyAllowCreate;
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

    public List<String> getSupportedProtocols() {
        return supportedProtocols;
    }

    public void setSupportedProtocols(final List<String> supportedProtocols) {
        this.supportedProtocols = supportedProtocols;
    }

    public String getCertificateSignatureAlg() {
        return certificateSignatureAlg;
    }

    public void setCertificateSignatureAlg(final String certificateSignatureAlg) {
        this.certificateSignatureAlg = certificateSignatureAlg;
    }

    public Period getCertificateExpirationPeriod() {
        return certificateExpirationPeriod;
    }

    public void setCertificateExpirationPeriod(final Period certificateExpirationPeriod) {
        this.certificateExpirationPeriod = certificateExpirationPeriod;
    }

    public int getPrivateKeySize() {
        return privateKeySize;
    }

    public void setPrivateKeySize(final int privateKeySize) {
        this.privateKeySize = privateKeySize;
    }


    public List<SAML2ScopingIdentityProvider> getScopingIdentityProviders() {
        return scopingIdentityProviders;
    }

    public List<SAML2ServiceProviderRequestedAttribute> getRequestedServiceProviderAttributes() {
        return requestedServiceProviderAttributes;
    }

    public void setIdentityProviderMetadataResourceFilepath(final String path) {
        this.identityProviderMetadataResource = new FileSystemResource(path);
    }

    public void setIdentityProviderMetadataResourceClasspath(final String path) {
        this.identityProviderMetadataResource = new ClassPathResource(path);
    }

    public void setIdentityProviderMetadataResourceUrl(final String url) {
        this.identityProviderMetadataResource = newUrlResource(url);
    }

    public void setIdentityProviderMetadataPath(final String path) {
        this.identityProviderMetadataResource = mapPathToResource(path);
    }

    public int getAssertionConsumerServiceIndex() {
        return assertionConsumerServiceIndex;
    }

    public void setAssertionConsumerServiceIndex(final int assertionConsumerServiceIndex) {
        this.assertionConsumerServiceIndex = assertionConsumerServiceIndex;
    }

    public boolean isForceKeystoreGeneration() {
        return forceKeystoreGeneration;
    }

    public void setForceKeystoreGeneration(final boolean forceKeystoreGeneration) {
        this.forceKeystoreGeneration = forceKeystoreGeneration;
    }

    public long getAcceptedSkew() {
        return acceptedSkew;
    }

    public void setAcceptedSkew(final long acceptedSkew) {
        this.acceptedSkew = acceptedSkew;
    }

    public Resource getIdentityProviderMetadataResource() {
        return this.identityProviderMetadataResource;
    }

    public void setIdentityProviderMetadataResource(final Resource identityProviderMetadataResource) {
        this.identityProviderMetadataResource = identityProviderMetadataResource;
    }

    public String getIdentityProviderEntityId() {
        return identityProviderEntityId;
    }

    public void setIdentityProviderEntityId(final String identityProviderEntityId) {
        this.identityProviderEntityId = identityProviderEntityId;
    }

    public void setKeystoreAlias(final String keyStoreAlias) {
        this.keyStoreAlias = keyStoreAlias;
    }

    public void setKeystoreType(final String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }

    public void setKeystoreResourceFilepath(final String path) {
        this.keystoreResource = new FileSystemResource(path);
    }

    public void setKeystoreResourceClasspath(final String path) {
        this.keystoreResource = new ClassPathResource(path);
    }

    public void setKeystoreResourceUrl(final String url) {
        this.keystoreResource = mapPathToResource(url);
    }

    public void setKeystorePath(final String path) {
        this.keystoreResource = mapPathToResource(path);
    }

    public String getKeyStoreAlias() {
        return keyStoreAlias;
    }

    public String getKeyStoreType() {
        return this.keyStoreType;
    }

    public Resource getKeystoreResource() {
        return keystoreResource;
    }

    public void setKeystoreResource(final Resource keystoreResource) {
        this.keystoreResource = keystoreResource;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public void setKeystorePassword(final String keystorePassword) {
        this.keystorePassword = keystorePassword;
    }

    public String getPrivateKeyPassword() {
        return privateKeyPassword;
    }

    public void setPrivateKeyPassword(final String privateKeyPassword) {
        this.privateKeyPassword = privateKeyPassword;
    }

    public String getCertificateNameToAppend() {
        return certificateNameToAppend;
    }

    public void setCertificateNameToAppend(final String certificateNameToAppend) {
        this.certificateNameToAppend = certificateNameToAppend;
    }

    public void setServiceProviderMetadataResourceFilepath(final String path) {
        this.serviceProviderMetadataResource = new FileSystemResource(path);
    }

    public void setServiceProviderMetadataPath(final String path) {
        this.serviceProviderMetadataResource = mapPathToResource(path);
    }

    public Resource getServiceProviderMetadataResource() {
        return serviceProviderMetadataResource;
    }

    public void setServiceProviderMetadataResource(final Resource serviceProviderMetadataResource) {
        this.serviceProviderMetadataResource = serviceProviderMetadataResource;
    }

    public String getServiceProviderEntityId() {
        return serviceProviderEntityId;
    }

    public void setServiceProviderEntityId(final String serviceProviderEntityId) {
        this.serviceProviderEntityId = serviceProviderEntityId;
    }

    public boolean isPassive() {
        return passive;
    }

    public void setPassive(final boolean passive) {
        this.passive = passive;
    }

    public boolean isForceAuth() {
        return forceAuth;
    }

    public void setForceAuth(final boolean forceAuth) {
        this.forceAuth = forceAuth;
    }

    public String getComparisonType() {
        return comparisonType;
    }

    public void setComparisonType(final String comparisonType) {
        this.comparisonType = comparisonType;
    }

    public String getAuthnRequestBindingType() {
        return authnRequestBindingType;
    }

    public void setAuthnRequestBindingType(final String authnRequestBindingType) {
        this.authnRequestBindingType = authnRequestBindingType;
    }

    public String getResponseBindingType() {
        return responseBindingType;
    }

    public void setResponseBindingType(final String responseBindingType) {
        this.responseBindingType = responseBindingType;
    }

    public String getSpLogoutRequestBindingType() {
        return spLogoutRequestBindingType;
    }

    public void setSpLogoutRequestBindingType(final String spLogoutRequestBindingType) {
        this.spLogoutRequestBindingType = spLogoutRequestBindingType;
    }

    public String getSpLogoutResponseBindingType() {
        return spLogoutResponseBindingType;
    }

    public void setSpLogoutResponseBindingType(final String spLogoutResponseBindingType) {
        this.spLogoutResponseBindingType = spLogoutResponseBindingType;
    }

    public List<String> getAuthnContextClassRefs() {
        return authnContextClassRefs;
    }

    public void setAuthnContextClassRefs(final List<String> authnContextClassRefs) {
        this.authnContextClassRefs = authnContextClassRefs;
    }

    public URIComparator getUriComparator() {
        return uriComparator;
    }

    public void setUriComparator(final URIComparator uriComparator) {
        this.uriComparator = uriComparator;
    }

    public String getNameIdPolicyFormat() {
        return nameIdPolicyFormat;
    }

    public void setNameIdPolicyFormat(final String nameIdPolicyFormat) {
        this.nameIdPolicyFormat = nameIdPolicyFormat;
    }

    public long getMaximumAuthenticationLifetime() {
        return maximumAuthenticationLifetime;
    }

    public void setMaximumAuthenticationLifetime(final long maximumAuthenticationLifetime) {
        this.maximumAuthenticationLifetime = maximumAuthenticationLifetime;
    }

    public boolean isForceServiceProviderMetadataGeneration() {
        return forceServiceProviderMetadataGeneration;
    }

    public void setForceServiceProviderMetadataGeneration(final boolean forceServiceProviderMetadataGeneration) {
        this.forceServiceProviderMetadataGeneration = forceServiceProviderMetadataGeneration;
    }

    public SAMLMessageStoreFactory getSamlMessageStoreFactory() {
        return samlMessageStoreFactory;
    }

    public void setSamlMessageStoreFactory(final SAMLMessageStoreFactory samlMessageStoreFactory) {
        this.samlMessageStoreFactory = samlMessageStoreFactory;
    }

    public Collection<String> getBlackListedSignatureSigningAlgorithms() {
        return blackListedSignatureSigningAlgorithms;
    }

    public void setBlackListedSignatureSigningAlgorithms(final Collection<String> blackListedSignatureSigningAlgorithms) {
        this.blackListedSignatureSigningAlgorithms = blackListedSignatureSigningAlgorithms;
    }

    public List<String> getSignatureAlgorithms() {
        return signatureAlgorithms;
    }

    public void setSignatureAlgorithms(final List<String> signatureAlgorithms) {
        this.signatureAlgorithms = signatureAlgorithms;
    }

    public List<String> getSignatureReferenceDigestMethods() {
        return signatureReferenceDigestMethods;
    }

    public void setSignatureReferenceDigestMethods(final List<String> signatureReferenceDigestMethods) {
        this.signatureReferenceDigestMethods = signatureReferenceDigestMethods;
    }

    public String getSignatureCanonicalizationAlgorithm() {
        return signatureCanonicalizationAlgorithm;
    }

    public void setSignatureCanonicalizationAlgorithm(final String signatureCanonicalizationAlgorithm) {
        this.signatureCanonicalizationAlgorithm = signatureCanonicalizationAlgorithm;
    }

    public boolean isWantsAssertionsSigned() {
        return this.wantsAssertionsSigned;
    }

    public void setWantsAssertionsSigned(final boolean wantsAssertionsSigned) {
        this.wantsAssertionsSigned = wantsAssertionsSigned;
    }

    public boolean isWantsResponsesSigned() {
        return wantsResponsesSigned;
    }

    public void setWantsResponsesSigned(final boolean wantsResponsesSigned) {
        this.wantsResponsesSigned = wantsResponsesSigned;
    }

    public boolean isAuthnRequestSigned() {
        return authnRequestSigned;
    }

    public void setAuthnRequestSigned(final boolean authnRequestSigned) {
        this.authnRequestSigned = authnRequestSigned;
    }

    public boolean isSpLogoutRequestSigned() {
        return spLogoutRequestSigned;
    }

    public void setSpLogoutRequestSigned(final boolean spLogoutRequestSigned) {
        this.spLogoutRequestSigned = spLogoutRequestSigned;
    }

    public boolean isAllSignatureValidationDisabled() {
        return allSignatureValidationDisabled;
    }

    /**
     * Disables all signature validation. DO NOT ENABLE THIS IN PRODUCTION!
     * This option is only provided for development purposes.
     *
     * @param allSignatureValidationDisabled
     */
    public void setAllSignatureValidationDisabled(final boolean allSignatureValidationDisabled) {
        this.allSignatureValidationDisabled = allSignatureValidationDisabled;
    }

    /**
     * SAML specification states the Response `Destination` attribute is optional.
     * Providing a value is recommended to prevent malicious forwarding of responses to unintended recipients.
     */
    public void setResponseDestinationAttributeMandatory(final boolean mandatory) {
        this.responseDestinationAttributeMandatory = mandatory;
    }

    public boolean isResponseDestinationAttributeMandatory() {
        return responseDestinationAttributeMandatory;
    }

    public int getAttributeConsumingServiceIndex() {
        return attributeConsumingServiceIndex;
    }

    public void setAttributeConsumingServiceIndex(final int attributeConsumingServiceIndex) {
        this.attributeConsumingServiceIndex = attributeConsumingServiceIndex;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(final String providerName) {
        this.providerName = providerName;
    }

    public Supplier<List<XSAny>> getAuthnRequestExtensions() {
        return authnRequestExtensions;
    }

    public void setAuthnRequestExtensions(final Supplier<List<XSAny>> authnRequestExtensions) {
        this.authnRequestExtensions = authnRequestExtensions;
    }

    public SAML2MetadataSigner getMetadataSigner() {
        return metadataSigner;
    }

    public void setMetadataSigner(final SAML2MetadataSigner metadataSigner) {
        this.metadataSigner = metadataSigner;
    }

    public String getAttributeAsId() {
        return attributeAsId;
    }

    public void setAttributeAsId(final String attributeAsId) {
        this.attributeAsId = attributeAsId;
    }

    public boolean isUseNameQualifier() {
        return useNameQualifier;
    }

    public void setUseNameQualifier(final boolean useNameQualifier) {
        this.useNameQualifier = useNameQualifier;
    }

    public boolean isSignMetadata() {
        return signMetadata;
    }

    public void setSignMetadata(final boolean signMetadata) {
        this.signMetadata = signMetadata;
    }

    public Map<String, String> getMappedAttributes() {
        return mappedAttributes;
    }

    public void setMappedAttributes(final Map<String, String> mappedAttributes) {
        this.mappedAttributes = mappedAttributes;
    }

    public LogoutHandler getLogoutHandler() {
        return logoutHandler;
    }

    public void setLogoutHandler(final LogoutHandler logoutHandler) {
        this.logoutHandler = logoutHandler;
    }

    public String getPostLogoutURL() {
        return postLogoutURL;
    }

    public void setPostLogoutURL(final String postLogoutURL) {
        this.postLogoutURL = postLogoutURL;
    }

    public String getNameIdAttribute() {
        return nameIdAttribute;
    }

    public void setNameIdAttribute(final String nameIdAttribute) {
        this.nameIdAttribute = nameIdAttribute;
    }

    public HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

    public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
    }

    public SSLSocketFactory getSslSocketFactory() {
        return sslSocketFactory;
    }

    public void setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
    }

    public LogoutHandler findLogoutHandler() {
        init();

        return logoutHandler;
    }

    private void initSignatureSigningConfiguration() {
        // Bootstrap signature signing configuration if not manually set
        val config = DefaultSecurityConfigurationBootstrap
            .buildDefaultSignatureSigningConfiguration();
        if (this.blackListedSignatureSigningAlgorithms == null) {
            this.blackListedSignatureSigningAlgorithms = new ArrayList<>(
                config.getExcludedAlgorithms());
            LOGGER.info("Bootstrapped Blacklisted Algorithms");
        }
        if (this.signatureAlgorithms == null) {
            this.signatureAlgorithms = new ArrayList<>(
                config.getSignatureAlgorithms());
            LOGGER.info("Bootstrapped Signature Algorithms");
        }
        if (this.signatureReferenceDigestMethods == null) {
            this.signatureReferenceDigestMethods = new ArrayList<>(
                config.getSignatureReferenceDigestMethods());
            this.signatureReferenceDigestMethods
                .remove(SignatureConstants.ALGO_ID_DIGEST_SHA512);
            LOGGER.info("Bootstrapped Signature Reference Digest Methods");
        }
        if (this.signatureCanonicalizationAlgorithm == null) {
            this.signatureCanonicalizationAlgorithm = config
                .getSignatureCanonicalizationAlgorithm();
            LOGGER.info("Bootstrapped Canonicalization Algorithm");
        }
    }

    public String getIssuerFormat() {
        return issuerFormat;
    }

    public void setIssuerFormat(final String issuerFormat) {
        this.issuerFormat = issuerFormat;
    }

    public String getSingleSignOutServiceUrl() {
        return singleSignOutServiceUrl;
    }

    public void setSingleSignOutServiceUrl(final String singleSignOutServiceUrl) {
        this.singleSignOutServiceUrl = singleSignOutServiceUrl;
    }

    public HttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = new SAML2HttpClientBuilder().build();
        }
        return httpClient;
    }

    public void setHttpClient(final HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public CredentialProvider getCredentialProvider() {
        return new KeyStoreCredentialProvider(this);
    }

    public SAML2MetadataGenerator toMetadataGenerator() {
        try {
            val instance = getMetadataGenerator();
            if (instance instanceof BaseSAML2MetadataGenerator) {
                val generator = (BaseSAML2MetadataGenerator) instance;
                generator.setWantAssertionSigned(isWantsAssertionsSigned());
                generator.setAuthnRequestSigned(isAuthnRequestSigned());
                generator.setSignMetadata(isSignMetadata());
                generator.setNameIdPolicyFormat(getNameIdPolicyFormat());
                generator.setRequestedAttributes(getRequestedServiceProviderAttributes());
                generator.setCredentialProvider(getCredentialProvider());
                generator.setMetadataSigner(getMetadataSigner());
                generator.setEntityId(getServiceProviderEntityId());

                generator.setRequestInitiatorLocation(StringUtils.defaultString(this.requestInitiatorUrl, this.callbackUrl));
                generator.setAssertionConsumerServiceUrl(StringUtils.defaultString(this.assertionConsumerServiceUrl, this.callbackUrl));

                generator.setResponseBindingType(getResponseBindingType());

                determineSingleSignOutServiceUrl(generator);

                if (getBlackListedSignatureSigningAlgorithms() != null) {
                    generator.setBlackListedSignatureSigningAlgorithms(
                        new ArrayList<>(getBlackListedSignatureSigningAlgorithms()));
                }
                generator.setSignatureAlgorithms(getSignatureAlgorithms());
                generator.setSignatureReferenceDigestMethods(getSignatureReferenceDigestMethods());

                generator.setSupportedProtocols(getSupportedProtocols());
                generator.setContactPersons(getContactPersons());
                generator.setMetadataUIInfos(getMetadataUIInfos());
            }
            return instance;
        } catch (final Exception e) {
            throw new TechnicalException(e);
        }
    }

    protected void determineSingleSignOutServiceUrl(final BaseSAML2MetadataGenerator generator) {
        val url = CommonHelper.ifBlank(this.singleSignOutServiceUrl, callbackUrl);
        val logoutUrl = CommonHelper.addParameter(url, Pac4jConstants.LOGOUT_ENDPOINT_PARAMETER, "true");
        // the logout URL is callback URL with an extra parameter
        generator.setSingleLogoutServiceUrl(logoutUrl);
    }

    public SAML2MetadataGenerator getMetadataGenerator() {
        return Objects.requireNonNullElseGet(this.metadataGenerator,
            () -> ServiceLoader.load(SAML2MetadataGenerator.class).stream().findFirst()
                .map(ServiceLoader.Provider::get)
                .orElseGet(() -> {
                    try {
                        return serviceProviderMetadataResource instanceof UrlResource
                            ? new SAML2HttpUrlMetadataGenerator(serviceProviderMetadataResource.getURL(), getHttpClient())
                            : new SAML2FileSystemMetadataGenerator(serviceProviderMetadataResource);
                    } catch (final Exception e) {
                        throw new TechnicalException(e);
                    }
                }));
    }

    public void setMetadataGenerator(final SAML2MetadataGenerator metadataGenerator) {
        this.metadataGenerator = metadataGenerator;
    }

    public SAML2MetadataResolver getIdentityProviderMetadataResolver() {
        if (identityProviderMetadataResolver == null) {
            return new SAML2IdentityProviderMetadataResolver(this);
        }
        return identityProviderMetadataResolver;
    }

    public void setIdentityProviderMetadataResolver(final SAML2MetadataResolver identityProviderMetadataResolver) {
        this.identityProviderMetadataResolver = identityProviderMetadataResolver;
    }

    public boolean isPartialLogoutTreatedAsSuccess() {
        return isPartialLogoutTreatedAsSuccess;
    }

    public void setPartialLogoutTreatedAsSuccess(boolean partialLogoutTreatedAsSuccess) {
        isPartialLogoutTreatedAsSuccess = partialLogoutTreatedAsSuccess;
    }

    public AttributeConverter getSamlAttributeConverter() {
        return samlAttributeConverter;
    }

    public void setSamlAttributeConverter(final AttributeConverter samlAttributeConverter) {
        this.samlAttributeConverter = samlAttributeConverter;
    }

    public int getIdentityProviderMetadataConnectTimeout() {
        return identityProviderMetadataConnectTimeout;
    }

    public void setIdentityProviderMetadataConnectTimeout(int identityProviderMetadataConnectTimeout) {
        this.identityProviderMetadataConnectTimeout = identityProviderMetadataConnectTimeout;
    }

    public int getIdentityProviderMetadataReadTimeout() {
        return identityProviderMetadataReadTimeout;
    }

    public void setIdentityProviderMetadataReadTimeout(int identityProviderMetadataReadTimeout) {
        this.identityProviderMetadataReadTimeout = identityProviderMetadataReadTimeout;
    }
}
