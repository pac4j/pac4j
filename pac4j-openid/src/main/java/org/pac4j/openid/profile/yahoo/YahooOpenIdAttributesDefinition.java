package org.pac4j.openid.profile.yahoo;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.core.profile.converter.Converters;

/**
 * This class defines the attributes of the {@link YahooOpenIdProfile}.
 * 
 * @author Patrice de Saint Steban
 * @since 1.6.0
 */
public class YahooOpenIdAttributesDefinition extends AttributesDefinition {
    
    public static final String EMAIL = "email";
    public static final String LANGUAGE = "language";
    public static final String FULLNAME = "fullname";
    public static final String PROFILEPICTURE = "picture_url";
    
    public YahooOpenIdAttributesDefinition() {
        primary(EMAIL, Converters.STRING);
        primary(LANGUAGE, Converters.LOCALE);
        primary(FULLNAME, Converters.STRING);
        primary(PROFILEPICTURE, Converters.GENDER);
    }
}
