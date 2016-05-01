package org.pac4j.oauth.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;
import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.model.OAuth1Token;
import com.github.scribejava.core.oauth.OAuth10aService;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.twitter.TwitterProfile;

/**
 * <p>This class is the OAuth client to authenticate users in Twitter.</p>
 * <p>You can define if a screen should always been displayed for authorization confirmation by using the
 * {@link #setAlwaysConfirmAuthorization(boolean)} method (<code>false</code> by default).</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.twitter.TwitterProfile}.</p>
 * <p>More information at https://dev.twitter.com/docs/api/1/get/account/verify_credentials</p>
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class TwitterClient extends BaseOAuth10Client<TwitterProfile> {
    
    private boolean alwaysConfirmAuthorization = false;
    
    public TwitterClient() {
    }
    
    public TwitterClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }
    
    @Override
    protected void internalInit(final WebContext context) {
        super.internalInit(context);
        this.service = new OAuth10aService((DefaultApi10a) getApi(), buildOAuthConfig(context));
    }

    @Override
    protected BaseApi<OAuth10aService> getApi() {
        final DefaultApi10a api;
        if (this.alwaysConfirmAuthorization == false) {
            api = TwitterApi.Authenticate.instance();
        } else {
            api = TwitterApi.instance();
        }
        return api;
    }

    @Override
    protected String getProfileUrl(final OAuth1Token accessToken) {
        return "https://api.twitter.com/1.1/account/verify_credentials.json";
    }
    
    @Override
    protected boolean hasBeenCancelled(final WebContext context) {
        final String denied = context.getRequestParameter("denied");
        if (CommonHelper.isNotBlank(denied)) {
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    protected TwitterProfile extractUserProfile(final String body) throws HttpAction {
        final TwitterProfile profile = new TwitterProfile();
        final JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profile.setId(JsonHelper.getElement(json, "id"));
            for (final String attribute : profile.getAttributesDefinition().getPrimaryAttributes()) {
                profile.addAttribute(attribute, JsonHelper.getElement(json, attribute));
            }
        }
        return profile;
    }
    
    public boolean isAlwaysConfirmAuthorization() {
        return this.alwaysConfirmAuthorization;
    }
    
    public void setAlwaysConfirmAuthorization(final boolean alwaysConfirmAuthorization) {
        this.alwaysConfirmAuthorization = alwaysConfirmAuthorization;
    }
}
