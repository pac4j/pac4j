package org.pac4j.scribe.builder.api;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.model.OAuthConfig;
import com.github.scribejava.core.model.ParameterList;
import java.util.Map;

/**
 * OAuth API class for the GenericOAuth20Client
 *
 * @author aherrick
 * @since 1.9.2
 */
public class GenericApi20 extends DefaultApi20 {

    protected final String authUrl;
    protected final String tokenUrl;
    protected Verb accessTokenVerb = Verb.POST;

    public GenericApi20(final String authUrl, final String tokenUrl) {
        this.authUrl = authUrl;
        this.tokenUrl = tokenUrl;
    }

    @Override
    public Verb getAccessTokenVerb() {
        return accessTokenVerb;
    }

    public void setAccessTokenVerb(Verb verb) {
        accessTokenVerb = verb;
    }

    @Override
    public String getAccessTokenEndpoint() {
        return tokenUrl;
    }

    @Override
    public String getAuthorizationUrl(final OAuthConfig config, Map<String, String> additionalParams) {
        final ParameterList parameters = new ParameterList(additionalParams);
        parameters.add("response_type", "code");
        parameters.add("client_id", config.getApiKey());
        parameters.add("redirect_uri", config.getCallback());

        if (config.getScope() != null) {
            parameters.add("scope", config.getScope());
        }

        if (config.getState() != null) {
            parameters.add("state", config.getState());
        }

        return parameters.appendTo(authUrl);
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return authUrl;
    }
}
