package org.pac4j.oauth.profile.dropbox;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.profile.definition.CommonProfileDefinition;

/**
 * This class is the DropBox profile definition.
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class DropBoxProfileDefinition extends CommonProfileDefinition<DropBoxProfile> {
    
    public static final String REFERRAL_LINK = "referral_link";
    public static final String COUNTRY = "country";
    public static final String SHARED = "shared";
    public static final String QUOTA = "quota";
    public static final String NORMAL = "normal";
    public static final String EMAIL_VERIFIED = "email_verified";

    public DropBoxProfileDefinition() {
        super(x -> new DropBoxProfile());
        primary(REFERRAL_LINK, Converters.STRING);
        primary(COUNTRY, Converters.LOCALE);
        primary(REFERRAL_LINK, Converters.URL);
        primary(EMAIL_VERIFIED, Converters.BOOLEAN);
        secondary(SHARED, Converters.LONG);
        secondary(QUOTA, Converters.LONG);
        secondary(NORMAL, Converters.LONG);
    }
}
