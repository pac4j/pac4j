package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.ContextHelper;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;

import java.util.List;

/**
 * Strict transport security header.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public class StrictTransportSecurityHeader implements Authorizer<UserProfile> {

    /**
     * 6 months in seconds.
     */
    private final static int DEFAULT_MAX_AGE = 15768000;

    private int maxAge = DEFAULT_MAX_AGE;

    public StrictTransportSecurityHeader() {}

    public StrictTransportSecurityHeader(final int maxAge) {
        this.maxAge = maxAge;
    }

    @Override
    public boolean isAuthorized(final WebContext context, final List<UserProfile> profiles) {
        if (ContextHelper.isHttpsOrSecure(context)) {
            context.setResponseHeader("Strict-Transport-Security", "max-age=" + maxAge + " ; includeSubDomains");
        }
        return true;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }
}
