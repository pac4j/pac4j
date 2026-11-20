package org.pac4j.core.credentials;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.util.Map;
import java.util.Objects;

/**
 * This credentials represents a token.
 *
 * @author Jerome Leleu
 * @since 1.7.0
 */
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = "token")
@NoArgsConstructor
@Getter
@Setter
public class TokenCredentials extends Credentials {

    @Serial
    private static final long serialVersionUID = -4270718634364817595L;

    private String token;

    /** Whether the access token is DPoP-bound, or null until its confirmation has been inspected. */
    @EqualsAndHashCode.Exclude
    private Boolean dpopBound;

    public TokenCredentials(final String token) {
        this.token = token;
    }

    public void setToken(final String token) {
        if (!Objects.equals(this.token, token)) {
            this.token = token;
            dpopBound = null;
        }
    }

    /**
     * Record the confirmation claim from the validated token or its introspection response (RFC 9449, section 6).
     *
     * @param confirmation the value of the cnf claim
     */
    public void setTokenConfirmation(final Object confirmation) {
        dpopBound = confirmation instanceof Map<?, ?> methods && methods.containsKey("jkt");
    }
}
