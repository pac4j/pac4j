package org.pac4j.core.profile;

import java.security.Principal;
import java.util.Objects;

import org.pac4j.core.util.CommonHelper;

/**
 * 
 * Default implementation for {@link Principal} based on a pac4j
 * {@link BasicUserProfile}.
 * 
 * It determines the name based on the profile's username, and fallbacks to id
 * if it doesn't exist.
 * 
 * @author Victor Noel
 * @since 3.0.0
 */
public class Pac4JPrincipal implements Principal {

    private final String name;

    public Pac4JPrincipal(final UserProfile profile) {
        final String username = profile.getUsername();
        if (CommonHelper.isNotBlank(username)) {
            this.name = username;
        } else {
            this.name = profile.getId();
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Pac4JPrincipal principal = (Pac4JPrincipal) o;
        return CommonHelper.areEquals(this.getName(), principal.getName());
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "profileId", this.name);
    }
}
