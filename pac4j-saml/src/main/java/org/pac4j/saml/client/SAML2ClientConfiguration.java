package org.pac4j.saml.client;

import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.xmlsec.config.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.impl.BasicSignatureSigningConfiguration;
import org.pac4j.core.io.Resource;
import org.pac4j.core.io.WritableResource;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.storage.EmptyStorageFactory;
import org.pac4j.saml.storage.SAMLMessageStorageFactory;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The {@link SAML2ClientConfiguration} is responsible for...
 * capturing client settings and passing them around.
 *
 * @author Misagh Moayyed
 * @since 1.7
 */
public final class SAML2ClientConfiguration implements Cloneable {
	private KeyStore keyStore;

    private Resource keystoreResource;

    private String keystorePassword;

    private String privateKeyPassword;

	private Resource identityProviderMetadataResource;

    private String identityProviderEntityId;

    private String serviceProviderEntityId;

    private int maximumAuthenticationLifetime;

    private boolean forceAuth = false;

    private String comparisonType = null;

    private String destinationBindingType = SAMLConstants.SAML2_POST_BINDING_URI;

    private String authnContextClassRef = null;

    private String nameIdPolicyFormat = null;

	private WritableResource serviceProviderMetadataResource;

    private boolean forceServiceProviderMetadataGeneration;

    private SAMLMessageStorageFactory samlMessageStorageFactory = new EmptyStorageFactory();

    public SAML2ClientConfiguration() {}


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
        return serviceProviderMetadataResource.getFilename();
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
}
