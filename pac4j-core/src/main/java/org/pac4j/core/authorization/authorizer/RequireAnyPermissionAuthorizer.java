package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;

import java.util.List;
import java.util.Set;

/**
 * Checks an access if the user profile has any of the permissions.
 *
 * @param <U> the user profile
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class RequireAnyPermissionAuthorizer<U extends UserProfile> extends AbstractRequireAnyAuthorizer<String, U> {

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
    protected boolean check(final WebContext context, final U profile, final String element) {
        final Set<String> profilePermissions = profile.getPermissions();
        return profilePermissions.contains(element);
    }

    public static <U extends UserProfile> RequireAnyPermissionAuthorizer<U> requireAnyPermission(final String ... permissions) {
        return new RequireAnyPermissionAuthorizer<>(permissions);
    }

    public static <U extends UserProfile> RequireAnyPermissionAuthorizer<U> requireAnyPermission(final List<String> permissions) {
        return new RequireAnyPermissionAuthorizer<>(permissions);
    }

    public static <U extends UserProfile> RequireAnyPermissionAuthorizer<U> requireAnyPermission(final Set<String> permissions) {
        return new RequireAnyPermissionAuthorizer<>(permissions);
    }
}
