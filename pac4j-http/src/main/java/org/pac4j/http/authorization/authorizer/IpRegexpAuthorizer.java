package org.pac4j.http.authorization.authorizer;

import lombok.val;
import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Authorizes users based on their IP and a regexp pattern.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public class IpRegexpAuthorizer implements Authorizer {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private String regexPattern;

    private Pattern pattern;

    /**
     * <p>Constructor for IpRegexpAuthorizer.</p>
     */
    public IpRegexpAuthorizer() { }

    /**
     * <p>Constructor for IpRegexpAuthorizer.</p>
     *
     * @param regexpPattern a {@link java.lang.String} object
     */
    public IpRegexpAuthorizer(final String regexpPattern) {
        setRegexpPattern(regexpPattern);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAuthorized(final WebContext context, final SessionStore sessionStore, final List<UserProfile> profile) {
        CommonHelper.assertNotNull("pattern", pattern);

        val ip = context.getRemoteAddr();
        return this.pattern.matcher(ip).matches();
    }

    /**
     * <p>setRegexpPattern.</p>
     *
     * @param regexpPattern a {@link java.lang.String} object
     */
    public void setRegexpPattern(final String regexpPattern) {
        this.regexPattern = regexpPattern;
        this.pattern = Pattern.compile(regexpPattern);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "IpRegexpAuthorizer[" + this.regexPattern + "]";
    }
}
