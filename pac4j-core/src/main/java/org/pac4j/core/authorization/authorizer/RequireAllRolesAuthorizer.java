package org.pac4j.core.authorization.authorizer;

import lombok.val;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.UserProfile;

import java.util.List;
import java.util.Set;

/**
 * Checks an access if the user profile has all the roles.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class RequireAllRolesAuthorizer extends AbstractRequireAllAuthorizer<String> {

    public RequireAllRolesAuthorizer() { }

    public RequireAllRolesAuthorizer(final String... roles) {
        setElements(roles);
    }

    public RequireAllRolesAuthorizer(final List<String> roles) {
        setElements(roles);
    }

    public RequireAllRolesAuthorizer(final Set<String> roles) {
        setElements(roles);
    }

    @Override
    protected boolean check(final WebContext context, final SessionStore sessionStore, final UserProfile profile, final String element) {
        val profileRoles = profile.getRoles();
        return profileRoles.contains(element);
    }

    public static RequireAllRolesAuthorizer requireAllRoles(String ... roles) {
        return new RequireAllRolesAuthorizer(roles);
    }

    public static RequireAllRolesAuthorizer requireAllRoles(List<String> roles) {
        return new RequireAllRolesAuthorizer(roles);
    }

    public static RequireAllRolesAuthorizer requireAllRoles(Set<String> roles) {
        return new RequireAllRolesAuthorizer(roles);
    }
}
