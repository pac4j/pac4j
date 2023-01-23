package org.pac4j.core.credentials;

import lombok.Getter;
import org.pac4j.core.logout.LogoutType;

import java.io.Serial;

/**
 * The logout credentials.
 *
 * @author Jerome LELEU
 * @since 6.0.0
 */
public abstract class LogoutCredentials implements Credentials {

    @Serial
    private static final long serialVersionUID = 2731359448582897749L;

    @Getter
    private LogoutType type = null;

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();
}
