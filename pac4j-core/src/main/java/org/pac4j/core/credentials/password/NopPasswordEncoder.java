package org.pac4j.core.credentials.password;

import org.pac4j.core.util.CommonHelper;

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

    @Override
    public boolean matches(final String plainPassword, final String encodedPassword) {
        return CommonHelper.areEquals(plainPassword, encodedPassword);
    }
}
