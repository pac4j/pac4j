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
public class OidcProfileDefinition extends CommonProfileDefinition {

    /** Constant <code>NAME="name"</code> */
    public static final String NAME                     = "name";
    /** Constant <code>GIVEN_NAME="given_name"</code> */
    public static final String GIVEN_NAME               = "given_name";
    /** Constant <code>MIDDLE_NAME="middle_name"</code> */
    public static final String MIDDLE_NAME              = "middle_name";
    /** Constant <code>NICKNAME="nickname"</code> */
    public static final String NICKNAME                 = "nickname";
    /** Constant <code>PREFERRED_USERNAME="preferred_username"</code> */
    public static final String PREFERRED_USERNAME       = "preferred_username";
    /** Constant <code>PROFILE="profile"</code> */
    public static final String PROFILE                  = "profile";
    /** Constant <code>PICTURE="picture"</code> */
    public static final String PICTURE                  = "picture";
    /** Constant <code>WEBSITE="website"</code> */
    public static final String WEBSITE                  = "website";
    /** Constant <code>EMAIL_VERIFIED="email_verified"</code> */
    public static final String EMAIL_VERIFIED           = "email_verified";
    /** Constant <code>BIRTHDATE="birthdate"</code> */
    public static final String BIRTHDATE                = "birthdate";
    /** Constant <code>ZONEINFO="zoneinfo"</code> */
    public static final String ZONEINFO                 = "zoneinfo";
    /** Constant <code>PHONE_NUMBER="phone_number"</code> */
    public static final String PHONE_NUMBER             = "phone_number";
    /** Constant <code>PHONE_NUMBER_VERIFIED="phone_number_verified"</code> */
    public static final String PHONE_NUMBER_VERIFIED    = "phone_number_verified";
    /** Constant <code>ADDRESS="address"</code> */
    public static final String ADDRESS                  = "address";
    /** Constant <code>UPDATED_AT="updated_at"</code> */
    public static final String UPDATED_AT               = "updated_at";
    /** Constant <code>ACCESS_TOKEN="access_token"</code> */
    public static final String ACCESS_TOKEN             = "access_token";
    /** Constant <code>ID_TOKEN="id_token"</code> */
    public static final String ID_TOKEN                 = "id_token";
    /** Constant <code>REFRESH_TOKEN="refresh_token"</code> */
    public static final String REFRESH_TOKEN            = "refresh_token";
    /** Constant <code>AUTH_TIME="auth_time"</code> */
    public static final String AUTH_TIME                = "auth_time";
    /** Constant <code>NONCE="nonce"</code> */
    public static final String NONCE                    = "nonce";
    /** Constant <code>ACR="acr"</code> */
    public static final String ACR                      = "acr";
    /** Constant <code>AMR="amr"</code> */
    public static final String AMR                      = "amr";
    /** Constant <code>AZP="azp"</code> */
    public static final String AZP                      = "azp";

    // Custom secondary attributes
    /** Constant <code>TOKEN_EXPIRATION_ADVANCE="token_expiration_advance"</code> */
    public static final String TOKEN_EXPIRATION_ADVANCE = "token_expiration_advance";
    /** Constant <code>EXPIRATION="expiration"</code> */
    public static final String EXPIRATION               = "expiration";

    /**
     * <p>Constructor for OidcProfileDefinition.</p>
     */
    public OidcProfileDefinition() {
        super(x -> new OidcProfile());
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

    /**
     * <p>Constructor for OidcProfileDefinition.</p>
     *
     * @param profileFactory a {@link ProfileFactory} object
     */
    public OidcProfileDefinition(final ProfileFactory profileFactory) {
        this();
        setProfileFactory(profileFactory);
    }
}
