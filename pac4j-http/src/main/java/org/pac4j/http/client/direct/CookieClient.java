package org.pac4j.http.client.direct;

import lombok.Getter;
import lombok.Setter;
import org.pac4j.core.client.DirectClient;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.http.credentials.extractor.CookieExtractor;

/**
 * Allows direct authentication based on a cookie.
 *
 * @author Misagh Moayyed
 * @since 1.8.0
 */
public class CookieClient extends DirectClient {

    @Getter
    @Setter
    private String cookieName;

    /**
     * <p>Constructor for CookieClient.</p>
     */
    public CookieClient() {}

    /**
     * <p>Constructor for CookieClient.</p>
     *
     * @param cookieName a {@link java.lang.String} object
     * @param cookieAuthenticator a {@link org.pac4j.core.credentials.authenticator.Authenticator} object
     */
    public CookieClient(final String cookieName, final Authenticator cookieAuthenticator) {
        this.cookieName = cookieName;
        setAuthenticatorIfUndefined(cookieAuthenticator);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        CommonHelper.assertNotBlank("cookieName", this.cookieName);

        setCredentialsExtractorIfUndefined(new CookieExtractor(this.cookieName));
    }
}
