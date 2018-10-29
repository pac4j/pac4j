package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;

import java.util.List;
import java.util.Set;

/**
 * Checks an access if the user profile has any of the roles.
 *
 * @param <U> the user profile
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class RequireAnyRoleAuthorizer<U extends CommonProfile> extends AbstractRequireAnyAuthorizer<String, U> {

    public RequireAnyRoleAuthorizer() { }

    public RequireAnyRoleAuthorizer(final String... roles) {
        setElements(roles);
    }

    public RequireAnyRoleAuthorizer(final List<String> roles) {
        setElements(roles);
    }

    public RequireAnyRoleAuthorizer(final Set<String> roles) { setElements(roles); }

    @Override
    protected boolean check(final WebContext context, final U profile, final String element) {
        final Set<String> profileRoles = profile.getRoles();
        return profileRoles.contains(element);
    }

    public static <U extends CommonProfile> RequireAnyRoleAuthorizer<U> requireAnyRole(String ... roles) {
        return new RequireAnyRoleAuthorizer<>(roles);
    }

    public static <U extends CommonProfile> RequireAnyRoleAuthorizer<U> requireAnyRole(List<String> roles) {
        return new RequireAnyRoleAuthorizer<>(roles);
    }

    public static <U extends CommonProfile> RequireAnyRoleAuthorizer<U> requireAnyRole(Set<String> roles) {
        return new RequireAnyRoleAuthorizer<>(roles);
    }
}
