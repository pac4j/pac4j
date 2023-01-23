package org.pac4j.core.credentials;

import lombok.Getter;
import lombok.Setter;
import org.pac4j.core.profile.UserProfile;

/**
 * The authentication credentials.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public abstract class AuthenticationCredentials implements Credentials {

    private static final long serialVersionUID = 4864923514027378583L;

    @Getter
    @Setter
    private UserProfile userProfile = null;

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();
}
