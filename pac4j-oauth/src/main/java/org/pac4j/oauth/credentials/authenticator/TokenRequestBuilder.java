package org.pac4j.oauth.credentials.authenticator;

import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.oauth.OAuthService;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.credentials.Credentials;

/**
 * Builds an access token request before HTTP configuration and execution.
 *
 * @param <C> the credentials type
 * @param <S> the ScribeJava service type
 * @author Jerome LELEU
 * @since 6.7.0
 */
@FunctionalInterface
public interface TokenRequestBuilder<C extends Credentials, S extends OAuthService> {

    /**
     * Build a request. OAuth 1 requests are signed by the authenticator after HTTP configuration.
     *
     * @param ctx the call context
     * @param credentials the credentials
     * @param service the provider service
     * @return the token request
     */
    OAuthRequest build(CallContext ctx, C credentials, S service);
}
