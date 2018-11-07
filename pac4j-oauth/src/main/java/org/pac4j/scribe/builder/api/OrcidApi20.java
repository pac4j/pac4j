package org.pac4j.scribe.builder.api;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.extractors.TokenExtractor;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthConfig;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.utils.OAuthEncoder;
import java.util.Map;
import org.pac4j.scribe.extractors.OrcidJsonExtractor;

/**
 * This class represents the OAuth API implementation for ORCiD using OAuth protocol version 2.
 *
 * @author Jens Tinglev
 * @since 1.6.0
 */
public class OrcidApi20 extends DefaultApi20 {

    private static final String AUTH_URL = "http://www.orcid.org/oauth/authorize";
    private static final String TOKEN_URL = "https://orcid.org/oauth/token";

    @Override
    public String getAccessTokenEndpoint() {
        return TOKEN_URL;
    }

    @Override
    public String getAuthorizationUrl(final OAuthConfig oAuthConfig, final Map<String, String> additionalParams) {
        // #show_login skips showing the registration form, which is only cluttersome
        return String.format(AUTH_URL + "?client_id=%s&scope=%s&response_type=%s&redirect_uri=%s#show_login",
            oAuthConfig.getApiKey(), (oAuthConfig.getScope()!=null)?OAuthEncoder.encode(oAuthConfig.getScope()):"",
            "code", OAuthEncoder.encode(oAuthConfig.getCallback()));
    }
    @Override
    protected String getAuthorizationBaseUrl() {
        return AUTH_URL;
    }

    @Override
    public TokenExtractor<OAuth2AccessToken> getAccessTokenExtractor() {
        return OrcidJsonExtractor.instance();
    }

    @Override
    public Verb getAccessTokenVerb() {
        return Verb.POST;
    }
}
