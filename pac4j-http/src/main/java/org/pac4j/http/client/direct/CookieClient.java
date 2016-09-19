package org.pac4j.http.client.direct;

import org.pac4j.core.client.DirectClientV2;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.http.credentials.extractor.CookieExtractor;

/**
 * Allows direct authentication based on a cookie.
 *
 * @author Misagh Moayyed
 * @since 1.8.0
 */
public class CookieClient extends DirectClientV2<TokenCredentials, CommonProfile> {

    private String cookieName;

    public CookieClient() {}

    @Deprecated
    public CookieClient(final Authenticator cookieAuthenticator) {
        setAuthenticator(cookieAuthenticator);
    }

    public CookieClient(final String cookieName, final Authenticator cookieAuthenticator) {
        this.cookieName = cookieName;
        setAuthenticator(cookieAuthenticator);
    }

    @Deprecated
    public CookieClient(final Authenticator cookieAuthenticator,
                        final ProfileCreator profileCreator) {
        setAuthenticator(cookieAuthenticator);
        setProfileCreator(profileCreator);
    }

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotBlank("cookieName", this.cookieName);

        setCredentialsExtractor(new CookieExtractor(this.cookieName, getName()));
    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }
}
