package org.pac4j.oauth.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.Gender;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.OAuthAttributesDefinitions;
import org.pac4j.oauth.profile.foursquare.FoursquareProfile;
import org.scribe.builder.api.Foursquare2Api;
import org.scribe.builder.api.FoursquareApi;
import org.scribe.model.OAuthConfig;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;
import org.scribe.oauth.FoursquareOAuth20ServiceImpl;

public class FoursquareClient extends BaseOAuth20Client<FoursquareProfile>{
    public FoursquareClient() {}

    public FoursquareClient(String key, String secret) {
        setKey(key);
        setSecret(secret);
    }

    protected FoursquareClient newClient() {
        return new FoursquareClient();
    }

    protected void internalInit() {
        super.internalInit();
        this.service = new FoursquareOAuth20ServiceImpl(new Foursquare2Api(),
                new OAuthConfig(this.key, this.secret, this.callbackUrl, SignatureType.Header, "user", null),
                this.connectTimeout,
                this.readTimeout,
                this.proxyHost,
                this.proxyPort);
    }

    protected FoursquareProfile extractUserProfile(String body) {
        FoursquareProfile profile = new FoursquareProfile();
        JsonNode json = JsonHelper.getFirstNode(body);
        if (json == null) {
        }
        JsonNode response = (JsonNode) JsonHelper.get(json, "response");
        if (response == null) {
        }
        JsonNode user = (JsonNode) JsonHelper.get(response, "user");
        if (user != null) {
            profile.setId(JsonHelper.get(user, "id"));
            profile.addAttribute("first_name",JsonHelper.get(user,"firstName"));
            profile.addAttribute("family_name",JsonHelper.get(user,"lastName"));
            profile.addAttribute("location", JsonHelper.get(user,"homeCity"));

            JsonNode contact = (JsonNode) JsonHelper.get(user, "contact");
            profile.addAttribute("email", JsonHelper.get(contact, "email"));

            JsonNode picture = (JsonNode) JsonHelper.get(user, "photo");

            String pictureUrl = picture.get("prefix").asText()+"original"+picture.get("suffix").asText();
            profile.addAttribute("picture_url", pictureUrl);

            for (final String attribute : OAuthAttributesDefinitions.foursquareDefenition.getAllAttributes()) {
                profile.addAttribute(attribute, JsonHelper.get(user, attribute));
            }
        }
        return profile;
    }

    protected boolean requiresStateParameter() {
        return false;
    }

    protected boolean hasBeenCancelled(WebContext context) {
        return false;
    }

    @Override
    protected String getProfileUrl(Token accessToken) {
        return "https://api.foursquare.com/v2/users/self?v=20131118";
    }
}
