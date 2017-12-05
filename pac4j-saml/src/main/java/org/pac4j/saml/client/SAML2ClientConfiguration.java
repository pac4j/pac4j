package org.pac4j.saml.client;

import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.xmlsec.config.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.impl.BasicSignatureSigningConfiguration;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableObject;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.storage.EmptyStorageFactory;
import org.pac4j.saml.storage.SAMLMessageStorageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.*;

import java.io.FileOutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * The {@link SAML2ClientConfiguration} is responsible for capturing client settings and passing them around.
 *
 * @author Misagh Moayyed
 * @since 1.7
 */
public class SAML2ClientConfiguration extends InitializableObject {

    private static final Logger LOGGER = LoggerFactory.getLogger(SAML2ClientConfiguration.class);

    protected static final String RESOURCE_PREFIX = "resource:";
    protected static final String CLASSPATH_PREFIX = "classpath:";
    protected static final String FILE_PREFIX = "file:";

    private Resource keystoreResource;

    private String keystorePassword;

    private String privateKeyPassword;

    private Resource identityProviderMetadataResource;

    private String identityProviderEntityId;

    private String serviceProviderEntityId;

    private int maximumAuthenticationLifetime;

    private boolean forceAuth = false;
    private boolean passive = false;

    private String comparisonType = null;

    private String destinationBindingType = SAMLConstants.SAML2_POST_BINDING_URI;

    private String authnContextClassRef = null;

    private String nameIdPolicyFormat = null;

    private WritableResource serviceProviderMetadataResource;

    private boolean forceServiceProviderMetadataGeneration;

    private SAMLMessageStorageFactory samlMessageStorageFactory = new EmptyStorageFactory();

    private boolean authnRequestSigned;

    private Collection<String> blackListedSignatureSigningAlgorithms;
    private List<String> signatureAlgorithms;
    private List<String> signatureReferenceDigestMethods;
    private String signatureCanonicalizationAlgorithm;
    private boolean wantsAssertionsSigned = true;

    private String keyStoreAlias;

    private String keyStoreType;

    private int assertionConsumerServiceIndex = -1;

    private int attributeConsumingServiceIndex = -1;

    public SAML2ClientConfiguration() {}

    public SAML2ClientConfiguration(final String keystorePath, final String keystorePassword, final String privateKeyPassword,
                                    final String identityProviderMetadataPath) {
        this(null, null, mapPathToResource(keystorePath), keystorePassword, privateKeyPassword,
                mapPathToResource(identityProviderMetadataPath), null, null);
    }

    public SAML2ClientConfiguration(final Resource keystoreResource, final String keystorePassword, final String privateKeyPassword,
                                    final Resource identityProviderMetadataResource) {
        this(null, null, keystoreResource, keystorePassword, privateKeyPassword,
                identityProviderMetadataResource, null, null);
    }

    public SAML2ClientConfiguration(final Resource keystoreResource, final String keyStoreAlias,
                                    final String keyStoreType, final String keystorePassword, final String privateKeyPassword,
                                    final Resource identityProviderMetadataResource) {
        this(keyStoreAlias, keyStoreType, keystoreResource, keystorePassword, privateKeyPassword,
                identityProviderMetadataResource, null, null);
    }

    private SAML2ClientConfiguration(final String keyStoreAlias, final String keyStoreType,
                                     final Resource keystoreResource, final String keystorePassword,
                                     final String privateKeyPassword, final Resource identityProviderMetadataResource,
                                     final String identityProviderEntityId, final String serviceProviderEntityId) {
        this.keyStoreAlias = keyStoreAlias;
        this.keyStoreType = keyStoreType;
        this.keystoreResource = keystoreResource;
        this.keystorePassword = keystorePassword;
        this.privateKeyPassword = privateKeyPassword;
        this.identityProviderMetadataResource = identityProviderMetadataResource;
        this.identityProviderEntityId = identityProviderEntityId;
        this.serviceProviderEntityId = serviceProviderEntityId;
    }

