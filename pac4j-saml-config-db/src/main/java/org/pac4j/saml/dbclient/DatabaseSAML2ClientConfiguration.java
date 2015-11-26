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

import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.context.WebContext;
import org.pac4j.saml.client.AbstractSAML2ClientConfiguration;
import org.pac4j.saml.client.SAML2ClientConfiguration;
import org.pac4j.saml.dbclient.dao.api.DbLoadedSamlClientConfigurationDto;
import org.pac4j.saml.dbclient.dao.api.SamlClientDao;


/**
 * SAML2 Client configuration intended to be loaded from a database.
 * 
 * An alternative to {@link SAML2ClientConfiguration}. Unlike {@link SAML2ClientConfiguration}, this class does not use paths to resources.
 * Instead, it contains the data directly.
 *
 * @author jkacer
 * @since 1.9.0
 */
public class DatabaseSAML2ClientConfiguration extends AbstractSAML2ClientConfiguration {

	/** DAO to read SAML client configurations. Should be shared by all configuration instances. */
	private final SamlClientDao dao;
	
	/** Binary data of a JKS keystore. */
	private byte[] keystoreBinaryData;

    /** IdP metadata. */
    private String identityProviderMetadata;

	/** SAML Client name. */
	private String clientName;
    
    // ------------------------------------------------------------------------------------------------------------------------------------
    
    
    /**
	 * The constructor. Use setters to initialize properties.
	 * 
	 * @param dao
	 *            DAO loading client configuration from DB.
	 */
    public DatabaseSAML2ClientConfiguration(final SamlClientDao dao) {
    	super();
    	if (dao == null) {
    		throw new IllegalArgumentException("DAO must not be null.");
    	}
    	this.dao = dao;
    }
    

	public byte[] getKeystoreBinaryData() {
		return keystoreBinaryData;
	}

	public void setKeystoreBinaryData(byte[] keystoreBinaryData) {
		this.keystoreBinaryData = keystoreBinaryData;
	}

	public String getIdentityProviderMetadata() {
		return identityProviderMetadata;
	}

	public void setIdentityProviderMetadata(String identityProviderMetadata) {
		this.identityProviderMetadata = identityProviderMetadata;
	}

	
	/* (non-Javadoc)
	 * @see org.pac4j.saml.client.AbstractSAML2ClientConfiguration#getClientName()
	 */
	@Override
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
    public DatabaseSAML2ClientConfiguration clone() throws CloneNotSupportedException {
		return (DatabaseSAML2ClientConfiguration) super.clone();
    }

	
	/**
	 * {@inheritDoc}
	 * 
	 * Initializes the configuration by loading all its propertied from a database using the DAO provided at creation time.
	 * 
	 * It is assumed the client name will come in the web context, under key "current_client_name".
	 * 
	 * @see org.pac4j.core.util.InitializableWebObject#internalInit(org.pac4j.core.context.WebContext)
	 * 
	 * @throws IllegalStateException
	 *             If the context does not contain the client name or if no configuration for the given name exists.
	 */
	@Override
	protected void init(final String clientName, final WebContext context) {
		if (StringUtils.isBlank(clientName)) {
			throw new IllegalStateException("The client name must not be null or empty.");
		}

		// Subsequently, the configuration for the name must be loaded using a DAO.
		DbLoadedSamlClientConfigurationDto loaded = dao.loadClient(clientName);
		if ((loaded == null) || (!clientName.equals(loaded.getClientName()))) {
			throw new IllegalStateException("SAML Client Configuration for name '" + clientName + "' could not be loaded.");
		}
		
		// If everything is OK, we will set the loaded values to the configuration object (itself).
		setClientName(clientName);
		setDestinationBindingType(loaded.getDestinationBindingType());
		setIdentityProviderEntityId(loaded.getIdentityProviderEntityId());
		setIdentityProviderMetadata(loaded.getIdentityProviderMetadata());
		setKeystoreBinaryData(loaded.getKeystoreBinaryData());
		setKeystorePassword(loaded.getKeystorePassword());
		setPrivateKeyPassword(loaded.getPrivateKeyPassword());
		setMaximumAuthenticationLifetime(loaded.getMaximumAuthenticationLifetime());
		setServiceProviderEntityId(loaded.getServiceProviderEntityId());
	}


	/**
	 * {@inheritDoc}
	 * 
	 * @return Always false.
	 * 
	 * @see org.pac4j.saml.client.AbstractSAML2ClientConfiguration#keystoreDataNeedResolution()
	 */
	@Override
	public boolean keystoreDataNeedResolution() {
		return false;
	}


	/**
	 * {@inheritDoc}
	 * 
	 * @return Always false.
	 * 
	 * @see org.pac4j.saml.client.AbstractSAML2ClientConfiguration#identityProviderMetadataNeedResolution()
	 */
	@Override
	public boolean identityProviderMetadataNeedResolution() {
		return false;
	}


	/**
	 * {@inheritDoc}
	 * 
	 * Not implemented.
	 * 
	 * @see org.pac4j.saml.client.AbstractSAML2ClientConfiguration#getKeystorePath()
	 */
	@Override
	public String getKeystorePath() {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " does not implement getKeystorePath()");
	}


	/* (non-Javadoc)
	 * @see org.pac4j.saml.client.AbstractSAML2ClientConfiguration#getResolvedKeystoreData()
	 */
	@Override
	public byte[] getResolvedKeystoreData() {
		return keystoreBinaryData;
	}


	/**
	 * {@inheritDoc}
	 * 
	 * Not implemented.
	 * 
	 * @see org.pac4j.saml.client.AbstractSAML2ClientConfiguration#getIdentityProviderMetadataPath()
	 */
	@Override
	public String getIdentityProviderMetadataPath() {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " does not implement getIdentityProviderMetadataPath()");
	}


	/* (non-Javadoc)
	 * @see org.pac4j.saml.client.AbstractSAML2ClientConfiguration#getResolvedIdentityProviderMetadata()
	 */
	@Override
	public String getResolvedIdentityProviderMetadata() {
		return identityProviderMetadata;
	}


	/**
	 * {@inheritDoc}
	 * 
	 * @return Always true.
	 * 
	 * @see org.pac4j.saml.client.AbstractSAML2ClientConfiguration#providesClientName()
	 */
	@Override
	public boolean providesClientName() {
		return true;
	}
	
}
