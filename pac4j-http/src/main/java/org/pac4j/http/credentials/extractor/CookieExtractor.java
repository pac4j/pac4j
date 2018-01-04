package org.pac4j.http.credentials.extractor;

import org.pac4j.core.context.Cookie;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.util.CommonHelper;

import java.util.Collection;
import java.util.Optional;

/**
 * Extracts a cookie value from the request context.
 *
 * @author Misagh Moayyed
 * @since 1.8.0
 */
public class CookieExtractor implements CredentialsExtractor<TokenCredentials> {

    private final String cookieName;

    public CookieExtractor(final String cookieName) {
        this.cookieName = cookieName;
    }

    @Override
    public Optional<TokenCredentials> extract(final WebContext context) {
        final Collection<Cookie> col = context.getRequestCookies();
        for (final Cookie c : col) {
            if (c.getName().equals(this.cookieName)) {
                return Optional.of(new TokenCredentials(c.getValue()));
            }
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "cookieName", this.cookieName);
    }
}
