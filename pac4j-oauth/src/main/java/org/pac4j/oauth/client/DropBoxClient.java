package org.pac4j.oauth.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.apis.DropBoxApi;
import com.github.scribejava.core.builder.api.BaseApi;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.OAuth1Token;
import com.github.scribejava.core.oauth.OAuth10aService;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.oauth.credentials.OAuth10Credentials;
import org.pac4j.oauth.credentials.OAuthCredentials;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.dropbox.DropBoxProfile;

/**
 * <p>This class is the OAuth client to authenticate users in DropBox.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.dropbox.DropBoxProfile}.</p>
 * <p>More information at https://www.dropbox.com/developers/reference/api#account-info</p>
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class DropBoxClient extends BaseOAuth10Client<DropBoxProfile> {
    
    public DropBoxClient() {
    }
    
    public DropBoxClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }

    @Override
    protected BaseApi<OAuth10aService> getApi() {
        return DropBoxApi.instance();
    }

    @Override
    protected String getProfileUrl(final OAuth1Token token) {
        return "https://api.dropbox.com/1/account/info";
    }
    
    @Override
    protected OAuthCredentials getOAuthCredentials(final WebContext context) throws RequiresHttpAction {
        // get tokenRequest from session
        final OAuth1RequestToken tokenRequest = (OAuth1RequestToken) context.getSessionAttribute(getRequestTokenSessionAttributeName());
        logger.debug("tokenRequest: {}", tokenRequest);
        // don't get parameters from url
        // token and verifier are equals and extracted from saved request token
        final String token = tokenRequest.getToken();
        logger.debug("token = verifier: {}", token);
        return new OAuth10Credentials(tokenRequest, token, token, getName());
    }
    
    @Override
    protected DropBoxProfile extractUserProfile(final String body) throws RequiresHttpAction {
        final DropBoxProfile profile = new DropBoxProfile();
        JsonNode json = JsonHelper.getFirstNode(body);
        final AttributesDefinition definition = profile.getAttributesDefinition();
        if (json != null) {
            profile.setId(JsonHelper.getElement(json, "uid"));
            for (final String attribute : definition.getPrimaryAttributes()) {
                profile.addAttribute(attribute, JsonHelper.getElement(json, attribute));
            }
            json = (JsonNode) JsonHelper.getElement(json, "quota_info");
            if (json != null) {
                for (final String attribute : definition.getSecondaryAttributes()) {
                    profile.addAttribute(attribute, JsonHelper.getElement(json, attribute));
                }
            }
        }
        return profile;
    }
}
