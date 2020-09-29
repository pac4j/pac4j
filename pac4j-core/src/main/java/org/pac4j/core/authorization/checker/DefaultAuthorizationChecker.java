package org.pac4j.core.authorization.checker;

import org.pac4j.core.authorization.authorizer.*;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.pac4j.core.util.CommonHelper.*;

/**
 * Default way to check the authorizations (with default authorizers).
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class DefaultAuthorizationChecker implements AuthorizationChecker {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAuthorizationChecker.class);

    static final CsrfAuthorizer CSRF_AUTHORIZER = new CsrfAuthorizer();
    static final IsAnonymousAuthorizer IS_ANONYMOUS_AUTHORIZER = new IsAnonymousAuthorizer();
    static final IsAuthenticatedAuthorizer IS_AUTHENTICATED_AUTHORIZER =new IsAuthenticatedAuthorizer();
    static final IsFullyAuthenticatedAuthorizer IS_FULLY_AUTHENTICATED_AUTHORIZER = new IsFullyAuthenticatedAuthorizer();
    static final IsRememberedAuthorizer IS_REMEMBERED_AUTHORIZER = new IsRememberedAuthorizer();

    @Override
    public boolean isAuthorized(final WebContext context, final List<UserProfile> profiles, final String authorizersValue,
                                final Map<String, Authorizer> authorizersMap, final List<Client> clients) {
        final List<Authorizer> authorizers = new ArrayList<>();
        String authorizerNames = authorizersValue;
        // if no authorizers are defined, compute the default one(s)
        if (isBlank(authorizerNames)) {
            authorizerNames = computeDefaultAuthorizers(clients);
        }
        final String[] names = authorizerNames.split(Pac4jConstants.ELEMENT_SEPARATOR);
        final int nb = names.length;
        for (int i = 0; i < nb; i++) {
            final String name = names[i].trim();
            if (DefaultAuthorizers.CSRF_CHECK.equalsIgnoreCase(name)) {
                authorizers.add(CSRF_AUTHORIZER);
            } else if (DefaultAuthorizers.IS_ANONYMOUS.equalsIgnoreCase(name)) {
                authorizers.add(IS_ANONYMOUS_AUTHORIZER);
            } else if (DefaultAuthorizers.IS_AUTHENTICATED.equalsIgnoreCase(name)) {
                authorizers.add(IS_AUTHENTICATED_AUTHORIZER);
            } else if (DefaultAuthorizers.IS_FULLY_AUTHENTICATED.equalsIgnoreCase(name)) {
                authorizers.add(IS_FULLY_AUTHENTICATED_AUTHORIZER);
            } else if (DefaultAuthorizers.IS_REMEMBERED.equalsIgnoreCase(name)) {
                authorizers.add(IS_REMEMBERED_AUTHORIZER);
            // we don't add any authorizer for none
            } else if (!DefaultAuthorizers.NONE.equalsIgnoreCase(name)){
                // we must have authorizers
                assertNotNull("authorizersMap", authorizersMap);
                Authorizer result = null;
                for (final Map.Entry<String, Authorizer> entry : authorizersMap.entrySet()) {
                    if (areEqualsIgnoreCaseAndTrim(entry.getKey(), name)) {
                        result = entry.getValue();
                        break;
                    }
                }
                // we must have an authorizer defined for this name
                assertNotNull("authorizersMap['" + name + "']", result);
                authorizers.add(result);
            }
        }
        return isAuthorized(context, profiles, authorizers);
    }

    protected String computeDefaultAuthorizers(final List<Client> clients) {
        for (final Client client : clients) {
            if (client instanceof IndirectClient) {
                return DefaultAuthorizers.CSRF_CHECK;
            }
        }
        return DefaultAuthorizers.NONE;
    }

    protected boolean isAuthorized(final WebContext context, final List<UserProfile> profiles, final List<Authorizer> authorizers) {
        // authorizations check comes after authentication and profile must not be null nor empty
        assertTrue(isNotEmpty(profiles), "profiles must not be null or empty");
        if (isNotEmpty(authorizers)) {
            // check authorizations using authorizers: all must be satisfied
            for (Authorizer authorizer : authorizers) {
                final boolean isAuthorized = authorizer.isAuthorized(context, profiles);
                LOGGER.debug("Checking authorizer: {} -> {}", authorizer, isAuthorized);
                if (!isAuthorized) {
                    return false;
                }
            }
        }
        return true;
    }
}
