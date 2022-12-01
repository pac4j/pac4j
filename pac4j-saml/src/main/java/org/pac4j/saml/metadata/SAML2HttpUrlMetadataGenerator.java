package org.pac4j.saml.metadata;

import lombok.val;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
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
public class SAML2HttpUrlMetadataGenerator extends BaseSAML2MetadataGenerator {
    private final URL metadataUrl;
    private final HttpClient httpClient;

    private float refreshDelayFactor = -1;
    private Duration maxRefreshDelay;
    private Duration minRefreshDelay;

    public SAML2HttpUrlMetadataGenerator(final URL metadataUrl, final HttpClient httpClient) {
        this.metadataUrl = metadataUrl;
        this.httpClient = httpClient;
    }

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
                val code = response.getStatusLine().getStatusCode();
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

    public URL getMetadataUrl() {
        return metadataUrl;
    }

    public float getRefreshDelayFactor() {
        return refreshDelayFactor;
    }

    public void setRefreshDelayFactor(final float refreshDelayFactor) {
        this.refreshDelayFactor = refreshDelayFactor;
    }

    public Duration getMaxRefreshDelay() {
        return maxRefreshDelay;
    }

    public void setMaxRefreshDelay(final Duration maxRefreshDelay) {
        this.maxRefreshDelay = maxRefreshDelay;
    }

    public Duration getMinRefreshDelay() {
        return minRefreshDelay;
    }

    public void setMinRefreshDelay(final Duration minRefreshDelay) {
        this.minRefreshDelay = minRefreshDelay;
    }
}
