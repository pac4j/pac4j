package org.pac4j.http.credentials.authenticator.test;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.http.credentials.DigestCredentials;

/**
 * This class is a simple test authenticator: username and token must not be blank.
 *
 * @author Mircea Carasel
 * @since 1.9.0
 */
public class SimpleTestDigestAuthenticator implements Authenticator {

    @Override
    public void validate(final Credentials cred, final WebContext context, final SessionStore sessionStore) {
        if (cred == null) {
            throw new CredentialsException("No credential");
        }
        if (!(cred instanceof DigestCredentials)) {
            throw new CredentialsException ("Unsupported credentials type " + cred.getClass());
        }
        var digestCredentials = (DigestCredentials) cred;
        var username = digestCredentials.getUsername();
        if (CommonHelper.isBlank(username)) {
            throw new CredentialsException("Username cannot be blank");
        }

        var token = digestCredentials.getToken();
        if (CommonHelper.isBlank(token)) {
            throw new CredentialsException("Token cannot be blank");
        }

        var profile = new CommonProfile();
        profile.setId(username);
        digestCredentials.setUserProfile(profile);
    }
}
