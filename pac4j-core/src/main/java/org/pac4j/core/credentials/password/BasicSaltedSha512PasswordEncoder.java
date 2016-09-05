package org.pac4j.core.credentials.password;

import org.apache.commons.codec.digest.DigestUtils;
import org.pac4j.core.util.CommonHelper;

/**
 * A password encoder based on SHA512 and using a salt.
 *
 * Add the <code>commons-codec</code> dependency to use this class.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class BasicSaltedSha512PasswordEncoder implements PasswordEncoder {

    protected String salt;

    public BasicSaltedSha512PasswordEncoder() {
    }

    public BasicSaltedSha512PasswordEncoder(final String salt) {
        this.salt = salt;
    }

    @Override
    public String encode(final String password) {
        CommonHelper.assertNotBlank("salt", salt);

        return DigestUtils.sha512Hex(password + salt);
    }

    @Override
    public boolean matches(final String plainPassword, final String encodedPassword) {
        CommonHelper.assertNotBlank("salt", salt);

        return CommonHelper.areEquals(encode(plainPassword), encodedPassword);
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