    @Override
    protected void internalInit() {
        CommonHelper.assertNotNull("keystoreResource", this.keystoreResource);
        CommonHelper.assertNotBlank("keystorePassword", this.keystorePassword);
        CommonHelper.assertNotBlank("privateKeyPassword", this.privateKeyPassword);
        CommonHelper.assertNotNull("identityProviderMetadataResource", this.identityProviderMetadataResource);

        if (!this.keystoreResource.exists()) {
            if (this.keystoreResource instanceof WritableResource) {
                LOGGER.warn("Provided keystoreResource does not exist. Creating one for: {}", this.keystoreResource);
                createKeystore();
            } else {
                throw new TechnicalException("Provided keystoreResource does not exist and cannot be created");
            }
        }

        final BasicSignatureSigningConfiguration config = DefaultSecurityConfigurationBootstrap.buildDefaultSignatureSigningConfiguration();
        this.blackListedSignatureSigningAlgorithms = new ArrayList<>(config.getBlacklistedAlgorithms());
        this.signatureAlgorithms = new ArrayList<>(config.getSignatureAlgorithms());
        this.signatureReferenceDigestMethods = new ArrayList<>(config.getSignatureReferenceDigestMethods());
        this.signatureReferenceDigestMethods.remove("http://www.w3.org/2001/04/xmlenc#sha512");
        this.signatureCanonicalizationAlgorithm = config.getSignatureCanonicalizationAlgorithm();
    }

