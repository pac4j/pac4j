package org.pac4j.oidc.client.azuread;

import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.Resource;
import com.nimbusds.jose.util.ResourceRetriever;
import lombok.val;

import java.io.IOException;
import java.net.URL;

/**
 * Specialized ResourceRetriever which escapes a possibly invalid issuer URI.
 *
 * @author Emond Papegaaij
 * @since 1.8.3
 */
public class AzureAdResourceRetriever extends DefaultResourceRetriever implements ResourceRetriever {
    @Override
    public Resource retrieveResource(final URL url) throws IOException {
        val ret = super.retrieveResource(url);
        return new Resource(ret.getContent().replace("{tenantid}", "%7Btenantid%7D"), ret.getContentType());
    }
}
