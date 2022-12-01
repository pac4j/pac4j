package org.pac4j.http.credentials.authenticator;

import lombok.val;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.http.profile.IpProfile;

import java.util.Optional;

/**
 * Authenticates users based on their IP and a regexp pattern.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class IpRegexpAuthenticator extends AbstractRegexpAuthenticator implements Authenticator {

    public IpRegexpAuthenticator() { }

    public IpRegexpAuthenticator(final String regexpPattern) {
        setRegexpPattern(regexpPattern);
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        CommonHelper.assertNotNull("pattern", pattern);
        defaultProfileDefinition(new CommonProfileDefinition(x -> new IpProfile()));
    }

    @Override
    public Optional<Credentials> validate(final Credentials credentials, final WebContext context, final SessionStore sessionStore) {
        init();

        val ip = ((TokenCredentials) credentials).getToken();

        if (!this.pattern.matcher(ip).matches()) {
            throw new CredentialsException("Unauthorized IP address: " + ip);
        }

        val profile = (IpProfile) getProfileDefinition().newProfile();
        profile.setId(ip);
        logger.debug("profile: {}", profile);

        credentials.setUserProfile(profile);

        return Optional.of(credentials);
    }
}
