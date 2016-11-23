package org.pac4j.oauth.profile.paypal;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.oauth.profile.converter.JsonConverter;

import java.util.Arrays;

/**
 * This class is the PayPal profile definition.
 * 
 * @author Jerome Leleu
 * @since 1.4.2
 */
public class PayPalProfileDefinition extends CommonProfileDefinition<PayPalProfile> {
    
    public static final String ADDRESS = "address";
    public static final String LANGUAGE = "language";
    public static final String ZONEINFO = "zoneinfo";
    public static final String NAME = "name";
    public static final String GIVEN_NAME = "given_name";
    
    public PayPalProfileDefinition() {
        super(x -> new PayPalProfile());
        Arrays.stream(new String[] {ZONEINFO, NAME, GIVEN_NAME}).forEach(a -> primary(a, Converters.STRING));
        primary(ADDRESS, new JsonConverter<>(PayPalAddress.class));
        primary(LANGUAGE, Converters.LOCALE);
    }
}
