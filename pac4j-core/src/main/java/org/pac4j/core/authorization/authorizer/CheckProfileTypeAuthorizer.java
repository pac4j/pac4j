package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;

import java.util.List;
import java.util.Set;

/**
 * Checks the profile type.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public class CheckProfileTypeAuthorizer extends AbstractRequireAnyAuthorizer<Class> {

    public CheckProfileTypeAuthorizer() { }

    public CheckProfileTypeAuthorizer(final Class... types) {
        setElements(types);
    }

    public CheckProfileTypeAuthorizer(final List<Class> types) {
        setElements(types);
    }

    public CheckProfileTypeAuthorizer(final Set<Class> types) {
        setElements(types);
    }

    @Override
    protected boolean check(final WebContext context, final UserProfile profile, final Class element) {
        return profile.getClass().isAssignableFrom(element);
    }

    public static CheckProfileTypeAuthorizer checkProfileType(Class... types) {
        return new CheckProfileTypeAuthorizer(types);
    }

    public static CheckProfileTypeAuthorizer checkProfileType(List<Class> types) {
        return new CheckProfileTypeAuthorizer(types);
    }

    public static CheckProfileTypeAuthorizer checkProfileType(Set<Class> types) {
        return new CheckProfileTypeAuthorizer(types);
    }
}
