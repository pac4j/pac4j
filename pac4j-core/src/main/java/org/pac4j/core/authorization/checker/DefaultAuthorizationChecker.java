package org.pac4j.core.authorization.checker;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.authorization.authorizer.*;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.client.direct.AnonymousClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.Pac4jConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.pac4j.core.util.CommonHelper.*;

/**
 * Default way to check the authorizations (with default authorizers).
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
@Slf4j
public class DefaultAuthorizationChecker implements AuthorizationChecker {

    /** Constant <code>CSRF_AUTHORIZER</code> */
    protected static final CsrfAuthorizer CSRF_AUTHORIZER = new CsrfAuthorizer();
    /** Constant <code>IS_ANONYMOUS_AUTHORIZER</code> */
    protected static final IsAnonymousAuthorizer IS_ANONYMOUS_AUTHORIZER = new IsAnonymousAuthorizer();
    /** Constant <code>IS_AUTHENTICATED_AUTHORIZER</code> */
    protected static final IsAuthenticatedAuthorizer IS_AUTHENTICATED_AUTHORIZER =new IsAuthenticatedAuthorizer();
    /** Constant <code>IS_FULLY_AUTHENTICATED_AUTHORIZER</code> */
    protected static final IsFullyAuthenticatedAuthorizer IS_FULLY_AUTHENTICATED_AUTHORIZER = new IsFullyAuthenticatedAuthorizer();
    /** Constant <code>IS_REMEMBERED_AUTHORIZER</code> */
    protected static final IsRememberedAuthorizer IS_REMEMBERED_AUTHORIZER = new IsRememberedAuthorizer();

    /** {@inheritDoc} */
    @Override
    public boolean isAuthorized(final WebContext context, final SessionStore sessionStore, final List<UserProfile> profiles,
                                final String authorizersValue, final Map<String, Authorizer> authorizersMap, final List<Client> clients) {

        val authorizers = computeAuthorizers(context, profiles, authorizersValue, authorizersMap, clients);
        return isAuthorized(context, sessionStore, profiles, authorizers);
    }

    /**
     * <p>computeAuthorizers.</p>
     *
     * @param context a {@link org.pac4j.core.context.WebContext} object
     * @param profiles a {@link java.util.List} object
     * @param authorizersValue a {@link java.lang.String} object
     * @param authorizersMap a {@link java.util.Map} object
     * @param clients a {@link java.util.List} object
     * @return a {@link java.util.List} object
     */
    protected List<Authorizer> computeAuthorizers(final WebContext context, final List<UserProfile> profiles, final String authorizersValue,
                                                  final Map<String, Authorizer> authorizersMap, final List<Client> clients) {
        final List<Authorizer> authorizers;
        if (isBlank(authorizersValue)) {
            authorizers = computeDefaultAuthorizers(context, profiles, clients, authorizersMap);
        } else {
            if (authorizersValue.trim().startsWith(Pac4jConstants.ADD_ELEMENT)) {
                val authorizerNames = substringAfter(authorizersValue, Pac4jConstants.ADD_ELEMENT);
                authorizers = computeDefaultAuthorizers(context, profiles, clients, authorizersMap);
                authorizers.addAll(computeAuthorizersFromNames(authorizerNames, authorizersMap));
            } else {
                authorizers = computeAuthorizersFromNames(authorizersValue, authorizersMap);
            }
        }
        return authorizers;
    }

    /**
     * <p>computeDefaultAuthorizers.</p>
     *
     * @param context a {@link org.pac4j.core.context.WebContext} object
     * @param profiles a {@link java.util.List} object
     * @param clients a {@link java.util.List} object
     * @param authorizersMap a {@link java.util.Map} object
     * @return a {@link java.util.List} object
     */
    protected List<Authorizer> computeDefaultAuthorizers(final WebContext context, final List<UserProfile> profiles,
                                                         final List<Client> clients, final Map<String, Authorizer> authorizersMap) {
        val authorizers = new ArrayList<Authorizer>();
        if (containsClientType(clients, IndirectClient.class)) {
            authorizers.add(retrieveAuthorizer(DefaultAuthorizers.CSRF_CHECK, authorizersMap));
        }
        if (!containsClientType(clients, AnonymousClient.class)) {
            authorizers.add(retrieveAuthorizer(DefaultAuthorizers.IS_AUTHENTICATED, authorizersMap));
        }
        return authorizers;
    }

    /**
     * <p>computeAuthorizersFromNames.</p>
     *
     * @param authorizerNames a {@link java.lang.String} object
     * @param authorizersMap a {@link java.util.Map} object
     * @return a {@link java.util.List} object
     */
    protected List<Authorizer> computeAuthorizersFromNames(final String authorizerNames, final Map<String, Authorizer> authorizersMap) {
        assertNotNull("authorizersMap", authorizersMap);
        val authorizers = new ArrayList<Authorizer>();
        val names = authorizerNames.split(Pac4jConstants.ELEMENT_SEPARATOR);
        val nb = names.length;
        for (var i = 0; i < nb; i++) {
            val name = names[i].trim();
            if (!DefaultAuthorizers.NONE.equalsIgnoreCase(name)){
                val result = retrieveAuthorizer(name, authorizersMap);
                // we must have an authorizer defined for this name
                assertTrue(result != null, "The authorizer '" + name + "' must be defined in the security configuration");
                authorizers.add(result);
            }
        }
        return authorizers;
    }

    /**
     * <p>retrieveAuthorizer.</p>
     *
     * @param authorizerName a {@link java.lang.String} object
     * @param authorizersMap a {@link java.util.Map} object
     * @return a {@link org.pac4j.core.authorization.authorizer.Authorizer} object
     */
    protected Authorizer retrieveAuthorizer(final String authorizerName, final Map<String, Authorizer> authorizersMap) {
        Authorizer authorizer = null;
        for (val entry : authorizersMap.entrySet()) {
            if (areEqualsIgnoreCaseAndTrim(entry.getKey(), authorizerName)) {
                authorizer = entry.getValue();
                break;
            }
        }
        if (authorizer == null) {
            if (DefaultAuthorizers.CSRF_CHECK.equalsIgnoreCase(authorizerName)) {
                return CSRF_AUTHORIZER;
            } else if (DefaultAuthorizers.IS_ANONYMOUS.equalsIgnoreCase(authorizerName)) {
                return IS_ANONYMOUS_AUTHORIZER;
            } else if (DefaultAuthorizers.IS_AUTHENTICATED.equalsIgnoreCase(authorizerName)) {
                return IS_AUTHENTICATED_AUTHORIZER;
            } else if (DefaultAuthorizers.IS_FULLY_AUTHENTICATED.equalsIgnoreCase(authorizerName)) {
                return IS_FULLY_AUTHENTICATED_AUTHORIZER;
            } else if (DefaultAuthorizers.IS_REMEMBERED.equalsIgnoreCase(authorizerName)) {
                return IS_REMEMBERED_AUTHORIZER;
            }
        }
        return authorizer;
    }

    /**
     * <p>containsClientType.</p>
     *
     * @param clients a {@link java.util.List} object
     * @param clazz a {@link java.lang.Class} object
     * @return a boolean
     */
    protected boolean containsClientType(final List<Client> clients, final Class<? extends Client> clazz) {
        for (val client : clients) {
            if (clazz.isAssignableFrom(client.getClass())) {
                return true;
            }
        }
        return false;
    }

    /**
     * <p>isAuthorized.</p>
     *
     * @param context a {@link org.pac4j.core.context.WebContext} object
     * @param sessionStore a {@link org.pac4j.core.context.session.SessionStore} object
     * @param profiles a {@link java.util.List} object
     * @param authorizers a {@link java.util.List} object
     * @return a boolean
     */
    protected boolean isAuthorized(final WebContext context, final SessionStore sessionStore,
                                   final List<UserProfile> profiles, final List<Authorizer> authorizers) {
        // authorizations check comes after authentication and profile must not be null nor empty
        assertTrue(isNotEmpty(profiles), "profiles must not be null or empty");
        if (isNotEmpty(authorizers)) {
            // check authorizations using authorizers: all must be satisfied
            for (var authorizer : authorizers) {
                val isAuthorized = authorizer.isAuthorized(context, sessionStore, profiles);
                LOGGER.debug("Checking authorizer: {} -> {}", authorizer, isAuthorized);
                if (!isAuthorized) {
                    return false;
                }
            }
        }
        return true;
    }
}
