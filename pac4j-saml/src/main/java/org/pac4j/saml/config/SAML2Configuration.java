package org.pac4j.saml.config;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.With;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.shibboleth.shared.net.URIComparator;
import net.shibboleth.shared.net.impl.BasicURLComparator;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.HttpClient;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.xmlsec.config.impl.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.pac4j.core.client.config.BaseClientConfiguration;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.logout.handler.DefaultSessionLogoutHandler;
import org.pac4j.core.logout.handler.SessionLogoutHandler;
import org.pac4j.core.profile.converter.AttributeConverter;
import org.pac4j.core.resource.SpringResourceHelper;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.saml.crypto.CredentialProvider;
import org.pac4j.saml.crypto.KeyStoreCredentialProvider;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.metadata.BaseSAML2MetadataGenerator;
import org.pac4j.saml.metadata.SAML2FileSystemMetadataGenerator;
import org.pac4j.saml.metadata.SAML2HttpUrlMetadataGenerator;
import org.pac4j.saml.metadata.SAML2IdentityProviderMetadataResolver;
import org.pac4j.saml.metadata.SAML2MetadataContactPerson;
import org.pac4j.saml.metadata.SAML2MetadataGenerator;
import org.pac4j.saml.metadata.SAML2MetadataResolver;
import org.pac4j.saml.metadata.SAML2MetadataSigner;
import org.pac4j.saml.metadata.SAML2MetadataUIInfo;
import org.pac4j.saml.metadata.SAML2ServiceProviderRequestedAttribute;
import org.pac4j.saml.metadata.keystore.SAML2FileSystemKeystoreGenerator;
import org.pac4j.saml.metadata.keystore.SAML2HttpUrlKeystoreGenerator;
import org.pac4j.saml.metadata.keystore.SAML2KeystoreGenerator;
import org.pac4j.saml.profile.converter.SimpleSAML2AttributeConverter;
import org.pac4j.saml.sso.impl.SAML2ScopingIdentityProvider;
import org.pac4j.saml.store.EmptyStoreFactory;
import org.pac4j.saml.store.SAMLMessageStoreFactory;
import org.pac4j.saml.util.SAML2HttpClientBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import java.net.URL;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.function.Supplier;

/**
 * The class is responsible for capturing client settings and passing them around.
 *
 * @author Misagh Moayyed
 * @author Jerome Leleu
 * @since 1.7
 */
@Slf4j
@Getter
@Setter
@Accessors(chain = true)
@ToString(of = {"serviceProviderEntityId", "serviceProviderMetadataResource", "identityProviderMetadataResource"})
@With
@AllArgsConstructor
@NoArgsConstructor
public class SAML2Configuration extends BaseClientConfiguration {

    /**
     * Constant <code>DEFAULT_PROVIDER_NAME="pac4j-saml"</code>
     */
    protected static final String DEFAULT_PROVIDER_NAME = "pac4j-saml";

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

    @Getter
    private String privateKeyPassword;

    @Getter
    @Setter
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

    private SessionLogoutHandler sessionLogoutHandler;

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

    /**
     * <p>Constructor for SAML2Configuration.</p>
     *
     * @param keystorePath                 a {@link String} object
     * @param keystorePassword             a {@link String} object
     * @param privateKeyPassword           a {@link String} object
     * @param identityProviderMetadataPath a {@link String} object
     */
    public SAML2Configuration(final String keystorePath, final String keystorePassword, final String privateKeyPassword,
                              final String identityProviderMetadataPath) {
        this(null, null, SpringResourceHelper.buildResourceFromPath(keystorePath), keystorePassword, privateKeyPassword,
            SpringResourceHelper.buildResourceFromPath(identityProviderMetadataPath), null, null,
            DEFAULT_PROVIDER_NAME, null, null);
    }

