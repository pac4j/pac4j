package org.pac4j.core.profile;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.val;
import org.pac4j.core.util.CommonHelper;

import java.security.Principal;

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
@Getter
@EqualsAndHashCode
@ToString
public class Pac4JPrincipal implements Principal {

    private final String name;

    /**
     * <p>Constructor for Pac4JPrincipal.</p>
     *
     * @param profile a {@link UserProfile} object
     */
    public Pac4JPrincipal(final UserProfile profile) {
        val username = profile.getUsername();
        if (CommonHelper.isNotBlank(username)) {
            this.name = username;
        } else {
            this.name = profile.getId();
        }
    }
}
