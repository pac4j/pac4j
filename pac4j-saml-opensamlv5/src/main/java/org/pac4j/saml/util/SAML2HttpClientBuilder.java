package org.pac4j.saml.util;

import net.shibboleth.utilities.java.support.httpclient.HttpClientBuilder;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.pac4j.core.exception.TechnicalException;

import java.time.Duration;

/**
 * This is {@link SAML2HttpClientBuilder}.
 *
 * @author Misagh Moayyed
 */
public class SAML2HttpClientBuilder {

    private Duration connectionTimeout;
    private Duration socketTimeout;
    private boolean useSystemProperties;
    private boolean followRedirects;
    private boolean closeConnectionAfterResponse = true;
    private int maxConnectionsTotal = 3;
    private CredentialsProvider credentialsProvider;

    public HttpClient build() {
        try {
            final var builder = new Pac4jHttpClientBuilder();
            builder.resetDefaults();

            if (this.connectionTimeout != null) {
                builder.setConnectionTimeout(this.connectionTimeout);
            }
            builder.setUseSystemProperties(this.useSystemProperties);
            if (this.socketTimeout != null) {
                builder.setSocketTimeout(this.socketTimeout);
            }
            builder.setHttpFollowRedirects(this.followRedirects);
            builder.setMaxConnectionsTotal(this.maxConnectionsTotal);
            builder.setConnectionCloseAfterResponse(this.closeConnectionAfterResponse);

            if (this.credentialsProvider != null) {
                builder.getApacheBuilder().setDefaultCredentialsProvider(credentialsProvider);
            }
            return builder.buildClient();
        } catch (final Exception e) {
            throw new TechnicalException(e);
        }
    }

    public Duration getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(final Duration connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Duration getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(final Duration socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public boolean isUseSystemProperties() {
        return useSystemProperties;
    }

    public void setUseSystemProperties(final boolean useSystemProperties) {
        this.useSystemProperties = useSystemProperties;
    }

    public boolean isFollowRedirects() {
        return followRedirects;
    }

    public void setFollowRedirects(final boolean followRedirects) {
        this.followRedirects = followRedirects;
    }

    public boolean isCloseConnectionAfterResponse() {
        return closeConnectionAfterResponse;
    }

    public void setCloseConnectionAfterResponse(final boolean closeConnectionAfterResponse) {
        this.closeConnectionAfterResponse = closeConnectionAfterResponse;
    }

    public int getMaxConnectionsTotal() {
        return maxConnectionsTotal;
    }

    public void setMaxConnectionsTotal(final int maxConnectionsTotal) {
        this.maxConnectionsTotal = maxConnectionsTotal;
    }

    public CredentialsProvider getCredentialsProvider() {
        return credentialsProvider;
    }

    public void setCredentialsProvider(final CredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
    }

    private static class Pac4jHttpClientBuilder extends HttpClientBuilder {
        @Override
        protected org.apache.http.impl.client.HttpClientBuilder getApacheBuilder() {
            final var builder = super.getApacheBuilder();
            return builder;
        }
    }
}
