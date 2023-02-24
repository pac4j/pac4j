package org.pac4j.saml.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.val;
import net.shibboleth.shared.httpclient.HttpClientBuilder;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.classic.HttpClient;
import org.pac4j.core.exception.TechnicalException;

import java.time.Duration;

/**
 * This is {@link org.pac4j.saml.util.SAML2HttpClientBuilder}.
 *
 * @author Misagh Moayyed
 */
@Getter
@Setter
public class SAML2HttpClientBuilder {

    private Duration connectionTimeout;
    private Duration socketTimeout;
    private boolean useSystemProperties;
    private boolean followRedirects;
    private boolean closeConnectionAfterResponse = true;
    private int maxConnectionsTotal = 3;
    private CredentialsProvider credentialsProvider;

    /**
     * <p>build.</p>
     *
     * @return a {@link org.apache.hc.client5.http.classic.HttpClient} object
     */
    public HttpClient build() {
        try {
            val builder = new Pac4jHttpClientBuilder(credentialsProvider);
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

            return builder.buildClient();
        } catch (final Exception e) {
            throw new TechnicalException(e);
        }
    }

    @RequiredArgsConstructor
    private static class Pac4jHttpClientBuilder extends HttpClientBuilder {

        private final CredentialsProvider credentialsProvider;

        @Override
        protected CredentialsProvider buildDefaultCredentialsProvider() {
            if (credentialsProvider != null) {
                return credentialsProvider;
            }

            return super.buildDefaultCredentialsProvider();
        }
    }
}
