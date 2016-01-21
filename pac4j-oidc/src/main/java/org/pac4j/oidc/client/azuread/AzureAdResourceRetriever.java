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
package org.pac4j.oidc.client.azuread;

import java.io.IOException;
import java.net.URL;

import com.nimbusds.oauth2.sdk.http.DefaultResourceRetriever;
import com.nimbusds.oauth2.sdk.http.Resource;
import com.nimbusds.oauth2.sdk.http.ResourceRetriever;

/**
 * Specialized ResourceRetriever which escapes a possibly invalid issuer URI.
 * 
 * @author Emond Papegaaij
 * @since 1.8.3
 */
public class AzureAdResourceRetriever extends DefaultResourceRetriever implements ResourceRetriever {
    @Override
	public Resource retrieveResource(final URL url) throws IOException {
        final Resource ret = super.retrieveResource(url);
        return new Resource(ret.getContent().replace("{tenantid}", "%7Btenantid%7D"), ret.getContentType());
    }
}
