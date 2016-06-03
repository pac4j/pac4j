package org.pac4j.oauth.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.apis.Foursquare2Api;
import com.github.scribejava.core.builder.api.BaseApi;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.foursquare.FoursquareProfile;
import org.pac4j.scribe.oauth.Foursquare20Service;

/**
 * <p>This class is the OAuth client to authenticate users in Foursquare.
 * It returns a {@link org.pac4j.oauth.profile.foursquare.FoursquareProfile}.</p>
 * <p>More information at https://developer.foursquare.com/overview/auth.html</p>
 *
 * @author Alexey Ogarkov
 * @since 1.5.0
 */
public class FoursquareClient extends BaseOAuth20Client<FoursquareProfile>{

    public FoursquareClient() {}

    public FoursquareClient(String key, String secret) {
        setKey(key);
        setSecret(secret);
    }

    @Override
    protected void internalInit(final WebContext context) {
        super.internalInit(context);
        this.service = new Foursquare20Service((DefaultApi20) getApi(), buildOAuthConfig(context));
    }

    @Override
    protected BaseApi<OAuth20Service> getApi() {
        return Foursquare2Api.instance();
    }

    @Override
    protected String getOAuthScope() {
        return "user";
    }

    @Override
    protected String getProfileUrl(final OAuth2AccessToken accessToken) {
        return "https://api.foursquare.com/v2/users/self?v=20131118";
    }

    @Override
    protected FoursquareProfile extractUserProfile(String body) throws HttpAction {
        FoursquareProfile profile = new FoursquareProfile();
        JsonNode json = JsonHelper.getFirstNode(body);
        if (json == null) {
            return profile;
        }
        JsonNode response = (JsonNode) JsonHelper.getElement(json, "response");
        if (response == null) {
            return profile;
        }
        JsonNode user = (JsonNode) JsonHelper.getElement(response, "user");
        if (user != null) {
            profile.setId(JsonHelper.getElement(user, "id"));

            for (final String attribute : profile.getAttributesDefinition().getPrimaryAttributes()) {
                profile.addAttribute(attribute, JsonHelper.getElement(user, attribute));
            }
        }
        return profile;
    }
}
