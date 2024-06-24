package org.pac4j.core.credentials.password;

import lombok.val;
import org.mindrot.jbcrypt.BCrypt;

/**
 * A password encoder for bcrypt and using a salt.
 *
 * Add the <code>jBcrypt</code> dependency to use this class.
 *
 * @author Victor Noël
 * @since 1.9.2
 */
public class JBCryptPasswordEncoder implements PasswordEncoder {

    /** {@inheritDoc} */
    @Override
    public String encode(final String password) {
        val salt = BCrypt.gensalt();
        return BCrypt.hashpw(password, salt);
    }

    /** {@inheritDoc} */
    @Override
    public boolean matches(final String plainPassword, final String encodedPassword) {
        return BCrypt.checkpw(plainPassword, encodedPassword);
    }
}
