package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;

import java.util.List;
import java.util.Set;

/**
 * Checks an access if the user profile has all the permissions.
 *
 * @param <U> the user profile
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class RequireAllPermissionsAuthorizer<U extends CommonProfile> extends AbstractRequireAllAuthorizer<String, U> {

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
    protected boolean check(final WebContext context, final U profile, final String element) {
        final Set<String> profilePermissions = profile.getPermissions();
        return profilePermissions.contains(element);
    }

    public static <U extends CommonProfile> RequireAllPermissionsAuthorizer<U> requireAllPermissions(String ... permissions) {
        return new RequireAllPermissionsAuthorizer<>(permissions);
    }

    public static <U extends CommonProfile> RequireAllPermissionsAuthorizer<U> requireAllPermissions(List<String> permissions) {
        return new RequireAllPermissionsAuthorizer<>(permissions);
    }

    public static <U extends CommonProfile> RequireAllPermissionsAuthorizer<U> requireAllPermissions(Set<String> permissions) {
        return new RequireAllPermissionsAuthorizer<>(permissions);
    }
}
