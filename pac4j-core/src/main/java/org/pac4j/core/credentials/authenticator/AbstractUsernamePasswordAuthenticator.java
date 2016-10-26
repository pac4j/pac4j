package org.pac4j.core.credentials.authenticator;

import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.password.PasswordEncoder;
import org.pac4j.core.util.InitializableWebObject;

/**
 * An abstract username / password authenticator having a password encoder.
 * 
 * @author Jerome Leleu
 * @since 1.8.0
 */
public abstract class AbstractUsernamePasswordAuthenticator extends InitializableWebObject implements Authenticator<UsernamePasswordCredentials> {

    private PasswordEncoder passwordEncoder;

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public void setPasswordEncoder(final PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
}
