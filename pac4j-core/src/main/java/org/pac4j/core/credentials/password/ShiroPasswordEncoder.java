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

    /**
     * <p>Constructor for ShiroPasswordEncoder.</p>
     *
     * @param delegate a {@link PasswordService} object
     */
    public ShiroPasswordEncoder(final PasswordService delegate) {
        setDelegate(delegate);
    }

    /** {@inheritDoc} */
    @Override
    public String encode(final String password) {
        return delegate.encryptPassword(password);
    }

    /** {@inheritDoc} */
    @Override
    public boolean matches(final String plainPassword, final String encodedPassword) {
        return delegate.passwordsMatch(plainPassword, encodedPassword);
    }

    /**
     * <p>Getter for the field <code>delegate</code>.</p>
     *
     * @return a {@link PasswordService} object
     */
    public PasswordService getDelegate() {
        return delegate;
    }

    /**
     * <p>Setter for the field <code>delegate</code>.</p>
     *
     * @param delegate a {@link PasswordService} object
     */
    public void setDelegate(final PasswordService delegate) {
        CommonHelper.assertNotNull("delegate", delegate);
        this.delegate = delegate;
    }
}
