package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * The disjunction of authorizers.
 * @param <U> Type of profile
 *
 * @author Sergey Morgunov
 * @since 3.4.0
 */
public class OrAuthorizer<U extends CommonProfile> implements Authorizer<U> {

    private final List<Authorizer<U>> authorizers;

    public OrAuthorizer(List<Authorizer<U>> authorizers) {
        this.authorizers = authorizers;
    }

    @Override
    public boolean isAuthorized(WebContext context, List<U> profiles) {
        for (Authorizer<U> authorizer : authorizers) {
            if (authorizer.isAuthorized(context, profiles)) return true;
        }
        return false;
    }

    public static <U extends CommonProfile> OrAuthorizer<U> or(Authorizer<U>... authorizers) {
        return new OrAuthorizer<>(asList(authorizers));
    }
}
