package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.UserProfile;

import java.util.List;
import java.util.Set;

/**
 * Checks an access if the user profile has any of the permissions.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class RequireAnyPermissionAuthorizer extends AbstractRequireAnyAuthorizer<String> {

    public RequireAnyPermissionAuthorizer() { }

    public RequireAnyPermissionAuthorizer(final String... permissions) {
        setElements(permissions);
    }

    public RequireAnyPermissionAuthorizer(final List<String> permissions) {
        setElements(permissions);
    }

    public RequireAnyPermissionAuthorizer(final Set<String> permissions) {
        setElements(permissions);
    }

    @Override
    protected boolean check(final WebContext context, final SessionStore sessionStore, final UserProfile profile, final String element) {
        final Set<String> profilePermissions = profile.getPermissions();
        return profilePermissions.contains(element);
    }

    public static RequireAnyPermissionAuthorizer requireAnyPermission(String ... permissions) {
        return new RequireAnyPermissionAuthorizer(permissions);
    }

    public static RequireAnyPermissionAuthorizer requireAnyPermission(List<String> permissions) {
        return new RequireAnyPermissionAuthorizer(permissions);
    }

    public static RequireAnyPermissionAuthorizer requireAnyPermission(Set<String> permissions) {
        return new RequireAnyPermissionAuthorizer(permissions);
    }
}