    /**
     * <p>Constructor for SAML2Configuration.</p>
     *
     * @param keystoreResource                 a {@link Resource} object
     * @param keystorePassword                 a {@link String} object
     * @param privateKeyPassword               a {@link String} object
     * @param identityProviderMetadataResource a {@link Resource} object
     */
    public SAML2Configuration(final Resource keystoreResource, final String keystorePassword, final String privateKeyPassword,
                              final Resource identityProviderMetadataResource) {
        this(null, null, keystoreResource, keystorePassword, privateKeyPassword,
            identityProviderMetadataResource, null, null,
            DEFAULT_PROVIDER_NAME, null, null);
    }

    /**
     * <p>Constructor for SAML2Configuration.</p>
     *
     * @param keystoreResource                 a {@link Resource} object
     * @param keyStoreAlias                    a {@link String} object
     * @param keyStoreType                     a {@link String} object
     * @param keystorePassword                 a {@link String} object
     * @param privateKeyPassword               a {@link String} object
     * @param identityProviderMetadataResource a {@link Resource} object
     */
    public SAML2Configuration(final Resource keystoreResource, final String keyStoreAlias,
                              final String keyStoreType, final String keystorePassword, final String privateKeyPassword,
                              final Resource identityProviderMetadataResource) {
        this(keyStoreAlias, keyStoreType, keystoreResource, keystorePassword,
            privateKeyPassword, identityProviderMetadataResource, null,
            null, DEFAULT_PROVIDER_NAME, null, null);
    }

    /**
     * <p>Constructor for SAML2Configuration.</p>
     *
     * @param keyStoreAlias                    a {@link String} object
     * @param keyStoreType                     a {@link String} object
     * @param keystoreResource                 a {@link Resource} object
     * @param keystorePassword                 a {@link String} object
     * @param privateKeyPassword               a {@link String} object
     * @param identityProviderMetadataResource a {@link Resource} object
     * @param identityProviderEntityId         a {@link String} object
     * @param serviceProviderEntityId          a {@link String} object
     * @param providerName                     a {@link String} object
     * @param authnRequestExtensions           a {@link Supplier} object
     * @param attributeAsId                    a {@link String} object
     */
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

    /**
     * <p>Setter for the field <code>callbackUrl</code>.</p>
     *
     * @param callbackUrl a {@link String} object
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected void internalInit(final boolean forceReinit) {
        val keystoreGenerator = getKeystoreGenerator();
        if (keystoreGenerator.shouldGenerate()) {
            LOGGER.warn("Generating keystore one for/via: {}", this.keystoreResource);
            keystoreGenerator.generate();
        }

        if (sessionLogoutHandler == null) {
            sessionLogoutHandler = new DefaultSessionLogoutHandler();
        }

        initSignatureSigningConfiguration();
    }

    /**
     * <p>Getter for the field <code>keystoreGenerator</code>.</p>
     *
     * @return a {@link SAML2KeystoreGenerator} object
     */
    public SAML2KeystoreGenerator getKeystoreGenerator() {
        if (keystoreGenerator == null) {
            if (keystoreResource instanceof UrlResource) {
                return new SAML2HttpUrlKeystoreGenerator(this);
            }
            return new SAML2FileSystemKeystoreGenerator(this);
        }
        return this.keystoreGenerator;
    }

    /**
     * <p>setIdentityProviderMetadataResourceFilepath.</p>
     *
     * @param path a {@link String} object
     */
    public void setIdentityProviderMetadataResourceFilepath(final String path) {
        this.identityProviderMetadataResource = new FileSystemResource(path);
    }

    /**
     * <p>setIdentityProviderMetadataResourceClasspath.</p>
     *
     * @param path a {@link String} object
     */
    public void setIdentityProviderMetadataResourceClasspath(final String path) {
        this.identityProviderMetadataResource = new ClassPathResource(path);
    }

    /**
     * <p>setIdentityProviderMetadataResourceUrl.</p>
     *
     * @param url a {@link String} object
     */
    public void setIdentityProviderMetadataResourceUrl(final String url) {
        this.identityProviderMetadataResource = SpringResourceHelper.newUrlResource(url);
    }

