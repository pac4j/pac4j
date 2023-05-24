package org.pac4j.core.authorization.authorizer;

import lombok.ToString;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.UserProfile;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * The conjunction of authorizers.
 *
 * @author Sergey Morgunov
 * @since 3.4.0
 */
@ToString
public class AndAuthorizer implements Authorizer {

    private final List<Authorizer> authorizers;

    /**
     * <p>Constructor for AndAuthorizer.</p>
     *
     * @param authorizers a {@link List} object
     */
    public AndAuthorizer(List<Authorizer> authorizers) {
        this.authorizers = authorizers;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAuthorized(final WebContext context, final SessionStore sessionStore, final List<UserProfile> profiles) {
        for (var authorizer : authorizers) {
            if (!authorizer.isAuthorized(context, sessionStore, profiles)) return false;
        }
        return true;
    }

    /**
     * <p>and.</p>
     *
     * @param authorizers a {@link Authorizer} object
     * @return a {@link Authorizer} object
     */
    public static Authorizer and(Authorizer... authorizers) {
        return new AndAuthorizer(asList(authorizers));
    }
}
