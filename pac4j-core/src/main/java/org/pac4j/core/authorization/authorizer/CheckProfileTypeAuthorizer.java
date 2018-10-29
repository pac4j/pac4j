package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;

import java.util.List;
import java.util.Set;

/**
 * Checks the profile type.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public class CheckProfileTypeAuthorizer<U extends CommonProfile> extends AbstractRequireAnyAuthorizer<Class<U>, U> {

    public CheckProfileTypeAuthorizer() { }

    public CheckProfileTypeAuthorizer(final Class<U>... types) {
        setElements(types);
    }

    public CheckProfileTypeAuthorizer(final List<Class<U>> types) {
        setElements(types);
    }

    public CheckProfileTypeAuthorizer(final Set<Class<U>> types) {
        setElements(types);
    }

    @Override
    protected boolean check(final WebContext context, final U profile, final Class<U> element) {
        return profile.getClass().isAssignableFrom(element);
    }

    public static <U extends CommonProfile> CheckProfileTypeAuthorizer<U> checkProfileType(Class<U> ... types) {
        return new CheckProfileTypeAuthorizer<>(types);
    }

    public static <U extends CommonProfile> CheckProfileTypeAuthorizer<U> checkProfileType(List<Class<U>> types) {
        return new CheckProfileTypeAuthorizer<>(types);
    }

    public static <U extends CommonProfile> CheckProfileTypeAuthorizer<U> checkProfileType(Set<Class<U>> types) {
        return new CheckProfileTypeAuthorizer<>(types);
    }

}
