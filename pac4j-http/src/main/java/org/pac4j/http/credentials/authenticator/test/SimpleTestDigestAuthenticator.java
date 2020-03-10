package org.pac4j.http.credentials.authenticator.test;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.TokenCredentials;
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
public class SimpleTestDigestAuthenticator implements Authenticator<TokenCredentials> {

    @Override
    public void validate(final TokenCredentials credentials, final WebContext context) {
        if (credentials == null) {
            throw new CredentialsException("No credential");
        }
        if (!(credentials instanceof DigestCredentials)) {
            throw new CredentialsException ("Unsupported credentials type " + credentials.getClass());
        }
        final DigestCredentials digestCredentials = (DigestCredentials) credentials;
        final String username = digestCredentials.getUsername();
        if (CommonHelper.isBlank(username)) {
            throw new CredentialsException("Username cannot be blank");
        }

        final String token = credentials.getToken();
        if (CommonHelper.isBlank(token)) {
            throw new CredentialsException("Token cannot be blank");
        }

        final CommonProfile profile = new CommonProfile();
        profile.setId(username);
        credentials.setUserProfile(profile);
    }
}
