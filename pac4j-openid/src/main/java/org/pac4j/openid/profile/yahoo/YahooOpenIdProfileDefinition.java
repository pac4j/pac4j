package org.pac4j.openid.profile.yahoo;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.profile.definition.CommonProfileDefinition;

/**
 * This is the definition of the {@link YahooOpenIdProfile}.
 * 
 * @author Patrice de Saint Steban
 * @since 1.6.0
 */
public class YahooOpenIdProfileDefinition extends CommonProfileDefinition<YahooOpenIdProfile> {
    
    public static final String LANGUAGE = "language";
    public static final String FULLNAME = "fullname";
    public static final String IMAGE = "image";

    public YahooOpenIdProfileDefinition() {
        super();
        primary(LANGUAGE, Converters.LOCALE);
        primary(FULLNAME, Converters.STRING);
        primary(IMAGE, Converters.URL);
        setProfileFactory(x -> new YahooOpenIdProfile());
    }
}
