package org.pac4j.core.authorization.checker;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.client.Client;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.UserProfile;

import java.util.List;
import java.util.Map;

/**
 * The way to check authorizations.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
@FunctionalInterface
public interface AuthorizationChecker {

    /**
     * Check whether the user is authorized.
     *
     * @param context the web context
     * @param sessionStore the session store
     * @param profiles the profile
     * @param authorizerNames the authorizers
     * @param authorizersMap the map of authorizers
     * @param clients the clients
     * @return whether the user is authorized.
     */
    boolean isAuthorized(WebContext context, SessionStore sessionStore, List<UserProfile> profiles, String authorizerNames,
                         Map<String, Authorizer> authorizersMap, List<Client> clients);
}
