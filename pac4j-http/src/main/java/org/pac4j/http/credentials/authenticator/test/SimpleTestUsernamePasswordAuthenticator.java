package org.pac4j.http.credentials.authenticator.test;

import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a simple test authenticator: password must match username.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class SimpleTestUsernamePasswordAuthenticator implements Authenticator<UsernamePasswordCredentials> {

    protected static final Logger logger = LoggerFactory.getLogger(SimpleTestUsernamePasswordAuthenticator.class);

    @Override
    public void validate(final UsernamePasswordCredentials credentials, final WebContext context) throws HttpAction, CredentialsException {
        if (credentials == null) {
            throwsException("No credential");
        }
        String username = credentials.getUsername();
        String password = credentials.getPassword();
        if (CommonHelper.isBlank(username)) {
            throwsException("Username cannot be blank");
        }
        if (CommonHelper.isBlank(password)) {
            throwsException("Password cannot be blank");
        }
        if (CommonHelper.areNotEquals(username, password)) {
            throwsException("Username : '" + username + "' does not match password");
        }
        final CommonProfile profile = new CommonProfile();
        profile.setId(username);
        profile.addAttribute(Pac4jConstants.USERNAME, username);
        credentials.setUserProfile(profile);
    }

    protected void throwsException(final String message) throws CredentialsException {
        throw new CredentialsException(message);
    }
}
