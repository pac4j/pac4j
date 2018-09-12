package org.pac4j.scribe.builder.api;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.Verb;
import java.util.HashMap;
import org.pac4j.core.util.CommonHelper;

import java.util.Map;

/**
 * This class represents the OAuth API implementation for Strava.
 *
 * <p>More information at http://strava.github.io/api/v3/oauth/#post-token</p>
 *
 * @author Adrian Papusoi
 */
public final class StravaApi20 extends DefaultApi20 {

    /**
     * Strava authorization URL
     */
    private static final String AUTHORIZE_BASE_URL = "https://www.strava.com/oauth/authorize";

    private static final String ACCESS_TOKEN_URL = "https://www.strava.com/oauth/token";


    /**
     * possible values: auto or force. If force, the authorisation dialog is always displayed by Strava.
     */
    private String approvalPrompt;

    public StravaApi20(String approvalPrompt) {
        this.approvalPrompt = approvalPrompt;
    }

    @Override
    public String getAccessTokenEndpoint() {
        return ACCESS_TOKEN_URL;
    }

    @Override
    public Verb getAccessTokenVerb() {
        return Verb.POST;
    }

    @Override
    public String getAuthorizationUrl(String responseType, String apiKey, String callback, String scope, String state,
            Map<String, String> additionalParams) {
        CommonHelper.assertNotBlank("callback", callback, "Must provide a valid callback url.");
        
        if (additionalParams == null) {
            additionalParams = new HashMap<>();
        }
        additionalParams.put("approval_prompt=", this.approvalPrompt);
        return super.getAuthorizationUrl(responseType, apiKey, callback, scope, state, additionalParams);
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return AUTHORIZE_BASE_URL;
    }
}
