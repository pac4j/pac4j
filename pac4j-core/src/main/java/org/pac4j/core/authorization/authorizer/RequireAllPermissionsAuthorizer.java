package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;

import java.util.List;
import java.util.Set;

/**
 * Checks an access if the user profile has all the permissions.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class RequireAllPermissionsAuthorizer extends AbstractRequireAllAuthorizer<String> {

    public RequireAllPermissionsAuthorizer() { }

    public RequireAllPermissionsAuthorizer(final String... permissions) {
        setElements(permissions);
    }

    public RequireAllPermissionsAuthorizer(final List<String> permissions) {
        setElements(permissions);
    }

    public RequireAllPermissionsAuthorizer(final Set<String> permissions) {
        setElements(permissions);
    }

    @Override
    protected boolean check(final WebContext context, final UserProfile profile, final String element) {
        final Set<String> profilePermissions = profile.getPermissions();
        return profilePermissions.contains(element);
    }

    public static RequireAllPermissionsAuthorizer requireAllPermissions(String ... permissions) {
        return new RequireAllPermissionsAuthorizer(permissions);
    }

    public static RequireAllPermissionsAuthorizer requireAllPermissions(List<String> permissions) {
        return new RequireAllPermissionsAuthorizer(permissions);
    }

    public static RequireAllPermissionsAuthorizer requireAllPermissions(Set<String> permissions) {
        return new RequireAllPermissionsAuthorizer(permissions);
    }
}
