package org.pac4j.core.credentials.password;

/**
 * Encode a password.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public interface PasswordEncoder {

    /**
     * Encode a password.
     *
     * @param password the not encoded password
     * @return the encoded password
     */
    String encode(String password);

    /**
     * Validate if a plainPassword matches
     *
     * @param plainPassword
     *            the not encoded password to check
     * @param encodedPassword
     *            the encoded password
     * @return <code>true</code> if they match
     */
    boolean matches(String plainPassword, String encodedPassword);
}
