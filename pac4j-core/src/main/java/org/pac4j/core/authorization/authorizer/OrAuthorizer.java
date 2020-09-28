package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * The disjunction of authorizers.
 *
 * @author Sergey Morgunov
 * @since 3.4.0
 */
public class OrAuthorizer implements Authorizer {

    private final List<Authorizer> authorizers;

    public OrAuthorizer(List<Authorizer> authorizers) {
        this.authorizers = authorizers;
    }

    @Override
    public boolean isAuthorized(WebContext context, List<UserProfile> profiles) {
        for (Authorizer authorizer : authorizers) {
            if (authorizer.isAuthorized(context, profiles)) return true;
        }
        return false;
    }

    public static OrAuthorizer or(Authorizer... authorizers) {
        return new OrAuthorizer(asList(authorizers));
    }
}
