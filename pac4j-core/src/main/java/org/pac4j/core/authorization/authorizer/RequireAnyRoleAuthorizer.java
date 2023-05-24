package org.pac4j.core.authorization.authorizer;

import lombok.val;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.UserProfile;

import java.util.List;
import java.util.Set;

/**
 * Checks an access if the user profile has any of the roles.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class RequireAnyRoleAuthorizer extends AbstractRequireAnyAuthorizer<String> {

    /**
     * <p>Constructor for RequireAnyRoleAuthorizer.</p>
     */
    public RequireAnyRoleAuthorizer() { }

    /**
     * <p>Constructor for RequireAnyRoleAuthorizer.</p>
     *
     * @param roles a {@link String} object
     */
    public RequireAnyRoleAuthorizer(final String... roles) {
        setElements(roles);
    }

    /**
     * <p>Constructor for RequireAnyRoleAuthorizer.</p>
     *
     * @param roles a {@link List} object
     */
    public RequireAnyRoleAuthorizer(final List<String> roles) {
        setElements(roles);
    }

    /**
     * <p>Constructor for RequireAnyRoleAuthorizer.</p>
     *
     * @param roles a {@link Set} object
     */
    public RequireAnyRoleAuthorizer(final Set<String> roles) { setElements(roles); }

    /** {@inheritDoc} */
    @Override
    protected boolean check(final WebContext context, final SessionStore sessionStore, final UserProfile profile, final String element) {
        val profileRoles = profile.getRoles();
        if( profileRoles.isEmpty() ) {
            return false;
        }
        if( element == null ) {
            return true;
        }
        return profileRoles.contains(element);
    }

    /**
     * <p>requireAnyRole.</p>
     *
     * @param roles a {@link String} object
     * @return a {@link RequireAnyRoleAuthorizer} object
     */
    public static RequireAnyRoleAuthorizer requireAnyRole(String ... roles) {
        return new RequireAnyRoleAuthorizer(roles);
    }

    /**
     * <p>requireAnyRole.</p>
     *
     * @param roles a {@link List} object
     * @return a {@link RequireAnyRoleAuthorizer} object
     */
    public static RequireAnyRoleAuthorizer requireAnyRole(List<String> roles) {
        return new RequireAnyRoleAuthorizer(roles);
    }

    /**
     * <p>requireAnyRole.</p>
     *
     * @param roles a {@link Set} object
     * @return a {@link RequireAnyRoleAuthorizer} object
     */
    public static RequireAnyRoleAuthorizer requireAnyRole(Set<String> roles) {
        return new RequireAnyRoleAuthorizer(roles);
    }
}
