package org.pac4j.oidc.profile;

import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.core.profile.converter.AttributeConverter;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oidc.profile.converter.OidcLongTimeConverter;

import java.util.Arrays;

/**
 * This class defines the attributes of the OpenID Connect profile: http://openid.net/specs/openid-connect-core-1_0.html#StandardClaims
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class OidcAttributesDefinition extends AttributesDefinition {

    public static final String NAME = "name";
    public static final String GIVEN_NAME = "given_name";
    public static final String FAMILY_NAME = "family_name";
    public static final String MIDDLE_NAME = "middle_name";
    public static final String NICKNAME = "nickname";
    public static final String PREFERRED_USERNAME = "preferred_username";
    public static final String PROFILE = "profile";
    public static final String PICTURE = "picture";
    public static final String WEBSITE = "website";
    public static final String EMAIL = "email";
    public static final String EMAIL_VERIFIED = "email_verified";
    public static final String GENDER = "gender";
    public static final String BIRTHDATE = "birthdate";
    public static final String ZONEINFO = "zoneinfo";
    public static final String LOCALE = "locale";
    public static final String PHONE_NUMBER = "phone_number";
    public static final String PHONE_NUMBER_VERIFIED = "phone_number_verified";
    public static final String ADDRESS = "address";
    public static final String UPDATED_AT = "updated_at";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String ID_TOKEN = "id_token";
    public static final String REFRESH_TOKEN = "refresh_token";

    public OidcAttributesDefinition() {
        Arrays.stream(new String[] {NAME, GIVEN_NAME, FAMILY_NAME, MIDDLE_NAME, NICKNAME, PREFERRED_USERNAME, PROFILE, PICTURE, WEBSITE, EMAIL,
                PHONE_NUMBER, ZONEINFO, ID_TOKEN}).forEach(a -> primary(a, Converters.STRING));
        primary(EMAIL_VERIFIED, Converters.BOOLEAN);
        primary(PHONE_NUMBER_VERIFIED, Converters.BOOLEAN);
        primary(GENDER, Converters.GENDER);
        primary(LOCALE, Converters.LOCALE);
        primary(UPDATED_AT, new OidcLongTimeConverter());
        primary(ACCESS_TOKEN, new AttributeConverter<AccessToken>() {
            @Override
            public AccessToken convert(final Object attribute) {
                if (attribute instanceof AccessToken) {
                    return (AccessToken) attribute;
                } else if (attribute instanceof String) {
                    return new BearerAccessToken((String) attribute);
                }
                return null;
            }
        });
        primary(REFRESH_TOKEN, new AttributeConverter<RefreshToken>() {
            @Override
            public RefreshToken convert(final Object attribute) {
                if (attribute instanceof RefreshToken) {
                    return (RefreshToken) attribute;
                } else if (attribute instanceof String) {
                    return new RefreshToken((String) attribute);
                }
                return null;
            }
        });
        // TODO: birthdate, address
    }
}
