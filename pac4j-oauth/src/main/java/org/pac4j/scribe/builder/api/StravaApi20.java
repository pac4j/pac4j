package org.pac4j.scribe.builder.api;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.OAuthConfig;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.utils.OAuthEncoder;
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
    private static final String AUTHORIZE_URL = "https://www.strava.com/oauth/authorize?approval_prompt=%s&response_type=code"
        + "&client_id=%s&redirect_uri=%s";

    private static final String SCOPED_AUTHORIZE_URL = AUTHORIZE_URL + "&scope=%s";

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
    public String getAuthorizationUrl(final OAuthConfig config, final Map<String, String> additionalParams) {
        CommonHelper.assertNotBlank("config.getCallback()", config.getCallback(), "Must provide a valid callback url.");

        // Append scope if present
        if (config.getScope() != null) {
            return String.format(SCOPED_AUTHORIZE_URL, this.approvalPrompt, config.getApiKey(), OAuthEncoder.encode(config.getCallback()),
                    OAuthEncoder.encode(config.getScope()));
        } else {
            return String.format(AUTHORIZE_URL, this.approvalPrompt, config.getApiKey(), OAuthEncoder.encode(config.getCallback()));
        }
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return "https://www.strava.com/oauth/authorize";
    }
}
