package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * The disjunction of authorizers.
 * @param <U> Type of profile
 *
 * @author Sergey Morgunov
 * @since 3.4.0
 */
public class OrAuthorizer<U extends UserProfile> implements Authorizer<U> {

    private final List<Authorizer<U>> authorizers;

    public OrAuthorizer(final List<Authorizer<U>> authorizers) {
        this.authorizers = authorizers;
    }

    @Override
    public boolean isAuthorized(final WebContext context, final List<U> profiles) {
        for (final Authorizer<U> authorizer : authorizers) {
            if (authorizer.isAuthorized(context, profiles)) return true;
        }
        return false;
    }

    public static <U extends UserProfile> OrAuthorizer<U> or(final Authorizer<U>... authorizers) {
        return new OrAuthorizer<>(asList(authorizers));
    }
}
