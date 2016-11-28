package org.pac4j.oauth.profile.casoauthwrapper;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.profile.definition.CommonProfileDefinition;

/**
 * {@link CasOAuthWrapperProfile} profile definition.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class CasOAuthWrapperProfileDefinition extends CommonProfileDefinition<CasOAuthWrapperProfile> {

    public static final String IS_FROM_NEW_LOGIN = "isFromNewLogin";
    public static final String AUTHENTICATION_DATE = "authenticationDate";
    public static final String AUTHENTICATION_METHOD = "authenticationMethod";
    public static final String SUCCESSFUL_AUTHENTICATION_HANDLERS = "successfulAuthenticationHandlers";
    public static final String LONG_TERM_AUTHENTICATION_REQUEST_TOKEN_USED = "longTermAuthenticationRequestTokenUsed";

    public CasOAuthWrapperProfileDefinition() {
        super(x -> new CasOAuthWrapperProfile());
        primary(IS_FROM_NEW_LOGIN, Converters.BOOLEAN);
        primary(AUTHENTICATION_DATE, new CasAuthenticationDateFormatter());
        primary(AUTHENTICATION_METHOD, Converters.STRING);
        primary(SUCCESSFUL_AUTHENTICATION_HANDLERS, Converters.STRING);
        primary(LONG_TERM_AUTHENTICATION_REQUEST_TOKEN_USED, Converters.BOOLEAN);
    }
}
