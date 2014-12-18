package org.pac4j.oauth.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.pac4j.core.context.WebContext;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.OAuthAttributesDefinitions;
import org.pac4j.oauth.profile.strava.StravaAttributesDefinition;
import org.pac4j.oauth.profile.strava.StravaProfile;
import org.scribe.builder.api.StravaApi;
import org.scribe.model.OAuthConfig;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;
import org.scribe.oauth.ProxyOAuth20ServiceImpl;

/**
 * Oauth20Client for Strava
 *
 * @author Adrian Papusoi
 */
public class StravaClient extends BaseOAuth20Client<StravaProfile> {

    /**
     * comma delimited string of ‘view_private’ and/or ‘write’, leave blank for read-only permissions.
     */
    protected String scope = null;
    /**
     * approvalPrompt by default auto.
     * If force, then the authorization dialog is always displayed by Strava.
     */
    private String approvalPrompt = "auto";

    public StravaClient() {
    }

    public StravaClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
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

    @Override
    protected StravaClient newClient() {
        StravaClient client = new StravaClient();
        client.setScope(this.scope);
        return client;
    }

    @Override
    protected void internalInit() {
        super.internalInit();
        this.service = new ProxyOAuth20ServiceImpl(new StravaApi(approvalPrompt), new OAuthConfig(this.key, this.secret,
                this.callbackUrl,
                SignatureType.Header, this.scope,
                null), this.connectTimeout,
                this.readTimeout, this.proxyHost, this.proxyPort, false, false);
    }

    @Override
    protected boolean requiresStateParameter() {
        return false;
    }

    @Override
    protected boolean hasBeenCancelled(WebContext context) {
        return false;
    }

    @Override
    protected String getProfileUrl(Token accessToken) {
        return "https://www.strava.com/api/v3/athlete";
    }

    @Override
    protected StravaProfile extractUserProfile(String body) {
        final StravaProfile profile = new StravaProfile();
        final JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profile.setId(JsonHelper.get(json, StravaAttributesDefinition.ID));
            for (final String attribute : OAuthAttributesDefinitions.stravaDefinition.getAllAttributes()) {
                profile.addAttribute(attribute, JsonHelper.get(json, attribute));
            }
        }
        return profile;
    }

    protected boolean isDirectRedirection() {
        return false;
    }
}
