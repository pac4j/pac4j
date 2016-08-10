package org.pac4j.oidc.profile;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oidc.profile.converter.OidcLongTimeConverter;

import java.util.Arrays;

/**
 * This class defines the attributes of the ID Token profile: http://openid.net/specs/openid-connect-core-1_0.html#IDToken
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class OidcIdTokenAttributesDefinition extends OidcAttributesDefinition {

    public static final String ISSUER          = "iss";
    public static final String SUBJECT         = "sub";
    public static final String AUDIENCE        = "aud";
    public static final String EXPIRATION_TIME = "exp";
    public static final String ISSUED_AT       = "iat";
    public static final String AUTH_TIME       = "auth_time";
    public static final String NONCE           = "nonce";
    public static final String ACR             = "acr";
    public static final String AMR             = "amr";
    public static final String AZP             = "azp";
    public static final String NBF             = "nbf";

    public OidcIdTokenAttributesDefinition() {
        super();
        Arrays.stream(new String[] {SUBJECT, ISSUER, NONCE, ACR, AZP}).forEach(a -> primary(a, Converters.STRING));
        Arrays.stream(new String[] {EXPIRATION_TIME, ISSUED_AT, NBF}).forEach(a -> primary(a, Converters.DATE_TZ_GENERAL));
        primary(AUTH_TIME, new OidcLongTimeConverter());
    }
}
