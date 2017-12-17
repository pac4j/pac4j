package org.pac4j.core.authorization.generator;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;

import java.util.Arrays;
import java.util.Collection;

/**
 * Grant default roles and/or permissions to a user profile.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class DefaultRolesPermissionsAuthorizationGenerator<U extends CommonProfile> implements AuthorizationGenerator<U> {

    private final Collection<String> defaultRoles;

    private final Collection<String> defaultPermissions;

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
    public U generate(final WebContext context, final U profile) {
        if (defaultRoles != null) {
            profile.addRoles(defaultRoles);
        }
        if (defaultPermissions != null) {
            profile.addPermissions(defaultPermissions);
        }
        return profile;
    }
}
