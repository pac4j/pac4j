package org.pac4j.saml.metadata;

import net.shibboleth.utilities.java.support.httpclient.HttpClientBuilder;
import org.apache.http.client.HttpClient;
import org.opensaml.saml.metadata.resolver.impl.AbstractBatchMetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.FilesystemMetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.HTTPMetadataResolver;
import org.springframework.core.io.Resource;

/**
 * Generates metadata object with standard values and overriden user defined values.
 *
 * @author Michael Remond
 * @since 1.5.0
 */
public class SAML2HttpMetadataGenerator extends BaseSAML2MetadataGenerator {

    @Override
    protected AbstractBatchMetadataResolver createMetadataResolver(final Resource metadataResource) throws Exception {
        final HttpClient httpClient = new HttpClientBuilder()
            .buildClient();

        return new HTTPMetadataResolver(httpClient, "");
    }

}
