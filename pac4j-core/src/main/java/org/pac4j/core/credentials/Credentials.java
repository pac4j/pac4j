package org.pac4j.core.credentials;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.pac4j.core.logout.LogoutType;
import org.pac4j.core.profile.UserProfile;

import java.io.Serial;
import java.io.Serializable;

/**
 * The credentials.
 *
 * @author Jerome LELEU
 * @since 6.0.0
 */
@EqualsAndHashCode
@ToString
public abstract class Credentials implements Serializable {

    @Serial
    private static final long serialVersionUID = 1197047159413927875L;

    @Getter
    @Setter
    private UserProfile userProfile = null;

    @Getter
    protected LogoutType logoutType = null;

    public boolean isForAuthentication() {
        return logoutType == null;
    }
}
