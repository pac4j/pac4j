package org.pac4j.oidc.profile.google;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.core.profile.converter.Converters;

import java.util.Arrays;

/**
 * This class defines the attributes of the Google OpenID Connect profile.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class GoogleOidcAttributesDefinition extends AttributesDefinition {

    public static final String NAME = "name";
    public static final String GIVEN_NAME = "given_name";
    public static final String FAMILY_NAME = "family_name";
    public static final String PROFILE = "profile";
    public static final String PICTURE = "picture";
    public static final String EMAIL = "email";
    public static final String EMAIL_VERIFIED = "email_verified";
    public static final String GENDER = "gender";
    public static final String LOCALE = "locale";

    public GoogleOidcAttributesDefinition() {
        Arrays.stream(new String[] {NAME, GIVEN_NAME, FAMILY_NAME, PROFILE, PICTURE, EMAIL}).forEach(a -> primary(a, Converters.STRING));
        primary(EMAIL_VERIFIED, Converters.BOOLEAN);
        primary(GENDER, Converters.GENDER);
        primary(LOCALE, Converters.LOCALE);
    }
}
