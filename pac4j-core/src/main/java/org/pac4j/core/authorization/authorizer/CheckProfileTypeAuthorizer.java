package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
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

    /**
     * <p>Constructor for CheckProfileTypeAuthorizer.</p>
     */
    public CheckProfileTypeAuthorizer() { }

    /**
     * <p>Constructor for CheckProfileTypeAuthorizer.</p>
     *
     * @param types a {@link Class} object
     */
    public CheckProfileTypeAuthorizer(final Class... types) {
        setElements(types);
    }

    /**
     * <p>Constructor for CheckProfileTypeAuthorizer.</p>
     *
     * @param types a {@link List} object
     */
    public CheckProfileTypeAuthorizer(final List<Class> types) {
        setElements(types);
    }

    /**
     * <p>Constructor for CheckProfileTypeAuthorizer.</p>
     *
     * @param types a {@link Set} object
     */
    public CheckProfileTypeAuthorizer(final Set<Class> types) {
        setElements(types);
    }

    /** {@inheritDoc} */
    @Override
    protected boolean check(final WebContext context, final SessionStore sessionStore, final UserProfile profile, final Class element) {
        return profile.getClass().isAssignableFrom(element);
    }

    /**
     * <p>checkProfileType.</p>
     *
     * @param types a {@link Class} object
     * @return a {@link CheckProfileTypeAuthorizer} object
     */
    public static CheckProfileTypeAuthorizer checkProfileType(Class... types) {
        return new CheckProfileTypeAuthorizer(types);
    }

    /**
     * <p>checkProfileType.</p>
     *
     * @param types a {@link List} object
     * @return a {@link CheckProfileTypeAuthorizer} object
     */
    public static CheckProfileTypeAuthorizer checkProfileType(List<Class> types) {
        return new CheckProfileTypeAuthorizer(types);
    }

    /**
     * <p>checkProfileType.</p>
     *
     * @param types a {@link Set} object
     * @return a {@link CheckProfileTypeAuthorizer} object
     */
    public static CheckProfileTypeAuthorizer checkProfileType(Set<Class> types) {
        return new CheckProfileTypeAuthorizer(types);
    }
}
