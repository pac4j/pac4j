package org.pac4j.saml.client;

import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.xmlsec.config.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.impl.BasicSignatureSigningConfiguration;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.io.Resource;
import org.pac4j.core.io.WritableResource;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.storage.EmptyStorageFactory;
import org.pac4j.saml.storage.SAMLMessageStorageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
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
 * The {@link SAML2ClientConfiguration} is responsible for...
 * capturing client settings and passing them around.
 *
 * @author Misagh Moayyed
 * @since 1.7
 */
public class SAML2ClientConfiguration implements Cloneable {
    private static final Logger LOGGER = LoggerFactory.getLogger(SAML2ClientConfiguration.class);

    private KeyStore keyStore;

    private Resource keystoreResource;

    private String keystorePassword;

    private String privateKeyPassword;

    private Resource identityProviderMetadataResource;

    private String identityProviderEntityId;

    private String serviceProviderEntityId;

    private int maximumAuthenticationLifetime;

    private boolean forceAuth = false;

    private boolean forceSignRedirectBindingAuthnRequest;

    private String comparisonType = null;

    private String destinationBindingType = SAMLConstants.SAML2_POST_BINDING_URI;

    private String authnContextClassRef = null;

    private String nameIdPolicyFormat = null;

    private WritableResource serviceProviderMetadataResource;

    private boolean forceServiceProviderMetadataGeneration;

    private SAMLMessageStorageFactory samlMessageStorageFactory = new EmptyStorageFactory();

    private boolean authnRequestSigned = true;

    public SAML2ClientConfiguration() {
    }

    private Collection<String> blackListedSignatureSigningAlgorithms;
    private List<String> signatureAlgorithms;
    private List<String> signatureReferenceDigestMethods;
    private String signatureCanonicalizationAlgorithm;
    private boolean wantsAssertionsSigned = true;

    private String keyStoreAlias;

    private String keyStoreType;

    public SAML2ClientConfiguration(final String keystorePath, final String keystorePassword,
                                    final String privateKeyPassword, final String identityProviderMetadataPath) {
        this(null, null, null, null, keystorePath, keystorePassword, privateKeyPassword, null, identityProviderMetadataPath, null, null);
    }

    public SAML2ClientConfiguration(final KeyStore keystore,
                                    final String keyStoreAlias,
                                    final String keyStoreType,
                                    final String privateKeyPassword,
                                    final Resource identityProviderMetadataResource) {
        this(keystore, keyStoreAlias, keyStoreType, null, null, null, privateKeyPassword,
                identityProviderMetadataResource, null, null, null);
    }

    public SAML2ClientConfiguration(final Resource keystoreResource, final String keyStoreAlias,
                                    final String keyStoreType, final String keystorePassword, final String privateKeyPassword,
                                    final Resource identityProviderMetadataResource) {
        this(null, keyStoreAlias, keyStoreType, keystoreResource, null, keystorePassword, privateKeyPassword,
                identityProviderMetadataResource, null, null, null);
    }

    private SAML2ClientConfiguration(final KeyStore keyStore, final String keyStoreAlias, final String keyStoreType,
                                     final Resource keystoreResource, final String keystorePath, final String keystorePassword,
                                     final String privateKeyPassword, final Resource identityProviderMetadataResource,
                                     final String identityProviderMetadataPath,
                                     final String identityProviderEntityId, final String serviceProviderEntityId) {
        this.keyStore = keyStore;
        this.keyStoreAlias = keyStoreAlias;
        this.keyStoreType = keyStoreType;
        this.keystoreResource = keystoreResource;
        if (this.keystoreResource == null) {
            this.keystoreResource = CommonHelper.getResource(keystorePath);
        }
        this.keystorePassword = keystorePassword;
        this.privateKeyPassword = privateKeyPassword;

        if (this.keystoreResource == null || !this.keystoreResource.exists()) {
            LOGGER.warn("Provided path to keystore does not exist. Creating one at {}", keystorePath);
            createKeystore();
        }
        this.identityProviderMetadataResource = identityProviderMetadataResource;
        if (this.identityProviderMetadataResource == null) {
            this.identityProviderMetadataResource = CommonHelper.getResource(identityProviderMetadataPath);
        }
        this.identityProviderEntityId = identityProviderEntityId;
        this.serviceProviderEntityId = serviceProviderEntityId;

        CommonHelper.assertNotBlank("keystorePassword", this.keystorePassword);
        CommonHelper.assertNotBlank("privateKeyPassword", this.privateKeyPassword);
        CommonHelper.assertTrue(
                this.identityProviderMetadataResource != null || CommonHelper.isNotBlank(identityProviderMetadataPath),
                "Either identityProviderMetadataResource or identityProviderMetadataPath must be provided");

        final BasicSignatureSigningConfiguration config = DefaultSecurityConfigurationBootstrap.buildDefaultSignatureSigningConfiguration();
        this.blackListedSignatureSigningAlgorithms = new ArrayList<>(config.getBlacklistedAlgorithms());
        this.signatureAlgorithms = new ArrayList<>(config.getSignatureAlgorithms());
        this.signatureReferenceDigestMethods = new ArrayList<>(config.getSignatureReferenceDigestMethods());
        this.signatureReferenceDigestMethods.remove("http://www.w3.org/2001/04/xmlenc#sha512");
        this.signatureCanonicalizationAlgorithm = config.getSignatureCanonicalizationAlgorithm();

    }

