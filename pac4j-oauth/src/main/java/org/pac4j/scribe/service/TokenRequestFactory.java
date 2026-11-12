package org.pac4j.scribe.service;

import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.oauth.AccessTokenRequestParams;

/**
 * Exposes a provider service's token request construction to pac4j before HTTP configuration and execution.
 *
 * @author Jerome LELEU
 * @since 6.7.0
 */
public interface TokenRequestFactory {

    /**
     * Build the provider-specific token request.
     *
     * @param params the token request parameters
     * @return the request
     */
    OAuthRequest createAccessTokenRequest(AccessTokenRequestParams params);
}
