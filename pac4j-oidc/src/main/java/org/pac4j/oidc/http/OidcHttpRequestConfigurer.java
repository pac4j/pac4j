package org.pac4j.oidc.http;

import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import lombok.RequiredArgsConstructor;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.http.configurer.HttpRequestConfigurer;

/**
 * Default HTTP request configuration for Oidc.
 *
 * @author Jerome LELEU
 * @since 6.7.0
 */
@RequiredArgsConstructor
public class OidcHttpRequestConfigurer implements HttpRequestConfigurer<HTTPRequest> {

    private final OidcConfiguration configuration;

    /** {@inheritDoc} */
    @Override
    public void configure(final HTTPRequest request, final CallContext context) {
        configuration.configureHttpRequest(request);
    }
}
