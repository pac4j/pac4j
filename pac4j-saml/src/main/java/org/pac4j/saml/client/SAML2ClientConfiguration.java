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

import org.pac4j.core.context.WebContext;


/**
 * The {@link SAML2ClientConfiguration} is responsible for...
 * capturing client settings and passing them around.
 * 
 * This configuration type is intended to be initialized statically, e.g. in Java code or in Spring context.
 *
 * @author Misagh Moayyed
 * @since 1.7
 */
public final class SAML2ClientConfiguration extends AbstractSAML2ClientConfiguration {

    private String keystorePath;

    private String identityProviderMetadataPath;


    // ------------------------------------------------------------------------------------------------------------------------------------
    
    
    public SAML2ClientConfiguration() {
    	super();
    }

    public SAML2ClientConfiguration(final String keystorePath, final String keystorePassword,
    		final String privateKeyPassword, final String identityProviderMetadataPath) {
    	super();
    	this.keystorePath = keystorePath;
    	setKeystorePassword(keystorePassword);
    	setPrivateKeyPassword(privateKeyPassword);
    	this.identityProviderMetadataPath = identityProviderMetadataPath;
    }


    public void setIdentityProviderMetadataPath(final String identityProviderMetadataPath) {
        this.identityProviderMetadataPath = identityProviderMetadataPath;
    }

    public void setKeystorePath(final String keystorePath) {
        this.keystorePath = keystorePath;
    }

	@Override
    public String getIdentityProviderMetadataPath() {
        return identityProviderMetadataPath;
    }

	@Override
    public String getKeystorePath() {
        return keystorePath;
    }


    @Override
    public SAML2ClientConfiguration clone() throws CloneNotSupportedException {
    	return (SAML2ClientConfiguration) super.clone();
    }

    
	/**
	 * {@inheritDoc}
	 * 
	 * Does not do anything. All fields should have been properly set up using setters or constructor parameters.
	 * 
	 * @see org.pac4j.saml.client.AbstractSAML2ClientConfiguration#init(java.lang.String, org.pac4j.core.context.WebContext)
	 */
	@Override
	protected void init(String clientName, WebContext webContext) {
		// Intentionally left empty
	}


	/**
	 * {@inheritDoc}
	 * 
	 * @return Always true.
	 * 
	 * @see org.pac4j.saml.client.AbstractSAML2ClientConfiguration#keystoreDataNeedResolution()
	 */
	@Override
	public boolean keystoreDataNeedResolution() {
		return true;
	}


	/**
	 * {@inheritDoc}
	 * 
	 * @return Always true.
	 * 
	 * @see org.pac4j.saml.client.AbstractSAML2ClientConfiguration#identityProviderMetadataNeedResolution()
	 */
	@Override
	public boolean identityProviderMetadataNeedResolution() {
		return true;
	}


	/**
	 * {@inheritDoc}
	 * 
	 * Not implemented.
	 * 
	 * @see org.pac4j.saml.client.AbstractSAML2ClientConfiguration#getResolvedKeystoreData()
	 */
	@Override
	public byte[] getResolvedKeystoreData() {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " does not implement getResolvedKeystoreData()");
	}


	/**
	 * {@inheritDoc}
	 * 
	 * Not implemented.
	 * 
	 * @see org.pac4j.saml.client.AbstractSAML2ClientConfiguration#getResolvedIdentityProviderMetadata()
	 */
	@Override
	public String getResolvedIdentityProviderMetadata() {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " does not implement getResolvedIdentityProviderMetadata()");
	}


	/**
	 * {@inheritDoc}
	 * 
	 * @return Always false.
	 * 
	 * @see org.pac4j.saml.client.AbstractSAML2ClientConfiguration#providesClientName()
	 */
	@Override
	public boolean providesClientName() {
		return false;
	}


	/**
	 * {@inheritDoc}
	 * 
	 * Not implemented.
	 * 
	 * @see org.pac4j.saml.client.AbstractSAML2ClientConfiguration#getClientName()
	 */
	@Override
	public String getClientName() {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " does not implement getClientName()");
	}

}
