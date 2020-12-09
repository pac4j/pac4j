package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * The conjunction of authorizers.
 *
 * @author Sergey Morgunov
 * @since 3.4.0
 */
public class AndAuthorizer implements Authorizer {

    private final List<Authorizer> authorizers;

    public AndAuthorizer(List<Authorizer> authorizers) {
        this.authorizers = authorizers;
    }

    @Override
    public boolean isAuthorized(WebContext context, List<UserProfile> profiles) {
        for (Authorizer authorizer : authorizers) {
            if (!authorizer.isAuthorized(context, profiles)) return false;
        }
        return true;
    }

    public static Authorizer and(Authorizer... authorizers) {
        return new AndAuthorizer(asList(authorizers));
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "authorizers", authorizers);
    }
}
