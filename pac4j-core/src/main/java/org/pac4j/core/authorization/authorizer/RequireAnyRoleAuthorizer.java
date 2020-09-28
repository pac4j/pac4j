package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;

import java.util.List;
import java.util.Set;

/**
 * Checks an access if the user profile has any of the roles.
 *
 * @param <U> the user profile
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class RequireAnyRoleAuthorizer extends AbstractRequireAnyAuthorizer<String> {

    public RequireAnyRoleAuthorizer() { }

    public RequireAnyRoleAuthorizer(final String... roles) {
        setElements(roles);
    }

    public RequireAnyRoleAuthorizer(final List<String> roles) {
        setElements(roles);
    }

    public RequireAnyRoleAuthorizer(final Set<String> roles) { setElements(roles); }

    @Override
    protected boolean check(final WebContext context, final UserProfile profile, final String element) {
        final Set<String> profileRoles = profile.getRoles();
        return profileRoles.contains(element);
    }

    public static RequireAnyRoleAuthorizer requireAnyRole(String ... roles) {
        return new RequireAnyRoleAuthorizer(roles);
    }

    public static RequireAnyRoleAuthorizer requireAnyRole(List<String> roles) {
        return new RequireAnyRoleAuthorizer(roles);
    }

    public static RequireAnyRoleAuthorizer requireAnyRole(Set<String> roles) {
        return new RequireAnyRoleAuthorizer(roles);
    }
}
