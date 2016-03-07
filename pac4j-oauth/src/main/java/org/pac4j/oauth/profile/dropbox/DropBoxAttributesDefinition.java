package org.pac4j.oauth.profile.dropbox;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.core.profile.converter.Converters;

/**
 * This class defines the attributes of the DropBox profile.
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class DropBoxAttributesDefinition extends AttributesDefinition {
    
    public static final String REFERRAL_LINK = "referral_link";
    public static final String DISPLAY_NAME = "display_name";
    public static final String COUNTRY = "country";
    public static final String SHARED = "shared";
    public static final String QUOTA = "quota";
    public static final String NORMAL = "normal";
    
    public DropBoxAttributesDefinition() {
        primary(REFERRAL_LINK, Converters.STRING);
        primary(DISPLAY_NAME, Converters.STRING);
        primary(COUNTRY, Converters.LOCALE);
        secondary(SHARED, Converters.LONG);
        secondary(QUOTA, Converters.LONG);
        secondary(NORMAL, Converters.LONG);
    }
}
