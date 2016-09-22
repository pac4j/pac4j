package org.pac4j.oauth.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.builder.api.BaseApi;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.dropbox.DropBoxProfile;
import org.pac4j.scribe.builder.api.DropboxApi20;

/**
 * <p>This class is the OAuth client to authenticate users in DropBox.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.dropbox.DropBoxProfile}.</p>
 * <p>More information at https://www.dropbox.com/developers/reference/api#account-info</p>
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class DropBoxClient extends BaseOAuth20Client<DropBoxProfile> {
    
    public DropBoxClient() {
    }
    
    public DropBoxClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }

    @Override
    protected BaseApi<OAuth20Service> getApi() {
        return DropboxApi20.INSTANCE;
    }

    @Override
    protected boolean hasOAuthGrantType() {
        return true;
    }

    @Override
    protected String getProfileUrl(final OAuth2AccessToken token) {
        return "https://api.dropbox.com/1/account/info";
    }

    @Override
    protected DropBoxProfile extractUserProfile(final String body) throws HttpAction {
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
