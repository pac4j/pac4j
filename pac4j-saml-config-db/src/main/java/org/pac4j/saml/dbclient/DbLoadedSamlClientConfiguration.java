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
package org.pac4j.saml.dbclient;

import org.opensaml.saml.common.xml.SAMLConstants;
import org.pac4j.saml.client.SAML2ClientConfiguration;
import org.pac4j.saml.storage.EmptyStorageFactory;
import org.pac4j.saml.storage.SAMLMessageStorageFactory;


/**
 * An alternative to {@link SAML2ClientConfiguration}. Unlike {@link SAML2ClientConfiguration}, this class does not use paths to resources.
 * Instead, it contains the data directly.
 *
 * Differences from {@link SAML2ClientConfiguration}:
 * <ul>
 * <li>The JKS keystore - contained as a byte array, not a path to a resource</li>
 * <li>IdP metadata - contained as a string, not a path to a resource</li>
 * <li>SP metadata - dropped completely</li>
 * <li>Client name - added</li>
 * </ul>
 *
 * TODO: It would be nice to have a common interface and maybe a common abstract parent too.
 * 
 * @author jkacer
 */
public class DbLoadedSamlClientConfiguration implements Cloneable {
	
    /** Default value of the Destination Binding Type. Used when a NULL is read from the database setup. */
    private static final String DEFAULT_DESTINATION_BINDING_TYPE = SAMLConstants.SAML2_POST_BINDING_URI;
    
    /** Minimum allowed value of Maximum Authentication Lifetime, in seconds. */
    private static final int MINIMUM_MAX_AUTHENTICATION_LIFETIME = 1;

    /** Default value of Maximum Authentication Lifetime, in seconds. Used when the supplied value is less than the defined minimum.*/
    private static final int DEFAULT_MAX_AUTHENTICATION_LIFETIME = 3600;

    
	/** Binary data of a JKS keystore. */
	private byte[] keystoreBinaryData;

    private String keystorePassword;

    private String privateKeyPassword;

    /** IdP metadata. */
    private String identityProviderMetadata;

    private String identityProviderEntityId;

    private String serviceProviderEntityId;

    private int maximumAuthenticationLifetime = DEFAULT_MAX_AUTHENTICATION_LIFETIME;

    private boolean forceAuth = false;

    private String comparisonType = null;

    private String destinationBindingType = DEFAULT_DESTINATION_BINDING_TYPE;

    private String authnContextClassRef = null;

    private String nameIdPolicyFormat = null;

    private boolean forceServiceProviderMetadataGeneration;

    private SAMLMessageStorageFactory samlMessageStorageFactory = new EmptyStorageFactory();

    /** SAML Client name. Should be always set to the client's name because the client will read its name from this field. */
    private String clientName;

    
    // ------------------------------------------------------------------------------------------------------------------------------------
    
    
    /**
     * The constructor. Use setters to initialize properties.
     */
    public DbLoadedSamlClientConfiguration() {
    	super();
    }
    

	public byte[] getKeystoreBinaryData() {
		return keystoreBinaryData;
	}

	public void setKeystoreBinaryData(byte[] keystoreBinaryData) {
		this.keystoreBinaryData = keystoreBinaryData;
	}

	public String getKeystorePassword() {
		return keystorePassword;
	}

	public void setKeystorePassword(String keystorePassword) {
		this.keystorePassword = keystorePassword;
	}

	public String getPrivateKeyPassword() {
		return privateKeyPassword;
	}

	public void setPrivateKeyPassword(String privateKeyPassword) {
		this.privateKeyPassword = privateKeyPassword;
	}

	public String getIdentityProviderMetadata() {
		return identityProviderMetadata;
	}

	public void setIdentityProviderMetadata(String identityProviderMetadata) {
		this.identityProviderMetadata = identityProviderMetadata;
	}

	public String getIdentityProviderEntityId() {
		return identityProviderEntityId;
	}

	public void setIdentityProviderEntityId(String identityProviderEntityId) {
		this.identityProviderEntityId = identityProviderEntityId;
	}

	public String getServiceProviderEntityId() {
		return serviceProviderEntityId;
	}

	public void setServiceProviderEntityId(String serviceProviderEntityId) {
		this.serviceProviderEntityId = serviceProviderEntityId;
	}

	public int getMaximumAuthenticationLifetime() {
		return maximumAuthenticationLifetime;
	}

	public void setMaximumAuthenticationLifetime(int maximumAuthenticationLifetime) {
		this.maximumAuthenticationLifetime = (maximumAuthenticationLifetime >= MINIMUM_MAX_AUTHENTICATION_LIFETIME ? maximumAuthenticationLifetime : DEFAULT_MAX_AUTHENTICATION_LIFETIME);
	}

	public boolean isForceAuth() {
		return forceAuth;
	}

	public void setForceAuth(boolean forceAuth) {
		this.forceAuth = forceAuth;
	}

	public String getComparisonType() {
		return comparisonType;
	}

	public void setComparisonType(String comparisonType) {
		this.comparisonType = comparisonType;
	}

	public String getDestinationBindingType() {
		return destinationBindingType;
	}

	public void setDestinationBindingType(String destinationBindingType) {
		this.destinationBindingType = (destinationBindingType != null ? destinationBindingType : DEFAULT_DESTINATION_BINDING_TYPE);
	}

	public String getAuthnContextClassRef() {
		return authnContextClassRef;
	}

	public void setAuthnContextClassRef(String authnContextClassRef) {
		this.authnContextClassRef = authnContextClassRef;
	}

	public String getNameIdPolicyFormat() {
		return nameIdPolicyFormat;
	}

	public void setNameIdPolicyFormat(String nameIdPolicyFormat) {
		this.nameIdPolicyFormat = nameIdPolicyFormat;
	}

	public boolean isForceServiceProviderMetadataGeneration() {
		return forceServiceProviderMetadataGeneration;
	}

	public void setForceServiceProviderMetadataGeneration(boolean forceServiceProviderMetadataGeneration) {
		this.forceServiceProviderMetadataGeneration = forceServiceProviderMetadataGeneration;
	}

	public SAMLMessageStorageFactory getSamlMessageStorageFactory() {
		return samlMessageStorageFactory;
	}

	public void setSamlMessageStorageFactory(SAMLMessageStorageFactory samlMessageStorageFactory) {
		this.samlMessageStorageFactory = samlMessageStorageFactory;
	}

    public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
    public DbLoadedSamlClientConfiguration clone() throws CloneNotSupportedException {
		return (DbLoadedSamlClientConfiguration) super.clone();
    }
	
}
