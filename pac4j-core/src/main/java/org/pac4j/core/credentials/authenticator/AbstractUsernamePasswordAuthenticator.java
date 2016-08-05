package org.pac4j.core.credentials.authenticator;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.password.NopPasswordEncoder;
import org.pac4j.core.credentials.password.PasswordEncoder;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableWebObject;

/**
 * An abstract username / password authenticator having a password encoder.
 * 
 * @author Jerome Leleu
 * @since 1.8.0
 */
public abstract class AbstractUsernamePasswordAuthenticator extends InitializableWebObject implements Authenticator<UsernamePasswordCredentials> {

    private PasswordEncoder passwordEncoder = new NopPasswordEncoder();

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotNull("passwordEncoder", this.passwordEncoder);
    }
}

