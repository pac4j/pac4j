package org.pac4j.core.credentials;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * This class represents a username and a password credentials
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
@EqualsAndHashCode
@ToString(exclude = "password")
public class UsernamePasswordCredentials extends Credentials {

    private static final long serialVersionUID = -7229878989627796565L;

    @Getter
    private String username;

    @Getter
    private String password;

    /**
     * <p>Constructor for UsernamePasswordCredentials.</p>
     *
     * @param username a {@link java.lang.String} object
     * @param password a {@link java.lang.String} object
     */
    public UsernamePasswordCredentials(final String username, final String password) {
        this.username = username;
        this.password = password;
    }
}
