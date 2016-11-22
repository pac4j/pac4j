package org.pac4j.oauth.profile.wordpress;

import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.oauth.profile.converter.JsonConverter;

/**
 * This class is the WordPress profile definition.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class WordPressProfileDefinition extends CommonProfileDefinition<WordPressProfile> {
    
    public static final String PRIMARY_BLOG = "primary_blog";
    public static final String AVATAR_URL = "avatar_URL";
    public static final String PROFILE_URL = "profile_URL";
    public static final String LINKS = "links";
    
    public WordPressProfileDefinition() {
        super(x -> new WordPressProfile());
        primary(Pac4jConstants.USERNAME, Converters.STRING);
        primary(PRIMARY_BLOG, Converters.INTEGER);
        primary(AVATAR_URL, Converters.URL);
        primary(PROFILE_URL, Converters.URL);
        secondary(LINKS, new JsonConverter<>(WordPressLinks.class));
    }
}
