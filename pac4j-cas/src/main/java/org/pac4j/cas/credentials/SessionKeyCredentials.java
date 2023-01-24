package org.pac4j.cas.credentials;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.pac4j.core.credentials.LogoutCredentials;
import org.pac4j.core.logout.LogoutType;

/**
 * The logout credentials with a session key.
 *
 * @author Jerome LELEU
 * @since 6.0.0
 */
@EqualsAndHashCode
@ToString(callSuper = true)
public class SessionKeyCredentials extends LogoutCredentials {

    @Getter
    private final String sessionKey;

    public SessionKeyCredentials(final LogoutType type, final String sessionKey) {
        this.type = type;
        this.sessionKey = sessionKey;
    }
}
