package org.pac4j.oauth.profile.foursquare;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.model.OAuth2AccessToken;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.converter.JsonConverter;
import org.pac4j.oauth.profile.definition.OAuth20ProfileDefinition;

import java.util.Arrays;

/**
 * This class is the Foursquare profile definition.
 *
 * @author Alexey Ogarkov
 * @since 1.5.0
 */
public class FoursquareProfileDefinition extends OAuth20ProfileDefinition<FoursquareProfile> {

    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String PHOTO = "photo";
    public static final String FIRENDS = "friends";
    public static final String HOME_CITY = "homeCity";
    public static final String CONTACT = "contact";
    public static final String BIO = "bio";

    public FoursquareProfileDefinition() {
        super(x -> new FoursquareProfile());
        Arrays.stream(new String[] {
                FIRST_NAME, LAST_NAME, HOME_CITY, BIO, PHOTO
        }).forEach(a -> primary(a, Converters.STRING));
        primary(GENDER, Converters.GENDER);
        primary(FIRENDS, new JsonConverter<>(FoursquareUserFriends.class));
        primary(CONTACT, new JsonConverter<>(FoursquareUserContact.class));
        primary(PHOTO, new JsonConverter<>(FoursquareUserPhoto.class));
    }

    @Override
    public String getProfileUrl(final OAuth2AccessToken accessToken, final OAuth20Configuration configuration) {
        return "https://api.foursquare.com/v2/users/self?v=20131118";
    }

    @Override
    public FoursquareProfile extractUserProfile(String body) throws HttpAction {
        FoursquareProfile profile = newProfile();
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

            for (final String attribute : getPrimaryAttributes()) {
                convertAndAdd(profile, attribute, JsonHelper.getElement(user, attribute));
            }
        }
        return profile;
    }
}
