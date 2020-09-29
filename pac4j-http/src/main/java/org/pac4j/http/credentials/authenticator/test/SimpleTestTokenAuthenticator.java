package org.pac4j.http.credentials.authenticator.test;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.credentials.TokenCredentials;

/**
 * This class is a simple test authenticator: token must not be blank.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class SimpleTestTokenAuthenticator implements Authenticator {

    @Override
    public void validate(final Credentials cred, final WebContext context) {
        if (cred == null) {
            throw new CredentialsException("credentials must not be null");
        }
        final TokenCredentials credentials = (TokenCredentials) cred;
        if (CommonHelper.isBlank(credentials.getToken())) {
            throw new CredentialsException("token must not be blank");
        }
        final String token = credentials.getToken();
        final CommonProfile profile = new CommonProfile();
        profile.setId(token);
        credentials.setUserProfile(profile);
    }
}