    public void setIdentityProviderMetadataResource(final Resource identityProviderMetadataResource) {
        this.identityProviderMetadataResource = identityProviderMetadataResource;
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

    protected static UrlResource newUrlResource(final String url) {
        try {
            return new UrlResource(url);
        } catch (final MalformedURLException e) {
            throw new TechnicalException(e);
        }
    }

    protected static Resource mapPathToResource(final String path) {
        CommonHelper.assertNotBlank("path", path);
        if (path.startsWith(RESOURCE_PREFIX)) {
            return new ClassPathResource(path.substring(RESOURCE_PREFIX.length()));
        } else if (path.startsWith(CLASSPATH_PREFIX)) {
            return new ClassPathResource(path.substring(CLASSPATH_PREFIX.length()));
        } else if (path.startsWith(HttpConstants.SCHEME_HTTP) || path.startsWith(HttpConstants.SCHEME_HTTPS)) {
            return newUrlResource(path);
        } else if (path.startsWith(FILE_PREFIX)) {
            return new FileSystemResource(path.substring(FILE_PREFIX.length()));
        } else {
            return new FileSystemResource(path);
        }
    }

    public Resource getIdentityProviderMetadataResource() {
        return this.identityProviderMetadataResource;
    }

    public void setIdentityProviderEntityId(final String identityProviderEntityId) {
        this.identityProviderEntityId = identityProviderEntityId;
    }

    public String getIdentityProviderEntityId() {
        return identityProviderEntityId;
    }

    public void setKeystoreAlias(final String keyStoreAlias) {
        this.keyStoreAlias = keyStoreAlias;
    }

    public void setKeystoreType(final String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }

    public void setKeystoreResource(final Resource keystoreResource) {
        this.keystoreResource = keystoreResource;
    }

    public void setKeystoreResourceFilepath(final String path) {
        this.keystoreResource = new FileSystemResource(path);
    }

    public void setKeystoreResourceClasspath(final String path) {
        this.keystoreResource = new ClassPathResource(path);
    }

    public void setKeystoreResourceUrl(final String url) {
        this.keystoreResource = newUrlResource(url);
    }

    public void setKeystorePath(final String path) {
        this.keystoreResource = mapPathToResource(path);
    }

    public void setKeystorePassword(final String keystorePassword) {
        this.keystorePassword = keystorePassword;
    }

    public void setPrivateKeyPassword(final String privateKeyPassword) {
        this.privateKeyPassword = privateKeyPassword;
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

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public String getPrivateKeyPassword() {
        return privateKeyPassword;
    }

    public void setServiceProviderMetadataResource(final WritableResource serviceProviderMetadataResource) {
        this.serviceProviderMetadataResource = serviceProviderMetadataResource;
    }

    public void setServiceProviderMetadataResourceFilepath(final String path) {
        this.serviceProviderMetadataResource = new FileSystemResource(path);
    }

    public void setServiceProviderMetadataPath(final String path) {
        final Resource resource = mapPathToResource(path);
        if (!(resource instanceof WritableResource)) {
            throw new TechnicalException(path + " must be a writable resource");
        } else {
            this.serviceProviderMetadataResource = (WritableResource) resource;
        }
    }

    public void setForceServiceProviderMetadataGeneration(final boolean forceServiceProviderMetadataGeneration) {
        this.forceServiceProviderMetadataGeneration = forceServiceProviderMetadataGeneration;
    }

    public WritableResource getServiceProviderMetadataResource() {
        return serviceProviderMetadataResource;
    }

    public void setServiceProviderEntityId(final String serviceProviderEntityId) {
        this.serviceProviderEntityId = serviceProviderEntityId;
    }

    public String getServiceProviderEntityId() {
        return serviceProviderEntityId;
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

    public String getDestinationBindingType() {
        return destinationBindingType;
    }

    public void setDestinationBindingType(final String destinationBindingType) {
        this.destinationBindingType = destinationBindingType;
    }

    public String getAuthnContextClassRef() {
        return authnContextClassRef;
    }

    public void setAuthnContextClassRef(final String authnContextClassRef) {
        this.authnContextClassRef = authnContextClassRef;
    }

    public String getNameIdPolicyFormat() {
        return nameIdPolicyFormat;
    }

    public void setNameIdPolicyFormat(final String nameIdPolicyFormat) {
        this.nameIdPolicyFormat = nameIdPolicyFormat;
    }

    public int getMaximumAuthenticationLifetime() {
        return maximumAuthenticationLifetime;
    }

    public void setMaximumAuthenticationLifetime(final int maximumAuthenticationLifetime) {
        this.maximumAuthenticationLifetime = maximumAuthenticationLifetime;
    }

    public boolean isForceServiceProviderMetadataGeneration() {
        return forceServiceProviderMetadataGeneration;
    }

    public SAMLMessageStorageFactory getSamlMessageStorageFactory() {
        return samlMessageStorageFactory;
    }

    public void setSamlMessageStorageFactory(final SAMLMessageStorageFactory samlMessageStorageFactory) {
        this.samlMessageStorageFactory = samlMessageStorageFactory;
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

    public void setWantsAssertionsSigned(boolean wantsAssertionsSigned) {
        this.wantsAssertionsSigned = wantsAssertionsSigned;
    }

    public boolean isAuthnRequestSigned() {
        return authnRequestSigned;
    }

    public void setAuthnRequestSigned(final boolean authnRequestSigned) {
        this.authnRequestSigned = authnRequestSigned;
    }

    public int getAttributeConsumingServiceIndex() {
        return attributeConsumingServiceIndex;
    }

    public void setAttributeConsumingServiceIndex(final int attributeConsumingServiceIndex) {
        this.attributeConsumingServiceIndex = attributeConsumingServiceIndex;
    }

    /**
     * Initializes the configuration for a particular client.
     *
     * @param clientName
     *            Name of the client. The configuration can use the value or not.
     */
    protected void init(final String clientName) {
        init();
    }

    private void createKeystore() {
        try {
            Security.addProvider(new BouncyCastleProvider());

            if (CommonHelper.isBlank(this.keyStoreAlias)) {
                this.keyStoreAlias = getClass().getSimpleName();
                LOGGER.warn("Using keystore alias {}", this.keyStoreAlias);
            }

            if (CommonHelper.isBlank(this.keyStoreType)) {
                this.keyStoreType = KeyStore.getDefaultType();
                LOGGER.warn("Using keystore type {}", this.keyStoreType);
            }

            final KeyStore ks = KeyStore.getInstance(this.keyStoreType);
            final char[] password = this.keystorePassword.toCharArray();
            ks.load(null, password);

            final KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            final KeyPair kp = kpg.genKeyPair();

            final X509V3CertificateGenerator cert = new X509V3CertificateGenerator();
            cert.setSerialNumber(BigInteger.valueOf(1));
            final String dn = InetAddress.getLocalHost().getHostName();
            cert.setSubjectDN(new X509Principal("CN=" + dn));
            cert.setIssuerDN(new X509Principal("CN=" + dn));
            cert.setPublicKey(kp.getPublic());
            cert.setNotBefore(new Date());

            final Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.YEAR, 1);
            cert.setNotAfter(c.getTime());

            cert.setSignatureAlgorithm("SHA1WithRSA");
            final PrivateKey signingKey = kp.getPrivate();
            final X509Certificate certificate = cert.generate(signingKey, "BC");

            ks.setKeyEntry(this.keyStoreAlias, signingKey, password, new Certificate[]{certificate});

            try (final FileOutputStream fos = new FileOutputStream(this.keystoreResource.getFile().getCanonicalPath())) {
                ks.store(fos, password);
                fos.flush();
            }

            LOGGER.info("Created keystore {} with key alias {} ",
                    keystoreResource.getFile().getCanonicalPath(),
                    ks.aliases().nextElement());
        } catch (final Exception e) {
            throw new SAMLException("Could not create keystore", e);
        }
    }
}