    /**
     * <p>setIdentityProviderMetadataPath.</p>
     *
     * @param path a {@link String} object
     */
    public void setIdentityProviderMetadataPath(final String path) {
        this.identityProviderMetadataResource = SpringResourceHelper.buildResourceFromPath(path);
    }

    /**
     * <p>setKeystoreResourceFilepath.</p>
     *
     * @param path a {@link String} object
     */
    public void setKeystoreResourceFilepath(final String path) {
        this.keystoreResource = new FileSystemResource(path);
    }

    /**
     * <p>setKeystoreResourceClasspath.</p>
     *
     * @param path a {@link String} object
     */
    public void setKeystoreResourceClasspath(final String path) {
        this.keystoreResource = new ClassPathResource(path);
    }

    /**
     * <p>setKeystoreResourceUrl.</p>
     *
     * @param url a {@link String} object
     */
    public void setKeystoreResourceUrl(final String url) {
        this.keystoreResource = SpringResourceHelper.buildResourceFromPath(url);
    }

    /**
     * <p>setKeystorePath.</p>
     *
     * @param path a {@link String} object
     */
    public void setKeystorePath(final String path) {
        this.keystoreResource = SpringResourceHelper.buildResourceFromPath(path);
    }


    /**
     * <p>setServiceProviderMetadataResourceFilepath.</p>
     *
     * @param path a {@link String} object
     */
    public void setServiceProviderMetadataResourceFilepath(final String path) {
        this.serviceProviderMetadataResource = new FileSystemResource(path);
    }

    /**
     * <p>setServiceProviderMetadataPath.</p>
     *
     * @param path a {@link String} object
     */
    public void setServiceProviderMetadataPath(final String path) {
        this.serviceProviderMetadataResource = SpringResourceHelper.buildResourceFromPath(path);
    }

    /**
     * <p>findSessionLogoutHandler.</p>
     *
     * @return a {@link SessionLogoutHandler} object
     */
    public SessionLogoutHandler findSessionLogoutHandler() {
        init();

        return sessionLogoutHandler;
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

    /**
     * <p>Getter for the field <code>httpClient</code>.</p>
     *
     * @return a {@link HttpClient} object
     */
    public HttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = new SAML2HttpClientBuilder().build();
        }
        return httpClient;
    }

    /**
     * <p>getCredentialProvider.</p>
     *
     * @return a {@link CredentialProvider} object
     */
    public CredentialProvider getCredentialProvider() {
        return new KeyStoreCredentialProvider(this);
    }

    /**
     * <p>toMetadataGenerator.</p>
     *
     * @return a {@link SAML2MetadataGenerator} object
     */
    public SAML2MetadataGenerator toMetadataGenerator() {
        try {
            val instance = getMetadataGenerator();
            if (instance instanceof BaseSAML2MetadataGenerator generator) {
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

    /**
     * <p>determineSingleSignOutServiceUrl.</p>
     *
     * @param generator a {@link BaseSAML2MetadataGenerator} object
     */
    protected void determineSingleSignOutServiceUrl(final BaseSAML2MetadataGenerator generator) {
        val logoutUrl = CommonHelper.ifBlank(this.singleSignOutServiceUrl, callbackUrl);
        generator.setSingleLogoutServiceUrl(logoutUrl);
    }

    /**
     * <p>Getter for the field <code>metadataGenerator</code>.</p>
     *
     * @return a {@link SAML2MetadataGenerator} object
     */
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

    /**
     * <p>Getter for the field <code>identityProviderMetadataResolver</code>.</p>
     *
     * @return a {@link SAML2MetadataResolver} object
     */
    public SAML2MetadataResolver getIdentityProviderMetadataResolver() {
        return Objects.requireNonNullElseGet(identityProviderMetadataResolver, () -> new SAML2IdentityProviderMetadataResolver(this));
    }
}
