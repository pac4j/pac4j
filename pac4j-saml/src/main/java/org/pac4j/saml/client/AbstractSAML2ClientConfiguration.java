/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.pac4j.saml.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.xmlsec.config.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.impl.BasicSignatureSigningConfiguration;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.InitializableWebObject;
import org.pac4j.saml.storage.EmptyStorageFactory;
import org.pac4j.saml.storage.SAMLMessageStorageFactory;


/**
 * Abstract base class for various types of SAML Client configurations.
 * 
 * @author jkacer
 * @since 1.9.0
 */
public abstract class AbstractSAML2ClientConfiguration implements Cloneable {

    /** Default value of the Destination Binding Type. Used when a NULL is read from the database setup. */
    protected static final String DEFAULT_DESTINATION_BINDING_TYPE = SAMLConstants.SAML2_POST_BINDING_URI;

    /** Minimum allowed value of Maximum Authentication Lifetime, in seconds. */
    protected static final int MINIMUM_MAX_AUTHENTICATION_LIFETIME = 1;

    /** Default value of Maximum Authentication Lifetime, in seconds. Used when the supplied value is less than the defined minimum.*/
    protected static final int DEFAULT_MAX_AUTHENTICATION_LIFETIME = 3600;

	
    private String keystorePassword;

    private String privateKeyPassword;

    private String identityProviderEntityId;

    private String serviceProviderEntityId;

    private int maximumAuthenticationLifetime;

    private boolean forceAuth = false;

    private String comparisonType = null;

    private String destinationBindingType = DEFAULT_DESTINATION_BINDING_TYPE;

    private String authnContextClassRef = null;

    private String nameIdPolicyFormat = null;

    private String serviceProviderMetadataPath;

    private boolean forceServiceProviderMetadataGeneration;

    private SAMLMessageStorageFactory samlMessageStorageFactory = new EmptyStorageFactory();

    private Collection<String> blackListedSignatureSigningAlgorithms;
    private List<String> signatureAlgorithms;
    private List<String> signatureReferenceDigestMethods;
    private String signatureCanonicalizationAlgorithm;

    
    // ------------------------------------------------------------------------------------------------------------------------------------
    
    
    public AbstractSAML2ClientConfiguration() {
    	super();
    	
    	// Most properties will be set using their setters; except signature-related algorithm.
        final BasicSignatureSigningConfiguration config = DefaultSecurityConfigurationBootstrap.buildDefaultSignatureSigningConfiguration();
        this.blackListedSignatureSigningAlgorithms = new ArrayList<>(config.getBlacklistedAlgorithms());
        this.signatureAlgorithms = new ArrayList<>(config.getSignatureAlgorithms());
        this.signatureReferenceDigestMethods = new ArrayList<>(config.getSignatureReferenceDigestMethods());
        this.signatureReferenceDigestMethods.remove("http://www.w3.org/2001/04/xmlenc#sha512");
        this.signatureCanonicalizationAlgorithm = config.getSignatureCanonicalizationAlgorithm();
    }

    

    public void setIdentityProviderEntityId(final String identityProviderEntityId) {
        this.identityProviderEntityId = identityProviderEntityId;
    }

    public void setServiceProviderEntityId(final String serviceProviderEntityId) {
        this.serviceProviderEntityId = serviceProviderEntityId;
    }


    public void setKeystorePassword(final String keystorePassword) {
        this.keystorePassword = keystorePassword;
    }

    public void setPrivateKeyPassword(final String privateKeyPassword) {
        this.privateKeyPassword = privateKeyPassword;
    }

    public void setMaximumAuthenticationLifetime(final int maximumAuthenticationLifetime) {
		this.maximumAuthenticationLifetime = (maximumAuthenticationLifetime >= MINIMUM_MAX_AUTHENTICATION_LIFETIME ? maximumAuthenticationLifetime : DEFAULT_MAX_AUTHENTICATION_LIFETIME);
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
		this.destinationBindingType = (destinationBindingType != null ? destinationBindingType : DEFAULT_DESTINATION_BINDING_TYPE);
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

    public void setServiceProviderMetadataPath(final String serviceProviderMetadataPath) {
        this.serviceProviderMetadataPath = serviceProviderMetadataPath;
    }

    public void setForceServiceProviderMetadataGeneration(final boolean forceServiceProviderMetadataGeneration) {
        this.forceServiceProviderMetadataGeneration = forceServiceProviderMetadataGeneration;
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
        return serviceProviderMetadataPath;
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

    
    @Override
    public AbstractSAML2ClientConfiguration clone() throws CloneNotSupportedException {
    	return (AbstractSAML2ClientConfiguration) super.clone();
    }

    
	/**
	 * Initializes the configuration for a particular client.
	 * 
	 * @param clientName
	 *            Name of the client. The configuration can use the value or not.
	 * @param webContext
	 *            Web context to transport additional information to the configuration. May or may not be used.
	 */
	protected abstract void init(String clientName, WebContext webContext);
	
	
	/**
	 * Is it necessary to perform resolution of some data in order to get actual keystore content?
	 * Or does the configuration provide keystore data already resolved (i.e. ready to be used)?
	 * 
	 * @return True if needed, false otherwise.
	 * 
	 * @see #getKeystorePath()
	 * @see #getResolvedKeystoreData()
	 */
	public abstract boolean keystoreDataNeedResolution();
	
	
	/**
	 * Is it necessary to perform resolution of some data in order to get actual IdP metadata?
	 * Or does the configuration provide IdP metadata already resolved (i.e. ready to be used)?
	 * 
	 * @return True if needed, false otherwise.
	 * 
	 * @see #getIdentityProviderMetadataPath()
	 * @see #getResolvedIdentityProviderMetadata()
	 */
	public abstract boolean identityProviderMetadataNeedResolution();
	
	
	/**
	 * Does the configuration provide client name?
	 * Some types of configuration provide client names and the application does not know them until the configuration is read.
	 * 
	 * @return True if the configuration provides the client name.
	 * 
	 * @see #getClientName()
	 */
	public abstract boolean providesClientName();
	
	
	/**
	 * Returns path to the keystore.
	 * Should return a meaningful value if {@link #keystoreDataNeedResolution()} returns true.
	 * 
	 * @return Path to the keystore or {@code null}.
	 * 
	 * @see #keystoreDataNeedResolution()
	 */
	public abstract String getKeystorePath();
	
	
	/**
	 * Returns keystore binary data.
	 * Should return a meaningful value if {@link #keystoreDataNeedResolution()} returns false.
	 * 
	 * @return Keystore binary data or {@code null}.
	 * 
	 * @see #keystoreDataNeedResolution()
	 */
	public abstract byte[] getResolvedKeystoreData();
	
	
	/**
	 * Returns path to the identity provider metadata.
	 * Should return a meaningful value if {@link #identityProviderMetadataNeedResolution()} returns true.
	 * 
	 * @return Path to the IdP metadata or {@code null}.
	 * 
	 * @see #identityProviderMetadataNeedResolution()
	 */
	public abstract String getIdentityProviderMetadataPath();
	
	
	/**
	 * Returns IdP metadata as string.
	 * Should return a meaningful value if {@link #identityProviderMetadataNeedResolution()} returns false.
	 * 
	 * @return IdP metadata or {@code null}.
	 * 
	 * @see #identityProviderMetadataNeedResolution()
	 */
	public abstract String getResolvedIdentityProviderMetadata();

	
	/**
	 * Returns the client name, if provided.
	 * 
	 * @return The client name or {@code null}, if not provided.
	 * 
	 * @see #providesClientName()
	 */
	public abstract String getClientName();
	
}
