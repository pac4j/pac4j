package org.pac4j.scribe.builder.api;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.extractors.AccessTokenExtractor;
import com.github.scribejava.core.extractors.JsonTokenExtractor;
import com.github.scribejava.core.extractors.TokenExtractor20Impl;
import com.github.scribejava.core.model.OAuthConfig;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.utils.OAuthEncoder;

/**
 * This class represents the OAuth API implementation for the CAS OAuth wrapper.
 * 
 * @author Jerome Leleu
 * @since 1.3.0
 */
public class CasOAuthWrapperApi20 extends DefaultApi20 {
    
    private final String casServerUrl;
    
    private final boolean springSecurityCompliant;
    
    public CasOAuthWrapperApi20(final String casServerUrl, final boolean springSecurityCompliant) {
        this.casServerUrl = casServerUrl;
        this.springSecurityCompliant = springSecurityCompliant;
    }
    
    @Override
    public AccessTokenExtractor getAccessTokenExtractor() {
        if (this.springSecurityCompliant) {
            return new JsonTokenExtractor();
        } else {
            return new TokenExtractor20Impl();
        }
    }
    
    @Override
    public String getAccessTokenEndpoint() {
        return this.casServerUrl + "/accessToken?";
    }
    
    @Override
    public String getAuthorizationUrl(final OAuthConfig config) {
        return String.format(this.casServerUrl + "/authorize?" + "response_type=code&client_id=%s&redirect_uri=%s",
                             config.getApiKey(), OAuthEncoder.encode(config.getCallback()));
    }
    
    @Override
    public Verb getAccessTokenVerb() {
        if (this.springSecurityCompliant) {
            return Verb.PUT;
        } else {
            return Verb.POST;
        }
    }
}
