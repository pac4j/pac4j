package org.pac4j.oauth.client;

import com.github.scribejava.apis.YahooApi;
import com.github.scribejava.core.builder.api.Api;
import com.github.scribejava.core.model.Token;
import org.pac4j.core.exception.HttpCommunicationException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.yahoo.YahooProfile;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * <p>This class is the OAuth client to authenticate users in Yahoo.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.yahoo.YahooProfile}.</p>
 * <p>More information at http://developer.yahoo.com/social/rest_api_guide/extended-profile-resource.html</p>
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class YahooClient extends BaseOAuth10Client<YahooProfile> {
    
    public YahooClient() {
    }
    
    public YahooClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }

    @Override
    protected Api getApi() {
        return YahooApi.instance();
    }

    @Override
    protected String getProfileUrl(final Token accessToken) {
        return "https://social.yahooapis.com/v1/me/guid?format=xml";
    }
    
    @Override
    protected YahooProfile retrieveUserProfileFromToken(final Token accessToken) {
        // get the guid: https://developer.yahoo.com/social/rest_api_guide/introspective-guid-resource.html
        String body = sendRequestForData(accessToken, getProfileUrl(accessToken));
        final String guid = CommonHelper.substringBetween(body, "<value>", "</value>");
        logger.debug("guid : {}", guid);
        if (CommonHelper.isBlank(guid)) {
            final String message = "Cannot find guid from body : " + body;
            throw new HttpCommunicationException(message);
        }
        body = sendRequestForData(accessToken, "https://social.yahooapis.com/v1/user/" + guid + "/profile?format=json");
        final YahooProfile profile = extractUserProfile(body);
        addAccessTokenToProfile(profile, accessToken);
        return profile;
    }
    
    @Override
    protected YahooProfile extractUserProfile(final String body) {
        final YahooProfile profile = new YahooProfile();
        JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            json = json.get("profile");
            if (json != null) {
                profile.setId(JsonHelper.getElement(json, "guid"));
                for (final String attribute : profile.getAttributesDefinition().getPrimaryAttributes()) {
                    profile.addAttribute(attribute, JsonHelper.getElement(json, attribute));
                }
            }
        }
        return profile;
    }
}
