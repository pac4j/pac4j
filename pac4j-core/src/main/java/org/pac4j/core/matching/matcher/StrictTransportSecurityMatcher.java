package org.pac4j.core.matching.matcher;

import org.pac4j.core.context.ContextHelper;
import org.pac4j.core.context.WebContext;

/**
 * Strict transport security header matcher.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
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
    public boolean matches(final WebContext context) {
        if (ContextHelper.isHttpsOrSecure(context)) {
            context.setResponseHeader("Strict-Transport-Security", "max-age=" + maxAge + " ; includeSubDomains");
        }
        return true;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(final int maxAge) {
        this.maxAge = maxAge;
    }
}
