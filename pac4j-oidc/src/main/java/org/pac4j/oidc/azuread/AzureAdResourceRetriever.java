package org.pac4j.oidc.azuread;

import java.io.IOException;
import java.net.URL;

import com.nimbusds.oauth2.sdk.http.DefaultResourceRetriever;
import com.nimbusds.oauth2.sdk.http.Resource;
import com.nimbusds.oauth2.sdk.http.ResourceRetriever;

/**
 * Specialized ResourceRetriever which escapes a possibly invalid issuer URI.
 * 
 * @author Emond Papegaaij
 */
public class AzureAdResourceRetriever extends DefaultResourceRetriever implements ResourceRetriever {
    @Override
	public Resource retrieveResource(URL url) throws IOException {
        Resource ret = super.retrieveResource(url);
        return new Resource(ret.getContent().replace("{tenantid}", "%7Btenantid%7D"), ret.getContentType());
    }
}
