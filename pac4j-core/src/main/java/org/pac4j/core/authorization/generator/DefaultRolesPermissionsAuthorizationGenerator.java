package org.pac4j.core.authorization.generator;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.UserProfile;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

/**
 * Grant default roles and/or permissions to a user profile.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class DefaultRolesPermissionsAuthorizationGenerator implements AuthorizationGenerator {

    private final Collection<String> defaultRoles;

    private final Collection<String> defaultPermissions;

    /**
     * <p>Constructor for DefaultRolesAuthorizationGenerator.</p>
     */
    public DefaultRolesAuthorizationGenerator() {}

    public DefaultRolesPermissionsAuthorizationGenerator(final Collection<String> defaultRoles,
        final Collection<String> defaultPermissions) {
        this.defaultRoles = defaultRoles;
        this.defaultPermissions = defaultPermissions;
    }

    public DefaultRolesPermissionsAuthorizationGenerator(final String[] defaultRoles, final String[] defaultPermissions) {
        if (defaultRoles != null) {
            this.defaultRoles = Arrays.asList(defaultRoles);
        } else {
            this.defaultRoles = null;
        }
        if (defaultPermissions != null) {
            this.defaultPermissions = Arrays.asList(defaultPermissions);
        } else {
            this.defaultPermissions = null;
        }
    }

    @Override
    public Optional<UserProfile> generate(final WebContext context, final SessionStore sessionStore, final UserProfile profile) {
        if (defaultRoles != null) {
            profile.addRoles(defaultRoles);
        }
        if (defaultPermissions != null) {
            profile.addPermissions(defaultPermissions);
        }
        return Optional.of(profile);
    }

    /**
     * Setter for defaultRoles
     *
     * @param defaultRolesStr a coma-separated string of role names
     */
    public void setDefaultRoles(final String defaultRolesStr) {
        this.defaultRoles = Arrays.asList(defaultRolesStr.split(","));
    }

    /**
     * Setter for setDefaultPermissions
     *
     * @param defaultPermissionsStr a coma-separated string of permissions
     */
    public void setDefaultPermissions(final String defaultPermissionsStr) {
        this.defaultPermissions = Arrays.asList(defaultPermissionsStr.split(","));
    }
}
