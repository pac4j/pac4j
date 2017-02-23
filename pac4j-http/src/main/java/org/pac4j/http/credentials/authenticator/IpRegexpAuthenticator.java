package org.pac4j.http.credentials.authenticator;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.profile.definition.ProfileDefinitionAware;
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
public class IpRegexpAuthenticator extends ProfileDefinitionAware<IpProfile> implements Authenticator<TokenCredentials> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private String regexPattern;

    private Pattern pattern;

    public IpRegexpAuthenticator() { }

    public IpRegexpAuthenticator(final String regexpPattern) {
        setRegexpPattern(regexpPattern);
    }

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotNull("pattern", pattern);
        defaultProfileDefinition(new CommonProfileDefinition<>(x -> new IpProfile()));
    }

    @Override
    public void validate(final TokenCredentials credentials, final WebContext context) throws HttpAction, CredentialsException {
        init(context);

        final String ip = credentials.getToken();

        if (!this.pattern.matcher(ip).matches()) {
            throw new CredentialsException("Unauthorized IP address: " + ip);
        }

        final IpProfile profile = getProfileDefinition().newProfile();
        profile.setId(ip);
        logger.debug("profile: {}", profile);

        credentials.setUserProfile(profile);
    }

    public void setRegexpPattern(final String regexpPattern) {
        CommonHelper.assertNotNull("regexpPattern", regexpPattern);
        this.regexPattern = regexpPattern;
        this.pattern = Pattern.compile(regexpPattern);
    }

    @Override
    public String toString() {
        return "IpRegexpAuthenticator[" + this.regexPattern + "]";
    }
}
