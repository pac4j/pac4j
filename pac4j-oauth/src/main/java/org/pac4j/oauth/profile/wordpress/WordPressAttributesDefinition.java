package org.pac4j.oauth.profile.wordpress;

import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.converter.JsonConverter;

/**
 * This class defines the attributes of the WordPress profile.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class WordPressAttributesDefinition extends AttributesDefinition {
    
    public static final String DISPLAY_NAME = "display_name";
    public static final String EMAIL = "email";
    public static final String PRIMARY_BLOG = "primary_blog";
    public static final String AVATAR_URL = "avatar_URL";
    public static final String PROFILE_URL = "profile_URL";
    public static final String LINKS = "links";
    
    public WordPressAttributesDefinition() {
        primary(DISPLAY_NAME, Converters.STRING);
        primary(Pac4jConstants.USERNAME, Converters.STRING);
        primary(EMAIL, Converters.STRING);
        primary(PRIMARY_BLOG, Converters.INTEGER);
        primary(AVATAR_URL, Converters.URL);
        primary(PROFILE_URL, Converters.URL);
        secondary(LINKS, new JsonConverter<>(WordPressLinks.class));
    }
}
