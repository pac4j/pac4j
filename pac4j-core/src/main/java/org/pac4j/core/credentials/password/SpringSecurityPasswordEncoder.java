package org.pac4j.core.credentials.password;

import org.pac4j.core.util.CommonHelper;

/**
 * A password encoder based on {@link org.springframework.security.crypto.password.PasswordEncoder} from the Spring
 * Security Crypto package.
 *
 * Add the <code>spring-security-crypto</code> dependency to use this class.
 *
 * @author Victor Noël
 * @since 1.9.2
 */
public class SpringSecurityPasswordEncoder implements PasswordEncoder {

    private final org.springframework.security.crypto.password.PasswordEncoder delegate;

    /**
     * <p>Constructor for SpringSecurityPasswordEncoder.</p>
     *
     * @param delegate a {@link org.springframework.security.crypto.password.PasswordEncoder} object
     */
    public SpringSecurityPasswordEncoder(final org.springframework.security.crypto.password.PasswordEncoder delegate) {
        CommonHelper.assertNotNull("delegate", delegate);
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public String encode(final String password) {
        return delegate.encode(password);
    }

    /** {@inheritDoc} */
    @Override
    public boolean matches(final String plainPassword, final String encodedPassword) {
        return delegate.matches(plainPassword, encodedPassword);
    }

    /**
     * <p>Getter for the field <code>delegate</code>.</p>
     *
     * @return a {@link org.springframework.security.crypto.password.PasswordEncoder} object
     */
    public org.springframework.security.crypto.password.PasswordEncoder getDelegate() {
        return delegate;
    }
}
