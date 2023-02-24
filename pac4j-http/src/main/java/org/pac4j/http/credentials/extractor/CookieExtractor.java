package org.pac4j.http.credentials.extractor;

import lombok.ToString;
import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;

import java.util.Optional;

/**
 * Extracts a cookie value from the request context.
 *
 * @author Misagh Moayyed
 * @since 1.8.0
 */
@ToString
public class CookieExtractor implements CredentialsExtractor {

    private final String cookieName;

    /**
     * <p>Constructor for CookieExtractor.</p>
     *
     * @param cookieName a {@link java.lang.String} object
     */
    public CookieExtractor(final String cookieName) {
        this.cookieName = cookieName;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Credentials> extract(final CallContext ctx) {
        val col = ctx.webContext().getRequestCookies();
        for (val c : col) {
            if (c.getName().equals(this.cookieName)) {
                return Optional.of(new TokenCredentials(c.getValue()));
            }
        }
        return Optional.empty();
    }
}
