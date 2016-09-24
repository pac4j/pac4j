package org.pac4j.core.credentials.password;

import org.apache.shiro.authc.credential.PasswordService;
import org.pac4j.core.util.CommonHelper;

/**
 * A password encoder based on {@link PasswordService} from Apache Shiro
 *
 * Add the <code>shiro-core</code> dependency to use this class.
 *
 * @author Victor Noël
 * @since 1.9.2
 */
public class ShiroPasswordEncoder implements PasswordEncoder {

    private PasswordService delegate;

    public ShiroPasswordEncoder(final PasswordService delegate) {
        setDelegate(delegate);
    }

    @Override
    public String encode(final String password) {
        return delegate.encryptPassword(password);
    }

    @Override
    public boolean matches(final String plainPassword, final String encodedPassword) {
        return delegate.passwordsMatch(plainPassword, encodedPassword);
    }

    public PasswordService getDelegate() {
        return delegate;
    }

    public void setDelegate(final PasswordService delegate) {
        CommonHelper.assertNotNull("delegate", delegate);
        this.delegate = delegate;
    }
}
