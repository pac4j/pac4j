package org.pac4j.saml.config;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.asn1.x509.V3TBSCertificateGenerator;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.xmlsec.config.impl.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.impl.BasicSignatureSigningConfiguration;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.logout.handler.DefaultLogoutHandler;
import org.pac4j.core.logout.handler.LogoutHandler;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableObject;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.metadata.SAML2ServiceProvicerRequestedAttribute;
import org.pac4j.saml.storage.EmptyStorageFactory;
import org.pac4j.saml.storage.SAMLMessageStorageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.WritableResource;

/**
 * The class is responsible for capturing client settings and passing them around.
 *
 * @author Misagh Moayyed
 * @author Jerome Leleu
 * @since 1.7
 */
public class SAML2Configuration extends InitializableObject {

    private static final Logger LOGGER = LoggerFactory.getLogger(SAML2Configuration.class);

    protected static final String RESOURCE_PREFIX = "resource:";
    protected static final String CLASSPATH_PREFIX = "classpath:";
    protected static final String FILE_PREFIX = "file:";
    protected static final String DEFAULT_PROVIDER_NAME = "pac4j-saml";
    protected static final String CERTIFICATES_PREFIX =  "saml-signing-cert";

    private Resource keystoreResource;

    private String keystorePassword;

    private String privateKeyPassword;

    private String certificateNameToAppend;

    private Resource identityProviderMetadataResource;

    private String identityProviderEntityId;

    private String serviceProviderEntityId;

    private int maximumAuthenticationLifetime = 3600;

    private int acceptedSkew = 300;

    private boolean forceAuth = false;
    private boolean passive = false;

    private String comparisonType = null;

    private String authnRequestBindingType = SAMLConstants.SAML2_POST_BINDING_URI;

    private String spLogoutRequestBindingType = SAMLConstants.SAML2_POST_BINDING_URI;

    private String spLogoutResponseBindingType = SAMLConstants.SAML2_POST_BINDING_URI;

    private List<String> authnContextClassRefs = null;

    private String nameIdPolicyFormat = null;

    private boolean useNameQualifier = false;

    private boolean signMetadata;

    private WritableResource serviceProviderMetadataResource;

    private boolean forceServiceProviderMetadataGeneration;

    private boolean forceKeystoreGeneration;

    private SAMLMessageStorageFactory samlMessageStorageFactory = new EmptyStorageFactory();

    private boolean authnRequestSigned;

    private boolean spLogoutRequestSigned;

    private final List<SAML2ServiceProvicerRequestedAttribute> requestedServiceProviderAttributes = new ArrayList<>();

    private Collection<String> blackListedSignatureSigningAlgorithms;
    private List<String> signatureAlgorithms;
    private List<String> signatureReferenceDigestMethods;
    private String signatureCanonicalizationAlgorithm;
    private boolean wantsAssertionsSigned = false;
    private boolean allSignatureValidationDisabled = false;

    private String keyStoreAlias;

    private String keyStoreType;

    private int assertionConsumerServiceIndex = -1;

    private int attributeConsumingServiceIndex = -1;

    private String providerName;

    private Supplier<List<XSAny>> authnRequestExtensions;

    private String attributeAsId;

    private Map<String, String> mappedAttributes = new LinkedHashMap<>();

    private LogoutHandler logoutHandler;

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

    @Override
    protected void internalInit() {
        CommonHelper.assertNotNull("keystoreResource", this.keystoreResource);
        CommonHelper.assertNotBlank("keystorePassword", this.keystorePassword);
        CommonHelper.assertNotBlank("privateKeyPassword", this.privateKeyPassword);
        CommonHelper.assertNotNull("identityProviderMetadataResource", this.identityProviderMetadataResource);

        if (!this.keystoreResource.exists() || this.forceKeystoreGeneration) {
            if (this.keystoreResource instanceof WritableResource) {
                LOGGER.warn("Provided keystoreResource does not exist or keystore generation is forced. Creating one for: {}",
                    this.keystoreResource);
                createKeystore();
            } else {
                throw new TechnicalException("Provided keystoreResource does not exist and cannot be created");
            }
        }

        if (logoutHandler == null) {
            logoutHandler = new DefaultLogoutHandler();
        }

        initSignatureSigningConfiguration();
    }

