package org.pac4j.http.credentials.authenticator;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.http.profile.IpProfile;

/**
 * Authenticates users based on their IP and a regexp pattern.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class IpRegexpAuthenticator extends AbstractRegexpAuthenticator<IpProfile> implements Authenticator {

    public IpRegexpAuthenticator() { }

    public IpRegexpAuthenticator(final String regexpPattern) {
        setRegexpPattern(regexpPattern);
    }

    @Override
    protected void internalInit() {
        CommonHelper.assertNotNull("pattern", pattern);
        defaultProfileDefinition(new CommonProfileDefinition<>(x -> new IpProfile()));
    }

    @Override
    public void validate(final Credentials credentials, final WebContext context) {
        init();

        final String ip = ((TokenCredentials) credentials).getToken();

        if (!this.pattern.matcher(ip).matches()) {
            throw new CredentialsException("Unauthorized IP address: " + ip);
        }

        final IpProfile profile = getProfileDefinition().newProfile();
        profile.setId(ip);
        logger.debug("profile: {}", profile);

        credentials.setUserProfile(profile);
    }
}
