package org.pac4j.oauth.profile.linkedin2;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.model.OAuth2AccessToken;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.converter.JsonConverter;
import org.pac4j.oauth.profile.definition.OAuth20ProfileDefinition;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

import java.util.Arrays;
import java.util.List;

/**
 * This class is the LinkedIn profile definition.
 *
 * @author Jerome Leleu
 * @since 1.4.1
 */
public class LinkedIn2ProfileDefinition extends OAuth20ProfileDefinition<LinkedIn2Profile, LinkedIn2Configuration> {

    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String MAIDEN_NAME = "maidenName";
    public static final String FORMATTED_NAME = "formattedName";
    public static final String PHONETIC_FIRST_NAME = "phoneticFirstName";
    public static final String PHONETIC_LAST_NAME = "phoneticLastName";
    public static final String FORMATTED_PHONETIC_NAME = "formattedPhoneticName";
    public static final String HEADLINE = "headline";
    public static final String INDUSTRY = "industry";
    public static final String CURRENT_SHARE = "currentShare";
    public static final String NUM_CONNECTIONS = "numConnections";
    public static final String NUM_CONNECTIONS_CAPPED = "numConnectionsCapped";
    public static final String SUMMARY = "summary";
    public static final String SPECIALTIES = "specialties";
    public static final String POSITIONS = "positions";
    public static final String PICTURE_URL = "pictureUrl";
    public static final String PUBLIC_PROFILE_URL = "publicProfileUrl";
    public static final String SITE_STANDARD_PROFILE_REQUEST = "siteStandardProfileRequest";
    public static final String API_STANDARD_PROFILE_REQUEST = "apiStandardProfileRequest";
    public static final String EMAIL_ADDRESS = "emailAddress";

    public LinkedIn2ProfileDefinition() {
        super(x -> new LinkedIn2Profile());
        Arrays.stream(new String[] {FIRST_NAME, LAST_NAME, MAIDEN_NAME, FORMATTED_NAME, PHONETIC_FIRST_NAME, PHONETIC_LAST_NAME,
                FORMATTED_PHONETIC_NAME, HEADLINE, INDUSTRY, CURRENT_SHARE, SUMMARY, SPECIALTIES, EMAIL_ADDRESS})
                .forEach(a -> primary(a, Converters.STRING));
        primary(NUM_CONNECTIONS, Converters.INTEGER);
        primary(NUM_CONNECTIONS_CAPPED, Converters.BOOLEAN);
        primary(PICTURE_URL, Converters.URL);
        primary(PUBLIC_PROFILE_URL, Converters.URL);
        primary(LOCATION, new JsonConverter<>(LinkedIn2Location.class));
        secondary(POSITIONS, new JsonConverter(List.class, new TypeReference<List<LinkedIn2Position>>() {}));
    }

    @Override
    public String getProfileUrl(final OAuth2AccessToken accessToken, final LinkedIn2Configuration configuration) {
        return "https://api.linkedin.com/v1/people/~:(" + configuration.getFields() + ")?format=json";
    }

    @Override
    public LinkedIn2Profile extractUserProfile(final String body) {
        LinkedIn2Profile profile = newProfile();
        final JsonNode json = JsonHelper.getFirstNode(body);
        if (json == null) {
            raiseProfileExtractionJsonError(body);
        }
        profile.setId(ProfileHelper.sanitizeIdentifier(profile, JsonHelper.getElement(json, "id")));
        for (final String attribute : getPrimaryAttributes()) {
            convertAndAdd(profile, PROFILE_ATTRIBUTE, attribute, JsonHelper.getElement(json, attribute));
        }
        final Object positions = JsonHelper.getElement(json, LinkedIn2ProfileDefinition.POSITIONS);
        if (positions != null && positions instanceof JsonNode) {
            convertAndAdd(profile, PROFILE_ATTRIBUTE, LinkedIn2ProfileDefinition.POSITIONS, JsonHelper.getElement((JsonNode) positions,
                    "values"));
        }
        addUrl(profile, json, LinkedIn2ProfileDefinition.SITE_STANDARD_PROFILE_REQUEST);
        addUrl(profile, json, LinkedIn2ProfileDefinition.API_STANDARD_PROFILE_REQUEST);
        return profile;
    }

    private void addUrl(final LinkedIn2Profile profile, final JsonNode json, final String name) {
        final String url = (String) JsonHelper.getElement(json, name + ".url");
        convertAndAdd(profile, PROFILE_ATTRIBUTE, name, url);
    }
}
