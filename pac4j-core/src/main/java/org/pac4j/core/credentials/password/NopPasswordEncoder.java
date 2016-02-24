package org.pac4j.core.credentials.password;

/**
 * A no-operation password encoder.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class NopPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(final String password) {
        return password;
    }
}
