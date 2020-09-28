package org.pac4j.oidc.profile;

import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.profile.factory.ProfileFactory;
import org.pac4j.core.profile.jwt.JwtClaims;
import org.pac4j.oidc.profile.converter.OidcLongTimeConverter;

import java.util.Arrays;

/**
 * This class defines the attributes of the OpenID Connect profile: http://openid.net/specs/openid-connect-core-1_0.html#StandardClaims
 * + http://openid.net/specs/openid-connect-core-1_0.html#IDToken
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class OidcProfileDefinition<P extends OidcProfile> extends CommonProfileDefinition {

    public static final String NAME                     = "name";
    public static final String GIVEN_NAME               = "given_name";
    public static final String MIDDLE_NAME              = "middle_name";
    public static final String NICKNAME                 = "nickname";
    public static final String PREFERRED_USERNAME       = "preferred_username";
    public static final String PROFILE                  = "profile";
    public static final String PICTURE                  = "picture";
    public static final String WEBSITE                  = "website";
    public static final String EMAIL_VERIFIED           = "email_verified";
    public static final String BIRTHDATE                = "birthdate";
    public static final String ZONEINFO                 = "zoneinfo";
    public static final String PHONE_NUMBER             = "phone_number";
    public static final String PHONE_NUMBER_VERIFIED    = "phone_number_verified";
    public static final String ADDRESS                  = "address";
    public static final String UPDATED_AT               = "updated_at";
    public static final String ACCESS_TOKEN             = "access_token";
    public static final String ID_TOKEN                 = "id_token";
    public static final String REFRESH_TOKEN            = "refresh_token";
    public static final String AUTH_TIME                = "auth_time";
    public static final String NONCE                    = "nonce";
    public static final String ACR                      = "acr";
    public static final String AMR                      = "amr";
    public static final String AZP                      = "azp";

    // Custom secondary attributes
    public static final String TOKEN_EXPIRATION_ADVANCE = "token_expiration_advance";
    public static final String EXPIRATION               = "expiration";

    public OidcProfileDefinition() {
        super(x -> (P) new OidcProfile());
        Arrays.stream(new String[] {NAME, GIVEN_NAME, MIDDLE_NAME, NICKNAME, PREFERRED_USERNAME, WEBSITE,
                PHONE_NUMBER, ZONEINFO, ID_TOKEN}).forEach(a -> primary(a, Converters.STRING));
        primary(PROFILE, Converters.URL);
        primary(PICTURE, Converters.URL);
        primary(EMAIL_VERIFIED, Converters.BOOLEAN);
        primary(PHONE_NUMBER_VERIFIED, Converters.BOOLEAN);
        primary(UPDATED_AT, new OidcLongTimeConverter());
        primary(ACCESS_TOKEN, attribute -> {
            if (attribute instanceof AccessToken) {
                return attribute;
            } else if (attribute instanceof String) {
                return new BearerAccessToken((String) attribute);
            }
            return null;
        });
        primary(REFRESH_TOKEN, attribute -> {
            if (attribute instanceof RefreshToken) {
                return attribute;
            } else if (attribute instanceof String) {
                return new RefreshToken((String) attribute);
            }
            return null;
        });
        // TODO: birthdate, address
        Arrays.stream(new String[] {JwtClaims.SUBJECT, JwtClaims.ISSUER, NONCE, ACR, AZP}).forEach(a -> primary(a, Converters.STRING));
        Arrays.stream(new String[] {JwtClaims.EXPIRATION_TIME, JwtClaims.ISSUED_AT, JwtClaims.NOT_BEFORE})
            .forEach(a -> primary(a, Converters.DATE_TZ_GENERAL));
        primary(AUTH_TIME, new OidcLongTimeConverter());

        // custom attributes
        secondary(TOKEN_EXPIRATION_ADVANCE, Converters.INTEGER);
        secondary(EXPIRATION, Converters.DATE_TZ_RFC822);
    }

    public OidcProfileDefinition(final ProfileFactory profileFactory) {
        this();
        setProfileFactory(profileFactory);
    }
}
