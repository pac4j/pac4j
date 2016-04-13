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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import javax.annotation.Nullable;

import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.UrlResource;

import net.shibboleth.ext.spring.resource.ResourceHelper;
import net.shibboleth.utilities.java.support.resource.Resource;


/**
 * IdP metadata resolver based on a given path. 
 * 
 * The path can point to a local file, a file on an HTTP URL or a Spring resource.
 * 
 * @author Misagh Moayyed
 * @author jkacer
 * @since 1.9.0
 */
public class PathBasedSAML2IdentityProviderMetadataResolver extends AbstractSAML2IdentityProviderMetadataResolver {

	protected static final String HTTP_PREFIX = "http";
    protected static final String FILE_PREFIX = "file:";
    protected static final String RESOURCE_PREFIX = CommonHelper.RESOURCE_PREFIX;

	private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String idpMetadataPath;

    
    // ------------------------------------------------------------------------------------------------------------------------------------

    
	/**
	 * Creates a new IdP metadata resolver.
	 * 
	 * @param idpMetadataPath
	 *            Path to the file with IdP metadata. It can be without a prefix or have one of these prefixes:
	 *            {@value #RESOURCE_PREFIX}, {@value #HTTP_PREFIX}, {@value #FILE_PREFIX}.
	 * @param idpEntityId
	 *            Entity ID of the identity provider.
	 */
    public PathBasedSAML2IdentityProviderMetadataResolver(final String idpMetadataPath,
                                                 @Nullable final String idpEntityId) {
    	super(idpEntityId);
        this.idpMetadataPath = idpMetadataPath;
    }


	/**
	 * Provides the path to metadata.
	 * 
	 * @return The path originally got in the constructor.
	 * 
	 * @see org.pac4j.saml.metadata.SAML2MetadataResolver#getMetadataPath()
	 */
    @Override
    public String getMetadataPath() {
        return idpMetadataPath;
    }


	/* (non-Javadoc)
	 * @see org.pac4j.saml.metadata.AbstractSAML2IdentityProviderMetadataResolver#getIdpMetadataResourceReader()
	 */
	@Override
	protected Reader getIdpMetadataResourceReader() throws IOException {
      Resource resource = null;
      if (this.idpMetadataPath.startsWith(RESOURCE_PREFIX)) {
          String path = this.idpMetadataPath.substring(RESOURCE_PREFIX.length());
          if (!path.startsWith("/")) {
              path = "/" + path;
          }
          resource = ResourceHelper.of(new ClassPathResource(path));
      }  else if (this.idpMetadataPath.startsWith(HTTP_PREFIX)) {
          final UrlResource urlResource = new UrlResource(this.idpMetadataPath);
          if (urlResource.getURL().getProtocol().equalsIgnoreCase(HTTP_PREFIX)) {
              logger.warn("IdP metadata is retrieved from an insecure http endpoint [{}]",
                      urlResource.getURL());
          }
          resource = ResourceHelper.of(urlResource);
      // for backward compatibility
      } else if (this.idpMetadataPath.startsWith(FILE_PREFIX)) {
          resource = ResourceHelper.of(new FileSystemResource(this.idpMetadataPath.substring(FILE_PREFIX.length())));
      } else {
          resource = ResourceHelper.of(new FileSystemResource(this.idpMetadataPath));
      }

      if (resource == null) {
          throw new IOException("idp metadata cannot be resolved from " + this.idpMetadataPath);
      }

      final InputStream is = resource.getInputStream();
      return new InputStreamReader(is, Charset.forName("UTF-8"));
	}

}
