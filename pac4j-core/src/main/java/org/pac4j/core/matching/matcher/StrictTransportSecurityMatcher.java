package org.pac4j.core.matching.matcher;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.WebContextHelper;

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

    /**
     * <p>Constructor for StrictTransportSecurityMatcher.</p>
     */
    public StrictTransportSecurityMatcher() {}

    /**
     * <p>Constructor for StrictTransportSecurityMatcher.</p>
     *
     * @param maxAge a int
     */
    public StrictTransportSecurityMatcher(final int maxAge) {
        this.maxAge = maxAge;
    }

    /** {@inheritDoc} */
    @Override
    public boolean matches(final CallContext ctx) {
        val webContext = ctx.webContext();
        if (WebContextHelper.isHttpsOrSecure(webContext)) {
            webContext.setResponseHeader("Strict-Transport-Security", "max-age=" + maxAge + " ; includeSubDomains");
        }
        return true;
    }
}
