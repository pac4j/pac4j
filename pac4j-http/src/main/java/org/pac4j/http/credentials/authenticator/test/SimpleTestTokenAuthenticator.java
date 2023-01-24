package org.pac4j.http.credentials.authenticator.test;

import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;

import java.util.Optional;

/**
 * This class is a simple test authenticator: token must not be blank.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class SimpleTestTokenAuthenticator implements Authenticator {

    @Override
    public Optional<Credentials> validate(final CallContext ctx, final Credentials cred) {
        if (cred == null) {
            throw new CredentialsException("credentials must not be null");
        }
        val credentials = (TokenCredentials) cred;
        if (CommonHelper.isBlank(credentials.getToken())) {
            throw new CredentialsException("token must not be blank");
        }
        val token = credentials.getToken();
        val profile = new CommonProfile();
        profile.setId(token);
        credentials.setUserProfile(profile);

        return Optional.of(credentials);
    }
}
