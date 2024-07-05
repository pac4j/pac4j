package org.pac4j.core.credentials;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.pac4j.core.logout.LogoutType;

/**
 * The logout credentials with a session key.
 *
 * @author Jerome LELEU
 * @since 6.0.0
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@Getter
@Setter
public class SessionKeyCredentials extends Credentials {

    private String sessionKey;

    /**
     * <p>Constructor for SessionKeyCredentials.</p>
     *
     * @param type a {@link LogoutType} object
     * @param sessionKey a {@link String} object
     */
    public SessionKeyCredentials(final LogoutType type, final String sessionKey) {
        this.logoutType = type;
        this.sessionKey = sessionKey;
    }
}