    public List<SAML2ServiceProvicerRequestedAttribute> getRequestedServiceProviderAttributes() {
        return requestedServiceProviderAttributes;
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

    public boolean isForceKeystoreGeneration() {
        return forceKeystoreGeneration;
    }

    public void setForceKeystoreGeneration(final boolean forceKeystoreGeneration) {
        this.forceKeystoreGeneration = forceKeystoreGeneration;
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

    public int getAcceptedSkew() {
        return acceptedSkew;
    }

    public void setAcceptedSkew(final int acceptedSkew) {
        this.acceptedSkew = acceptedSkew;
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

    public String getCertificateNameToAppend() {
        return certificateNameToAppend;
    }

    public void setCertificateNameToAppend(final String certificateNameToAppend) {
        this.certificateNameToAppend = certificateNameToAppend;
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

    /**
     * Use {@link #getAuthnRequestBindingType()}.
     *
     * @return the authn request binding type
     */
    @Deprecated
    public String getDestinationBindingType() {
        return getAuthnRequestBindingType();
    }

    /**
     * Use {@link #setAuthnRequestBindingType(String)}.
     *
     * @param destinationBindingType the authn request binding type
     */
    @Deprecated
    public void setDestinationBindingType(final String destinationBindingType) {
        setAuthnRequestBindingType(destinationBindingType);
    }

    public String getAuthnRequestBindingType() {
        return authnRequestBindingType;
    }

    public void setAuthnRequestBindingType(final String authnRequestBindingType) {
        this.authnRequestBindingType = authnRequestBindingType;
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

    /**
     * Use {@link #getAuthnContextClassRefs()}.
     *
     * @return the authn context class ref
     */
    @Deprecated
    public String getAuthnContextClassRef() {
        if (authnContextClassRefs != null && !authnContextClassRefs.isEmpty()) {
            return authnContextClassRefs.get(0);
        }
        return null;
    }

    /**
     * Use {@link #setAuthnContextClassRefs(List)}.
     *
     * @param authnContextClassRef the authn context class ref
     */
    @Deprecated
    public void setAuthnContextClassRef(final String authnContextClassRef) {
        this.authnContextClassRefs = Arrays.asList(authnContextClassRef);
    }

    public List<String> getAuthnContextClassRefs() {
        return authnContextClassRefs;
    }

    public void setAuthnContextClassRefs(final List<String> authnContextClassRefs) {
        this.authnContextClassRefs = authnContextClassRefs;
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

    public void setWantsAssertionsSigned(final boolean wantsAssertionsSigned) {
        this.wantsAssertionsSigned = wantsAssertionsSigned;
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
    public void setAllSignatureValidationDisabled(boolean allSignatureValidationDisabled) {
        this.allSignatureValidationDisabled = allSignatureValidationDisabled;
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

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public Supplier<List<XSAny>> getAuthnRequestExtensions() {
        return authnRequestExtensions;
    }

    public void setAuthnRequestExtensions(Supplier<List<XSAny>> authnRequestExtensions) {
        this.authnRequestExtensions = authnRequestExtensions;
    }

    public String getAttributeAsId() {
        return attributeAsId;
    }

    public void setAttributeAsId(String attributeAsId) {
        this.attributeAsId = attributeAsId;
    }

    public boolean isUseNameQualifier() {
        return useNameQualifier;
    }

    public void setUseNameQualifier(boolean useNameQualifier) {
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

    public LogoutHandler findLogoutHandler() {
        init();

        return logoutHandler;
    }

    /**
     * Initializes the configuration for a particular client.
     *
     * @param clientName Name of the client. The configuration can use the value or not.
     */
    public void init(final String clientName) {
        init();
    }

    /**
     * Generate a self-signed certificate for dn using the provided signature algorithm and key pair.
     *
     * @param dn       X.500 name to associate with certificate issuer/subject.
     * @param sigName  name of the signature algorithm to use.
     * @param sigAlgID algorithm ID associated with the signature algorithm name.
     * @param keyPair  the key pair to associate with the certificate.
     * @return an X509Certificate containing the public key in keyPair.
     * @throws Exception
     */
    private X509Certificate createSelfSignedCert(X500Name dn, String sigName, AlgorithmIdentifier sigAlgID, KeyPair keyPair)
        throws Exception {
        final V3TBSCertificateGenerator certGen = new V3TBSCertificateGenerator();

        certGen.setSerialNumber(new ASN1Integer(BigInteger.valueOf(1)));
        certGen.setIssuer(dn);
        certGen.setSubject(dn);

        certGen.setStartDate(new Time(new Date(System.currentTimeMillis() - 1000L)));

        final Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.YEAR, 1);
        certGen.setEndDate(new Time(c.getTime()));

        certGen.setSignature(sigAlgID);
        certGen.setSubjectPublicKeyInfo(SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded()));

        Signature sig = Signature.getInstance(sigName);

        sig.initSign(keyPair.getPrivate());

        sig.update(certGen.generateTBSCertificate().getEncoded(ASN1Encoding.DER));

        TBSCertificate tbsCert = certGen.generateTBSCertificate();

        ASN1EncodableVector v = new ASN1EncodableVector();

        v.add(tbsCert);
        v.add(sigAlgID);
        v.add(new DERBitString(sig.sign()));

        final X509Certificate cert = (X509Certificate) CertificateFactory.getInstance("X.509")
            .generateCertificate(new ByteArrayInputStream(new DERSequence(v).getEncoded(ASN1Encoding.DER)));

        // check the certificate - this will confirm the encoded sig algorithm ID is correct.
        cert.verify(keyPair.getPublic());

        return cert;
    }

    private void createKeystore() {
        try {
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

            final String sigAlgName = "SHA1WithRSA";
            final AlgorithmIdentifier sigAlgID = new AlgorithmIdentifier(PKCSObjectIdentifiers.sha1WithRSAEncryption, DERNull.INSTANCE);

            final String dn = InetAddress.getLocalHost().getHostName();
            final PrivateKey signingKey = kp.getPrivate();
            final X509Certificate certificate = createSelfSignedCert(new X500Name("CN=" + dn), sigAlgName, sigAlgID, kp);

            final char[] keyPassword = this.privateKeyPassword.toCharArray();
            ks.setKeyEntry(this.keyStoreAlias, signingKey, keyPassword, new Certificate[]{certificate});

            final File keystoreFile = this.keystoreResource.getFile();
            try (final FileOutputStream fos = new FileOutputStream(keystoreFile.getCanonicalPath())) {
                ks.store(fos, password);
                fos.flush();
            }

            final File signingCertEncoded = getSigningBase64CertificatePath();
            writeEncodedCertificateToFile(signingCertEncoded, certificate.getEncoded());

            final File signingCertBinary = getSigningBinaryCertificatePath();
            writeBinaryCertificateToFile(signingCertBinary, certificate.getEncoded());

            final File signingKeyEncoded = getSigningKeyFile();
            writeEncodedCertificateToFile(signingKeyEncoded, signingKey.getEncoded());

            LOGGER.info("Created keystore {} with key alias {} ",
                keystoreResource.getFile().getCanonicalPath(),
                ks.aliases().nextElement());
        } catch (final Exception e) {
            throw new SAMLException("Could not create keystore", e);
        }
    }

    public File getSigningBinaryCertificatePath() throws IOException {
        return new File(keystoreResource.getFile().getParentFile(), getNormalizedCertificateName() + ".crt");
    }

    public File getSigningBase64CertificatePath() throws IOException {
        return new File(keystoreResource.getFile().getParentFile(), getNormalizedCertificateName() + ".pem");
    }

    public File getSigningKeyFile() throws IOException {
        return new File(keystoreResource.getFile().getParentFile(), getNormalizedCertificateName() + ".key");
    }

    private String normalizedCertificateName;

    /**
     *  Sanitize String to use it as fileName for Signing Certificate Names
     */
    private String getNormalizedCertificateName() {
        if (this.normalizedCertificateName == null) {
            StringBuilder certName =  new StringBuilder(CERTIFICATES_PREFIX);
            if (CommonHelper.isNotBlank(this.certificateNameToAppend)) {
                certName.append("-");
                certName.append(this.certificateNameToAppend.replaceAll("[^a-zA-Z0-9-_\\.]", ""));
            }
            this.normalizedCertificateName = certName.toString();
        }
        return this.normalizedCertificateName;
    }

    private static void writeBinaryCertificateToFile(final File file, final byte[] certificate) {
        if (file.exists()) {
            final boolean res = file.delete();
            LOGGER.debug("Deleted file [{}]:{}", file, res);
        }
        try (final FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(certificate);
            fos.flush();
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private static void writeEncodedCertificateToFile(final File file, final byte[] certificate) {
        if (file.exists()) {
            final boolean res = file.delete();
            LOGGER.debug("Deleted file [{}]:{}", file, res);
        }
        try (final PemWriter pemWriter = new PemWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            final PemObject pemObject = new PemObject(file.getName(), certificate);
            pemWriter.writeObject(pemObject);
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void initSignatureSigningConfiguration() {
        // Bootstrap signature signing configuration if not manually set
        final BasicSignatureSigningConfiguration config = DefaultSecurityConfigurationBootstrap
            .buildDefaultSignatureSigningConfiguration();
        if (this.blackListedSignatureSigningAlgorithms == null) {
            this.blackListedSignatureSigningAlgorithms = new ArrayList<>(
                config.getBlacklistedAlgorithms());
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
}
