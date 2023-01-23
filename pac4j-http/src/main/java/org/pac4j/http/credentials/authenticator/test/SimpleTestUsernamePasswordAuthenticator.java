package org.pac4j.http.credentials.authenticator.test;

import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.credentials.AuthenticationCredentials;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;

import java.util.Optional;

/**
 * This class is a simple test authenticator: password must match username.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class SimpleTestUsernamePasswordAuthenticator implements Authenticator {

    @Override
    public Optional<AuthenticationCredentials> validate(final CallContext ctx, final AuthenticationCredentials cred) {
        if (cred == null) {
            throw new CredentialsException("No credential");
        }
        val credentials = (UsernamePasswordCredentials) cred;
        var username = credentials.getUsername();
        var password = credentials.getPassword();
        if (CommonHelper.isBlank(username)) {
            throw new CredentialsException("Username cannot be blank");
        }
        if (CommonHelper.isBlank(password)) {
            throw new CredentialsException("Password cannot be blank");
        }
        if (CommonHelper.areNotEquals(username, password)) {
            throw new CredentialsException("Username : '" + username + "' does not match password");
        }
        val profile = new CommonProfile();
        profile.setId(username);
        profile.addAttribute(Pac4jConstants.USERNAME, username);
        credentials.setUserProfile(profile);

        return Optional.of(credentials);
    }
}
