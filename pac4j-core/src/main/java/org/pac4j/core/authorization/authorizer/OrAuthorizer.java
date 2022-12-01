package org.pac4j.core.authorization.authorizer;

import lombok.ToString;
import lombok.val;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.UserProfile;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * The disjunction of authorizers.
 *
 * @author Sergey Morgunov
 * @since 3.4.0
 */
@ToString
public class OrAuthorizer implements Authorizer {

    private final List<Authorizer> authorizers;

    public OrAuthorizer(List<Authorizer> authorizers) {
        this.authorizers = authorizers;
    }

    @Override
    public boolean isAuthorized(final WebContext context, final SessionStore sessionStore, final List<UserProfile> profiles) {
        for (val authorizer : authorizers) {
            if (authorizer.isAuthorized(context, sessionStore, profiles)) return true;
        }
        return false;
    }

    public static OrAuthorizer or(Authorizer... authorizers) {
        return new OrAuthorizer(asList(authorizers));
    }
}
