package org.pac4j.oauth.profile.linkedin2;

import com.fasterxml.jackson.core.type.TypeReference;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.oauth.profile.converter.JsonConverter;

import java.util.Arrays;
import java.util.List;

/**
 * This class is the LinkedIn profile definition.
 * 
 * @author Jerome Leleu
 * @since 1.4.1
 */
public class LinkedIn2ProfileDefinition extends CommonProfileDefinition<LinkedIn2Profile> {
    
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
        Arrays.stream(new String[] { FIRST_NAME, LAST_NAME, MAIDEN_NAME, FORMATTED_NAME, PHONETIC_FIRST_NAME, PHONETIC_LAST_NAME,
                FORMATTED_PHONETIC_NAME, HEADLINE, INDUSTRY, CURRENT_SHARE, SUMMARY, SPECIALTIES, EMAIL_ADDRESS})
                .forEach(a -> primary(a, Converters.STRING));
        primary(NUM_CONNECTIONS, Converters.INTEGER);
        primary(NUM_CONNECTIONS_CAPPED, Converters.BOOLEAN);
        primary(PICTURE_URL, Converters.URL);
        primary(PUBLIC_PROFILE_URL, Converters.URL);
        primary(LOCATION, new JsonConverter<>(LinkedIn2Location.class));
        secondary(POSITIONS, new JsonConverter(List.class, new TypeReference<List<LinkedIn2Position>>() {}));
    }
}
