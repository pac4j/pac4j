package org.pac4j.http.authorization.authorizer;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;
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
public class IpRegexpAuthorizer implements Authorizer<CommonProfile> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private String regexPattern;

    private Pattern pattern;

    public IpRegexpAuthorizer() { }

    public IpRegexpAuthorizer(final String regexpPattern) {
        setRegexpPattern(regexpPattern);
    }

    @Override
    public boolean isAuthorized(final WebContext context, final List<CommonProfile> profile) {
        CommonHelper.assertNotNull("pattern", pattern);

        final String ip = context.getRemoteAddr();
        return this.pattern.matcher(ip).matches();
    }

    public void setRegexpPattern(final String regexpPattern) {
        this.regexPattern = regexpPattern;
        this.pattern = Pattern.compile(regexpPattern);
    }

    @Override
    public String toString() {
        return "IpRegexpAuthorizer[" + this.regexPattern + "]";
    }
}
