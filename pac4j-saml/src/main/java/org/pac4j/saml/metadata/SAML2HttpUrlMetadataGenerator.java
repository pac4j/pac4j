package org.pac4j.saml.metadata;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.opensaml.saml.metadata.resolver.impl.AbstractBatchMetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.HTTPMetadataResolver;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;

import java.time.Duration;

/**
 * Generates metadata object with standard values and overriden user defined values.
 *
 * @author Michael Remond
 * @since 1.5.0
 */
public class SAML2HttpUrlMetadataGenerator extends BaseSAML2MetadataGenerator {
    private final String metadataUrl;
    private final HttpClient httpClient;

    private float refreshDelayFactor = -1;
    private Duration maxRefreshDelay;
    private Duration minRefreshDelay;

    public SAML2HttpUrlMetadataGenerator(final String metadataUrl, final HttpClient httpClient) {
        this.metadataUrl = metadataUrl;
        this.httpClient = httpClient;
    }

    @Override
    protected AbstractBatchMetadataResolver createMetadataResolver(final Resource metadataResource) throws Exception {
        final HTTPMetadataResolver resolver = new HTTPMetadataResolver(httpClient, this.metadataUrl);
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
    public void storeMetadata(final String metadata, final WritableResource resource, final boolean force) throws Exception {
        final HttpPost httpPost = new HttpPost(this.metadataUrl);
        httpPost.setEntity(new StringEntity(metadata, ContentType.APPLICATION_XML));
        final HttpResponse response = httpClient.execute(httpPost);
        if (response != null) {
            final int code = response.getStatusLine().getStatusCode();
            if (code == HttpStatus.SC_NOT_IMPLEMENTED) {
                logger.info("Storing metadata is not supported/implemented by {}", metadataUrl);
            } else if (code == HttpStatus.SC_OK || code == HttpStatus.SC_CREATED) {
                logger.info("Successfully submitted metadata to {}", metadataUrl);
            }
        }
    }

    public String getMetadataUrl() {
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
