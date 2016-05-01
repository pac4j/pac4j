package org.pac4j.oauth.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.builder.api.BaseApi;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.strava.StravaAttributesDefinition;
import org.pac4j.oauth.profile.strava.StravaProfile;
import org.pac4j.scribe.builder.api.StravaApi20;

/**
 * <p>OAuth20Client for Strava.</p>
 * <p>Use the key as the client_id and secret as the client_secret, both provided by Strava at: <a href="https://www.strava.com/settings/api">https://www.strava.com/settings/api</a> </p>
 * <p>Set approvalPrompt to "force" if you want to force the authorization dialog to always display on Strava, otherwise let it to "auto" (default value). </p>
 * <p>More info at: <a href="http://strava.github.io/api/">http://strava.github.io/api/</a></p>
 *
 * @author Adrian Papusoi
 * @since 1.7.0
 */
public class StravaClient extends BaseOAuth20Client<StravaProfile> {

    /**
     * comma delimited string of ‘view_private’ and/or ‘write’, leave blank for read-only permissions.
     */
    protected String scope = null;
    /**
     * approvalPrompt is by default "auto".   <br>
     * If "force", then the authorization dialog is always displayed by Strava.
     */
    private String approvalPrompt = "auto";

    public StravaClient() {
    }

    public StravaClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }

    @Override
    protected BaseApi<OAuth20Service> getApi() {
        return new StravaApi20(approvalPrompt);
    }

    @Override
    protected String getOAuthScope() {
        return this.scope;
    }

    @Override
    protected String getProfileUrl(OAuth2AccessToken accessToken) {
        return "https://www.strava.com/api/v3/athlete";
    }

    @Override
    protected StravaProfile extractUserProfile(String body) throws HttpAction {
        final StravaProfile profile = new StravaProfile();
        final JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profile.setId(JsonHelper.getElement(json, StravaAttributesDefinition.ID));
            for (final String attribute : profile.getAttributesDefinition().getPrimaryAttributes()) {
                profile.addAttribute(attribute, JsonHelper.getElement(json, attribute));
            }
        }
        return profile;
    }

    public String getApprovalPrompt() {
        return approvalPrompt;
    }

    public void setApprovalPrompt(String approvalPrompt) {
        this.approvalPrompt = approvalPrompt;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
