package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * The conjunction of authorizers.
 * @param <U> Type of profile
 *
 * @author Sergey Morgunov
 * @since 3.4.0
 */
public class AndAuthorizer<U extends UserProfile> implements Authorizer<U> {

    private final List<Authorizer<U>> authorizers;

    public AndAuthorizer(List<Authorizer<U>> authorizers) {
        this.authorizers = authorizers;
    }

    @Override
    public boolean isAuthorized(WebContext context, List<U> profiles) {
        for (Authorizer<U> authorizer : authorizers) {
            if (!authorizer.isAuthorized(context, profiles)) return false;
        }
        return true;
    }

    public static <U extends UserProfile> Authorizer<U> and(Authorizer<U>... authorizers) {
        return new AndAuthorizer<>(asList(authorizers));
    }

}
