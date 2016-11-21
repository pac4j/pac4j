package org.pac4j.oauth.profile.casoauthwrapper;

import org.pac4j.oauth.profile.OAuth20Profile;

import java.util.Date;

/**
 * <p>This class is the user profile for sites using OAuth wrapper for CAS.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.CasOAuthWrapperClient}.</p>
 *
 * @author Jerome Leleu
 * @since 1.3.0
 */
public class CasOAuthWrapperProfile extends OAuth20Profile {
    
    private static final long serialVersionUID = 1347249873352825528L;

    public Boolean isFromNewLogin() {
        return (Boolean) getAttribute(CasOAuthWrapperProfileDefinition.IS_FROM_NEW_LOGIN);
    }

    public Date getAuthenticationDate() {
        return (Date) getAttribute(CasOAuthWrapperProfileDefinition.AUTHENTICATION_DATE);
    }

    public String getAuthenticationMethod() {
        return (String) getAttribute(CasOAuthWrapperProfileDefinition.AUTHENTICATION_METHOD);
    }

    public String getSuccessfulAuthenticationHandlers() {
        return (String) getAttribute(CasOAuthWrapperProfileDefinition.SUCCESSFUL_AUTHENTICATION_HANDLERS);
    }

    public Boolean isLongTermAuthenticationRequestTokenUsed() {
        return (Boolean) getAttribute(CasOAuthWrapperProfileDefinition.LONG_TERM_AUTHENTICATION_REQUEST_TOKEN_USED);
    }
}
