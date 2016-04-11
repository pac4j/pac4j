package org.pac4j.core.authorization.generator;

import org.pac4j.core.profile.CommonProfile;

import java.util.Arrays;
import java.util.List;

/**
 * Grant default roles and/or permissions to a user profile.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class DefaultRolesPermissionsAuthorizationGenerator<U extends CommonProfile> implements AuthorizationGenerator<U> {

    private final List<String> defaultRoles;

    private final List<String> defaultPermissions;

    public DefaultRolesPermissionsAuthorizationGenerator(final List<String> defaultRoles, final List<String> defaultPermissions) {
        this.defaultRoles = defaultRoles;
        this.defaultPermissions = defaultPermissions;
    }

    public DefaultRolesPermissionsAuthorizationGenerator(final String[] defaultRoles, final String[] defaultPermissions) {
        if (defaultRoles != null) {
            this.defaultRoles = Arrays.<String>asList(defaultRoles);
        } else {
            this.defaultRoles = null;
        }
        if (defaultPermissions != null) {
            this.defaultPermissions = Arrays.<String>asList(defaultPermissions);
        } else {
            this.defaultPermissions = null;
        }
    }

    public void generate(final U profile) {
        if (defaultRoles != null) {
            profile.addRoles(defaultRoles);
        }
        if (defaultPermissions != null) {
            profile.addPermissions(defaultPermissions);
        }
    }
}
