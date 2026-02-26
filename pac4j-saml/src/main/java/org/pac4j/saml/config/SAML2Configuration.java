package org.pac4j.saml.config;


import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import net.shibboleth.shared.net.URIComparator;
import net.shibboleth.shared.net.impl.BasicURLComparator;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.HttpClient;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.xmlsec.config.impl.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.pac4j.core.client.config.BaseClientConfiguration;
import org.pac4j.core.client.config.KeystoreProperties;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.keystore.generation.KeystoreGenerator;
import org.pac4j.core.profile.converter.AttributeConverter;
import org.pac4j.core.resource.SpringResourceHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.saml.crypto.CredentialProvider;
import org.pac4j.saml.crypto.KeyStoreCredentialProvider;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.metadata.*;
import org.pac4j.saml.metadata.keystore.SAML2FileSystemKeystoreGenerator;
import org.pac4j.saml.metadata.keystore.SAML2HttpUrlKeystoreGenerator;
import org.pac4j.saml.profile.api.SAML2ObjectBuilder;
import org.pac4j.saml.profile.converter.SimpleSAML2AttributeConverter;
import org.pac4j.saml.sso.impl.SAML2AuthnRequestBuilder;
import org.pac4j.saml.sso.impl.SAML2ScopingIdentityProvider;
import org.pac4j.saml.store.EmptyStoreFactory;
import org.pac4j.saml.store.SAMLMessageStoreFactory;
import org.pac4j.saml.util.SAML2HttpClientBuilder;
import org.pac4j.saml.util.SAML2UrlResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
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

    private KeystoreProperties keystore = new KeystoreProperties().setCertificatePrefix("saml-signing-cert")
        .setCertificateExpirationPeriod(Period.ofYears(20));

    private Resource identityProviderMetadataResource;

    private String identityProviderEntityId;

    private String serviceProviderEntityId;

    private long maximumAuthenticationLifetime;

    private long acceptedSkew = 300;

    private boolean forceAuth = false;

    private boolean passive = false;

    private String comparisonType = null;

    private boolean isPartialLogoutTreatedAsSuccess = true;

    private String authnRequestBindingType = SAMLConstants.SAML2_POST_BINDING_URI;

    private String authnRequestSubjectNameId;

    private String authnRequestSubjectNameIdFormat;

    private String responseBindingType = SAMLConstants.SAML2_POST_BINDING_URI;

    private String spLogoutRequestBindingType = SAMLConstants.SAML2_POST_BINDING_URI;

    private String spLogoutResponseBindingType = SAMLConstants.SAML2_POST_BINDING_URI;

    private List<String> authnContextClassRefs = new ArrayList<>();

    private String nameIdPolicyFormat = null;

    private boolean useNameQualifier = false;

    private boolean signMetadata;

    private Resource serviceProviderMetadataResource;

    private boolean forceServiceProviderMetadataGeneration;

    private SAMLMessageStoreFactory samlMessageStoreFactory = new EmptyStoreFactory();

    private SAML2MetadataGenerator metadataGenerator;

    private CredentialProvider credentialProvider;

    private SAML2ObjectBuilder<AuthnRequest> samlAuthnRequestBuilder = new SAML2AuthnRequestBuilder();

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

    private int assertionConsumerServiceIndex = -1;

    private int attributeConsumingServiceIndex = -1;

    private String providerName;

    private Supplier<List<XSAny>> authnRequestExtensions;

    private String attributeAsId;

    private Map<String, String> mappedAttributes = new LinkedHashMap<>();

    private URIComparator uriComparator = new BasicURLComparator();

    private String postLogoutURL;

    private SAML2MetadataResolver defaultIdentityProviderMetadataResolverSupplier;

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

    private List<String> supportedProtocols = List.of(SAMLConstants.SAML20P_NS);

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
        this.keystore.setCertificatePrefix("saml-signing-cert");
        this.keystore.setKeyStoreAlias(keyStoreAlias);
        this.keystore.setKeyStoreType(keyStoreType);
        this.keystore.setKeystoreResource(keystoreResource);
        this.keystore.setKeystorePassword(keystorePassword);
        this.keystore.setPrivateKeyPassword(privateKeyPassword);
        if (identityProviderMetadataResource instanceof UrlResource urlResource) {
            this.identityProviderMetadataResource = new SAML2UrlResource(urlResource.getURL(), this);
        } else {
            this.identityProviderMetadataResource = identityProviderMetadataResource;
        }
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
            if (StringUtils.isBlank(getServiceProviderEntityId())) {
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
        this.defaultIdentityProviderMetadataResolverSupplier = new SAML2IdentityProviderMetadataResolver(this);

        var keystoreGenerator = keystore.getKeystoreGenerator();
        if (keystoreGenerator == null) {
            if (this.keystore.getKeystoreResource() instanceof UrlResource) {
                keystoreGenerator = new SAML2HttpUrlKeystoreGenerator(this);
            } else {
                keystoreGenerator = new SAML2FileSystemKeystoreGenerator(this);
            }
            this.keystore.setKeystoreGenerator(keystoreGenerator);
        }
        if (keystoreGenerator.shouldGenerate()) {
            LOGGER.info("Generating keystore one for/via: {}", keystore.getKeystoreResource());
            keystoreGenerator.generate();
        }

        initSignatureSigningConfiguration();
    }

    @Deprecated
    public KeystoreGenerator getKeystoreGenerator() {
        return this.keystore.getKeystoreGenerator();
    }

    @Deprecated
    public void setKeystoreGenerator(final KeystoreGenerator keystoreGenerator) {
        this.keystore.setKeystoreGenerator(keystoreGenerator);
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
     * @deprecated use getKeystore().getKeystoreResource() instead of getKeystoreResource()
     */
    @Deprecated
    public Resource getKeystoreResource() {
        return keystore.getKeystoreResource();
    }

    /**
     * @deprecated use getKeystore().setKeystoreResource(resource) instead of setKeystoreResource(resource)
     */
    @Deprecated
    public void setKeystoreResource(final Resource resource) {
        keystore.setKeystoreResource(resource);
    }

    /**
     * @deprecated use getKeystore().getKeystorePassword() instead of getKeystorePassword()
     */
    @Deprecated
    public String getKeystorePassword() {
        return keystore.getKeystorePassword();
    }

    /**
     * @deprecated use getKeystore().setKeystorePassword(password) instead of setKeystorePassword(password)
     */
    @Deprecated
    public void setKeystorePassword(final String password) {
        keystore.setKeystorePassword(password);
    }

    /**
     * @deprecated use getKeystore().getPrivateKeyPassword() instead of getPrivateKeyPassword()
     */
    @Deprecated
    public String getPrivateKeyPassword() {
        return keystore.getPrivateKeyPassword();
    }

    /**
     * @deprecated use getKeystore().setPrivateKeyPassword(password) instead of setPrivateKeyPassword(password)
     */
    @Deprecated
    public void setPrivateKeyPassword(final String password) {
        keystore.setPrivateKeyPassword(password);
    }

    /**
     * @deprecated use getKeystore().getKeyStoreAlias() instead of getKeyStoreAlias()
     */
    @Deprecated
    public String getKeyStoreAlias() {
        return keystore.getKeyStoreAlias();
    }

    /**
     * @deprecated use getKeystore().setKeyStoreAlias(alias) instead of setKeyStoreAlias(alias)
     */
    @Deprecated
    public void setKeyStoreAlias(final String alias) {
        keystore.setKeyStoreAlias(alias);
    }

    /**
     * @deprecated use getKeystore().getKeyStoreType() instead of getKeyStoreType()
     */
    @Deprecated
    public String getKeyStoreType() {
        return keystore.getKeyStoreType();
    }

    /**
     * @deprecated use getKeystore().setKeyStoreType(type) instead of setKeyStoreType(type)
     */
    @Deprecated
    public void setKeyStoreType(final String type) {
        keystore.setKeyStoreType(type);
    }

    /**
     * @deprecated use getKeystore().isForceKeystoreGeneration() instead of isForceKeystoreGeneration()
     */
    @Deprecated
    public boolean isForceKeystoreGeneration() {
        return keystore.isForceKeystoreGeneration();
    }

    /**
     * @deprecated use getKeystore().setForceKeystoreGeneration(force) instead of setForceKeystoreGeneration(force)
     */
    @Deprecated
    public void setForceKeystoreGeneration(final boolean force) {
        keystore.setForceKeystoreGeneration(force);
    }

    /**
     * @deprecated use getKeystore().getCertificateNameToAppend() instead of getCertificateNameToAppend()
     */
    @Deprecated
    public String getCertificateNameToAppend() {
        return keystore.getCertificateNameToAppend();
    }

    /**
     * @deprecated use getKeystore().setCertificateNameToAppend(name) instead of setCertificateNameToAppend(name)
     */
    @Deprecated
    public void setCertificateNameToAppend(final String name) {
        keystore.setCertificateNameToAppend(name);
    }

    /**
     * @deprecated use getKeystore().getCertificateExpirationPeriod() instead of getCertificateExpirationPeriod()
     */
    @Deprecated
    public Period getCertificateExpirationPeriod() {
        return keystore.getCertificateExpirationPeriod();
    }

    /**
     * @deprecated use getKeystore().setCertificateExpirationPeriod(period) instead of setCertificateExpirationPeriod(period)
     */
    @Deprecated
    public void setCertificateExpirationPeriod(final Period period) {
        keystore.setCertificateExpirationPeriod(period);
    }

    /**
     * @deprecated use getKeystore().getCertificateSignatureAlg() instead of getCertificateSignatureAlg()
     */
    @Deprecated
    public String getCertificateSignatureAlg() {
        return keystore.getCertificateSignatureAlg();
    }

    /**
     * @deprecated use getKeystore().setCertificateSignatureAlg(alg) instead of setCertificateSignatureAlg(alg)
     */
    @Deprecated
    public void setCertificateSignatureAlg(final String alg) {
        keystore.setCertificateSignatureAlg(alg);
    }

    /**
     * @deprecated use getKeystore().getPrivateKeySize() instead of getPrivateKeySize()
     */
    @Deprecated
    public int getPrivateKeySize() {
        return keystore.getPrivateKeySize();
    }

    /**
     * @deprecated use getKeystore().setPrivateKeySize(size) instead of setPrivateKeySize(size)
     */
    @Deprecated
    public void setPrivateKeySize(final int size) {
        keystore.setPrivateKeySize(size);
    }

    /**
     * @deprecated use getKeystore().setKeystoreResourceFilepath(path) instead of setKeystoreResourceFilepath(path)
     */
    @Deprecated
    public void setKeystoreResourceFilepath(final String path) {
        keystore.setKeystoreResourceFilepath(path);
    }

    /**
     * @deprecated use getKeystore().setKeystoreResourceClasspath(path) instead of setKeystoreResourceClasspath(path)
     */
    @Deprecated
    public void setKeystoreResourceClasspath(final String path) {
        keystore.setKeystoreResourceClasspath(path);
    }

    /**
     * @deprecated use getKeystore().setKeystoreResourceUrl(url) instead of setKeystoreResourceUrl(url)
     */
    @Deprecated
    public void setKeystoreResourceUrl(final String url) {
        keystore.setKeystoreResourceUrl(url);
    }

    /**
     * @deprecated use getKeystore().setKeystorePath(path) instead of setKeystorePath(path)
     */
    @Deprecated
    public void setKeystorePath(final String path) {
        keystore.setKeystorePath(path);
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
        if (credentialProvider == null) {
            credentialProvider = new KeyStoreCredentialProvider(this);
        }
        return credentialProvider;
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

                generator.setRequestInitiatorLocation(resolveRequestInitiatorLocation());
                generator.setAssertionConsumerServiceUrl(resolveAssertionConsumerServiceUrl());

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

    public String resolveAssertionConsumerServiceUrl() {
        return StringUtils.defaultString(this.assertionConsumerServiceUrl, this.callbackUrl);
    }

    public String resolveRequestInitiatorLocation() {
        return StringUtils.defaultString(this.requestInitiatorUrl, this.callbackUrl);
    }

    /**
     * <p>determineSingleSignOutServiceUrl.</p>
     *
     * @param generator a {@link BaseSAML2MetadataGenerator} object
     */
    protected void determineSingleSignOutServiceUrl(final BaseSAML2MetadataGenerator generator) {
        val logoutUrl = StringUtils.defaultIfBlank(this.singleSignOutServiceUrl, callbackUrl);
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
        return Objects.requireNonNullElse(identityProviderMetadataResolver, defaultIdentityProviderMetadataResolverSupplier);
    }
}
