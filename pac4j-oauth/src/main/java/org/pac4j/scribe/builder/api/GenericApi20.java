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

    private final static String AUTHORIZATION_URL = "%s?response_type=code&client_id=%s&redirect_uri=%s&scope=%s";

    protected final String authUrl;
    protected final String tokenUrl;

    public GenericApi20(String authUrl, String tokenUrl) {
        this.authUrl = authUrl;
        this.tokenUrl = tokenUrl;
    }

    @Override
    public Verb getAccessTokenVerb() {
        return Verb.POST;
    }

    public String getAccessTokenEndpoint() {
        return tokenUrl;
    }

    @Override
    public String getAuthorizationUrl(final OAuthConfig config) {
        String url = String.format(AUTHORIZATION_URL, authUrl, config.getApiKey(), OAuthEncoder.encode(config.getCallback()),
                                   OAuthEncoder.encode(config.getScope()));
        if (config.getState() != null) {
            url += "&state=" + OAuthEncoder.encode(config.getState());
        }
        return url;
    }
}