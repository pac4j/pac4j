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
package org.pac4j.saml.metadata;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.annotation.Nullable;


/**
 * IdP metadata resolver based on a string that already contains all metadata content directly. 
 * 
 * @author jkacer
 * @since 1.9.0
 */
public class StringBasedSAML2IdentityProviderMetadataResolver extends AbstractSAML2IdentityProviderMetadataResolver {

	/** Content of IdP metadata. */
    private final String idpMetadata;

    
    // ------------------------------------------------------------------------------------------------------------------------------------

    
	/**
	 * Creates a new IdP metadata resolver.
	 * 
	 * @param idpMetadata
	 *            Content of the metadata.
	 * @param idpEntityId
	 *            Entity ID of the identity provider.
	 */
	public StringBasedSAML2IdentityProviderMetadataResolver(final String idpMetadata, @Nullable final String idpEntityId) {
		super(idpEntityId);
		this.idpMetadata = idpMetadata;
	}

	
	/**
	 * Provides the path to metadata.
	 * 
	 * @return Always {@code null} because the metadata is locate directly in a string.
	 * 
	 * @see org.pac4j.saml.metadata.SAML2MetadataResolver#getMetadataPath()
	 */
	@Override
	public String getMetadataPath() {
        return null; // There is no "path"
	}


	/* (non-Javadoc)
	 * @see org.pac4j.saml.metadata.AbstractSAML2IdentityProviderMetadataResolver#getIdpMetadataResourceReader()
	 */
	@Override
	protected Reader getIdpMetadataResourceReader() throws IOException {
		final Reader r = new StringReader(this.idpMetadata);
		return r;
	}

}
