package org.pac4j.oauth.profile.paypal;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.converter.JsonConverter;

import java.util.Arrays;

/**
 * This class defines the attributes of the PayPal profile.
 * 
 * @author Jerome Leleu
 * @since 1.4.2
 */
public class PayPalAttributesDefinition extends AttributesDefinition {
    
    public static final String ADDRESS = "address";
    public static final String FAMILY_NAME = "family_name";
    public static final String LANGUAGE = "language";
    public static final String LOCALE = "locale";
    public static final String ZONEINFO = "zoneinfo";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String GIVEN_NAME = "given_name";
    
    public PayPalAttributesDefinition() {
        Arrays.stream(new String[] {FAMILY_NAME, ZONEINFO, NAME, EMAIL, GIVEN_NAME}).forEach(a -> primary(a, Converters.STRING));
        primary(ADDRESS, new JsonConverter<>(PayPalAddress.class));
        primary(LANGUAGE, Converters.LOCALE);
        primary(LOCALE, Converters.LOCALE);
    }
}
