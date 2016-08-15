package org.pac4j.scribe.builder.api;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.utils.OAuthEncoder;
import com.github.scribejava.core.model.OAuthConfig;

/**
 * OAuth API class for the GenericOAuth20Client
 *
 * @author aherrick
 * @since 1.9.2
 */
public class GenericApi20 extends DefaultApi20 {

    private final static String AUTHORIZATION_URL = "%s%s?response_type=code&client_id=%s&redirect_uri=%s&scope=%s";

    protected final String baseUrl;
    protected final String authEndpoint;
    protected final String tokenEndpoint;

    public GenericApi20(String baseUrl, String authEndpoint, String tokenEndpoint) {
        this.baseUrl = baseUrl;
        this.authEndpoint = authEndpoint;
        this.tokenEndpoint = tokenEndpoint;
    }

    @Override
    public Verb getAccessTokenVerb() {
        return Verb.POST;
    }

    public String getAccessTokenEndpoint() {
        return baseUrl + tokenEndpoint;
    }

    @Override
    public String getAuthorizationUrl(final OAuthConfig config) {
        String url = String.format(AUTHORIZATION_URL, baseUrl, authEndpoint, config.getApiKey(), OAuthEncoder.encode(config.getCallback()),
                                   OAuthEncoder.encode(config.getScope()));
        if (config.getState() != null) {
            url += "&state=" + OAuthEncoder.encode(config.getState());
        }
        return url;
    }
}