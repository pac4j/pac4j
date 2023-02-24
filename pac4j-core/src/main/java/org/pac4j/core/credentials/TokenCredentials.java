package org.pac4j.core.credentials;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * This credentials represents a token.
 *
 * @author Jerome Leleu
 * @since 1.7.0
 */
@EqualsAndHashCode
@ToString
public class TokenCredentials extends Credentials {

    private static final long serialVersionUID = -4270718634364817595L;

    @Getter
    private String token;

    /**
     * <p>Constructor for TokenCredentials.</p>
     *
     * @param token a {@link java.lang.String} object
     */
    public TokenCredentials(String token) {
        this.token = token;
    }
}
