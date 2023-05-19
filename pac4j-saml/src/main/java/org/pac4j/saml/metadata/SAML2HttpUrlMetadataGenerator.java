package org.pac4j.saml.metadata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.val;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.opensaml.saml.metadata.resolver.impl.AbstractMetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.HTTPMetadataResolver;

import java.net.URL;
import java.time.Duration;

/**
 * Generates metadata object with standard values and overridden user defined values.
 *
 * @author Misagh Moayyed
 * @since 4.0.1
 */
@RequiredArgsConstructor
public class SAML2HttpUrlMetadataGenerator extends BaseSAML2MetadataGenerator {
    @Getter
    private final URL metadataUrl;
    private final HttpClient httpClient;

    @Getter
    private float refreshDelayFactor = -1;
    @Getter
    @Setter
    private Duration maxRefreshDelay;
    @Getter
    @Setter
    private Duration minRefreshDelay;


    /** {@inheritDoc} */
    @Override
    protected AbstractMetadataResolver createMetadataResolver() throws Exception {
        val resolver = new HTTPMetadataResolver(httpClient, this.metadataUrl.toExternalForm());
        if (minRefreshDelay != null) {
            resolver.setMinRefreshDelay(minRefreshDelay);
        }
        if (maxRefreshDelay != null) {
            resolver.setMaxRefreshDelay(maxRefreshDelay);
        }
        if (refreshDelayFactor >= 0) {
            resolver.setRefreshDelayFactor(refreshDelayFactor);
        }
        return resolver;
    }

    /** {@inheritDoc} */
    @Override
    public boolean storeMetadata(final String metadata, final boolean force) throws Exception {
        HttpResponse response = null;

        try {
            logger.debug("Posting metadata to {}", this.metadataUrl.toURI());

            val httpPost = new HttpPost(this.metadataUrl.toURI());
            httpPost.addHeader("Accept", ContentType.APPLICATION_XML.getMimeType());
            httpPost.addHeader("Content-Type", ContentType.APPLICATION_XML.getMimeType());
            httpPost.setEntity(new StringEntity(metadata, ContentType.APPLICATION_XML));

            response = httpClient.execute(httpPost);
            if (response != null) {
                val code = response.getCode();
                if (code == HttpStatus.SC_NOT_IMPLEMENTED) {
                    logger.info("Storing metadata is not supported/implemented by {}", metadataUrl);
                    return false;
                }
                if (code == HttpStatus.SC_OK || code == HttpStatus.SC_CREATED) {
                    logger.info("Successfully submitted metadata to {}", metadataUrl);
                    return true;
                }
            }
            logger.error("Unable to store metadata successfully via {}", metadataUrl.toExternalForm());
            return false;
        } finally {
            if (response != null && response instanceof CloseableHttpResponse) {
                ((CloseableHttpResponse) response).close();
            }
        }
    }
}
