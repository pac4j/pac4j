package org.pac4j.core.matching.matcher;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.WebContextHelper;
import org.pac4j.core.context.session.SessionStore;

/**
 * Strict transport security header matcher.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
@Getter
@Setter
@ToString
public class StrictTransportSecurityMatcher implements Matcher {

    /**
     * 6 months in seconds.
     */
    private final static int DEFAULT_MAX_AGE = 15768000;

    private int maxAge = DEFAULT_MAX_AGE;

    public StrictTransportSecurityMatcher() {}

    public StrictTransportSecurityMatcher(final int maxAge) {
        this.maxAge = maxAge;
    }

    @Override
    public boolean matches(final WebContext context, final SessionStore sessionStore) {
        if (WebContextHelper.isHttpsOrSecure(context)) {
            context.setResponseHeader("Strict-Transport-Security", "max-age=" + maxAge + " ; includeSubDomains");
        }
        return true;
    }
}
