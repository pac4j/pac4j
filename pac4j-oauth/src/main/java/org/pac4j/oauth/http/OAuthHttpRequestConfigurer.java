package org.pac4j.oauth.http;

import com.github.scribejava.core.model.OAuthRequest;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.http.configurer.HttpRequestConfigurer;

/**
 * Default HTTP request configuration for OAuth.
 *
 * @author Jerome LELEU
 * @since 6.7.0
 */
public class OAuthHttpRequestConfigurer implements HttpRequestConfigurer<OAuthRequest> {

    /** {@inheritDoc} */
    @Override
    public void configure(final OAuthRequest request, final CallContext context) {
        // ScribeJava already configures the request.
    }
}
