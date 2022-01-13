package org.pac4j.scribe.builder.api;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.extractors.OAuth2AccessTokenExtractor;
import com.github.scribejava.core.extractors.OAuth2AccessTokenJsonExtractor;
import com.github.scribejava.core.extractors.TokenExtractor;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.Verb;

/**
 * This class represents the OAuth API implementation for the CAS OAuth wrapper.
 *
 * @author Jerome Leleu
 * @since 1.3.0
 */
public class CasOAuthWrapperApi20 extends DefaultApi20 {

    private final String casServerUrl;

    private final boolean isJsonTokenExtractor;

    private final Verb accessTokenVerb;

    public CasOAuthWrapperApi20(final String casServerUrl, final boolean isJsonTokenExtractor, final Verb accessTokenVerb) {
        this.casServerUrl = casServerUrl;
        this.isJsonTokenExtractor = isJsonTokenExtractor;
        this.accessTokenVerb = accessTokenVerb;
    }

    @Override
    public TokenExtractor<OAuth2AccessToken> getAccessTokenExtractor() {
        if (this.isJsonTokenExtractor) {
            return OAuth2AccessTokenJsonExtractor.instance();
        } else {
            return OAuth2AccessTokenExtractor.instance();
        }
    }

    @Override
    public String getAccessTokenEndpoint() {
        return this.casServerUrl + "/accessToken?";
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return this.casServerUrl + "/authorize";
    }

    @Override
    public Verb getAccessTokenVerb() {
        return this.accessTokenVerb;
    }

    public String getCasServerUrl() {
        return casServerUrl;
    }
}