    public void setIdentityProviderMetadataPath(final String identityProviderMetadataPath) {
        this.identityProviderMetadataResource = CommonHelper.getResource(identityProviderMetadataPath);
    }

    public void setIdentityProviderMetadataResource(final Resource identityProviderMetadataResource) {
        this.identityProviderMetadataResource = identityProviderMetadataResource;
    }

    public void setIdentityProviderEntityId(final String identityProviderEntityId) {
        this.identityProviderEntityId = identityProviderEntityId;
    }

    public void setServiceProviderEntityId(final String serviceProviderEntityId) {
        this.serviceProviderEntityId = serviceProviderEntityId;
    }

    public void setKeystore(final KeyStore keyStore) {
        this.keyStore = keyStore;
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

    public void setKeystorePath(final String keystorePath) {
        this.keystoreResource = CommonHelper.getResource(keystorePath);
    }

    public void setKeystorePassword(final String keystorePassword) {
        this.keystorePassword = keystorePassword;
    }

    public void setPrivateKeyPassword(final String privateKeyPassword) {
        this.privateKeyPassword = privateKeyPassword;
    }

    public void setMaximumAuthenticationLifetime(final int maximumAuthenticationLifetime) {
        this.maximumAuthenticationLifetime = maximumAuthenticationLifetime;
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

    public void setServiceProviderMetadataPath(final String serviceProviderMetadataPath) {
        this.serviceProviderMetadataResource = (WritableResource) CommonHelper.getResource(serviceProviderMetadataPath);
    }

    public void setServiceProviderMetadataResource(final WritableResource serviceProviderMetadataResource) {
        this.serviceProviderMetadataResource = serviceProviderMetadataResource;
    }

    public void setForceServiceProviderMetadataGeneration(final boolean forceServiceProviderMetadataGeneration) {
        this.forceServiceProviderMetadataGeneration = forceServiceProviderMetadataGeneration;
    }

    public String getIdentityProviderMetadataPath() {
        return identityProviderMetadataResource.getFilename();
    }

    public Resource getIdentityProviderMetadataResource() {
        return this.identityProviderMetadataResource;
    }

    public KeyStore getKeyStore() {
        return keyStore;
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

    public String getKeystorePath() {
        // if keystore is specified you won't specify keystoreResource
        if (keystoreResource != null) {
            return keystoreResource.getFilename();
        } else {
            return null;
        }
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public String getPrivateKeyPassword() {
        return privateKeyPassword;
    }

    public String getIdentityProviderEntityId() {
        return identityProviderEntityId;
    }

    public String getServiceProviderEntityId() {
        return serviceProviderEntityId;
    }

    public int getMaximumAuthenticationLifetime() {
        return maximumAuthenticationLifetime;
    }

    public String getServiceProviderMetadataPath() {
        if (serviceProviderMetadataResource != null) {
            return serviceProviderMetadataResource.getFilename();
        }
        return null;
    }

    public WritableResource getServiceProviderMetadataResource() {
        return serviceProviderMetadataResource;
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

    public boolean getWantsAssertionsSigned() {
        return this.wantsAssertionsSigned;
    }

    public void setWantsAssertionsSigned(boolean wantsAssertionsSigned) {
        this.wantsAssertionsSigned = wantsAssertionsSigned;
    }

    @Override
    public SAML2ClientConfiguration clone() {
        try {
            return (SAML2ClientConfiguration) super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isForceSignRedirectBindingAuthnRequest() {
        return forceSignRedirectBindingAuthnRequest;
    }

    public void setForceSignRedirectBindingAuthnRequest(final boolean forceSignRedirectBindingAuthnRequest) {
        this.forceSignRedirectBindingAuthnRequest = forceSignRedirectBindingAuthnRequest;
    }

    public boolean isAuthnRequestSigned() {
        return authnRequestSigned;
    }
    
    
	/**
	 * Initializes the configuration for a particular client.
	 * 
	 * @param clientName
	 *            Name of the client. The configuration can use the value or not.
	 * @param context
	 *            Web context to transport additional information to the configuration.
	 */
    protected void init(final String clientName, final WebContext context) {
    	// Intentionally left empty
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

            try (FileOutputStream fos = new FileOutputStream(this.keystoreResource.getFile().getCanonicalPath())) {
                ks.store(fos, password);
                fos.flush();
            }

            LOGGER.info("Created keystore {} with key alias {} ",
                    keystoreResource.getFile().getCanonicalPath(),
                    ks.aliases().nextElement());

            this.keyStore = ks;
        } catch (final Exception e) {
            throw new SAMLException("Could not create keystore", e);
        }
    }
}
