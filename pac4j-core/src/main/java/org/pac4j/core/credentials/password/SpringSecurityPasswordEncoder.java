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

    public SpringSecurityPasswordEncoder(final org.springframework.security.crypto.password.PasswordEncoder delegate) {
        CommonHelper.assertNotNull("delegate", delegate);
        this.delegate = delegate;
    }

    @Override
    public String encode(final String password) {
        return delegate.encode(password);
    }

    @Override
    public boolean matches(final String plainPassword, final String encodedPassword) {
        return delegate.matches(plainPassword, encodedPassword);
    }

    public org.springframework.security.crypto.password.PasswordEncoder getDelegate() {
        return delegate;
    }
}
