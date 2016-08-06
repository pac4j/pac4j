package org.pac4j.http.credentials.authenticator;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.http.profile.IpProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * Authenticates users based on their IP and a regexp pattern.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class IpRegexpAuthenticator implements Authenticator<TokenCredentials> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private String regexPattern;

    private Pattern pattern;

    public IpRegexpAuthenticator() { }

    public IpRegexpAuthenticator(final String regexpPattern) {
        setRegexpPattern(regexpPattern);
    }

    @Override
    public void validate(final TokenCredentials credentials, final WebContext context) throws HttpAction {
        CommonHelper.assertNotNull("pattern", pattern);

        final String ip = credentials.getToken();

        if (!this.pattern.matcher(ip).matches()) {
            throw new CredentialsException("Unauthorized IP address: " + ip);
        }

        final IpProfile profile = new IpProfile(ip);
        logger.debug("profile: {}", profile);
        credentials.setUserProfile(profile);
    }

    public void setRegexpPattern(final String regexpPattern) {
        this.regexPattern = regexpPattern;
        this.pattern = Pattern.compile(regexpPattern);
    }

    @Override
    public String toString() {
        return "IpRegexpAuthenticator[" + this.regexPattern + "]";
    }
}
