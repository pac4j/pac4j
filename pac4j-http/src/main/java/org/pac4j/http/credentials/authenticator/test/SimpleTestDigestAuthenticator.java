package org.pac4j.http.credentials.authenticator.test;

import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.http.credentials.DigestCredentials;

import java.util.Optional;

/**
 * This class is a simple test authenticator: username and token must not be blank.
 *
 * @author Mircea Carasel
 * @since 1.9.0
 */
public class SimpleTestDigestAuthenticator implements Authenticator {

    @Override
    public Optional<Credentials> validate(final CallContext ctx, final Credentials cred) {
        if (cred == null) {
            throw new CredentialsException("No credential");
        }
        if (!(cred instanceof DigestCredentials digestCredentials)) {
            throw new CredentialsException ("Unsupported credentials type " + cred.getClass());
        }
        var username = digestCredentials.getUsername();
        if (CommonHelper.isBlank(username)) {
            throw new CredentialsException("Username cannot be blank");
        }

        val token = digestCredentials.getToken();
        if (CommonHelper.isBlank(token)) {
            throw new CredentialsException("Token cannot be blank");
        }

        val profile = new CommonProfile();
        profile.setId(username);
        digestCredentials.setUserProfile(profile);

        return Optional.of(digestCredentials);
    }
}
