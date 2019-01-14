package org.pac4j.core.authorization.checker;

import org.pac4j.core.authorization.authorizer.*;
import org.pac4j.core.authorization.authorizer.csrf.*;
import org.pac4j.core.context.DefaultAuthorizers;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.pac4j.core.util.CommonHelper.*;

import static org.pac4j.core.context.HttpConstants.*;

/**
 * Default way to check the authorizations (with default authorizers).
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class DefaultAuthorizationChecker implements AuthorizationChecker {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAuthorizationChecker.class);

    final static StrictTransportSecurityHeader STRICT_TRANSPORT_SECURITY_HEADER = new StrictTransportSecurityHeader();
    final static XContentTypeOptionsHeader X_CONTENT_TYPE_OPTIONS_HEADER = new XContentTypeOptionsHeader();
    final static XFrameOptionsHeader X_FRAME_OPTIONS_HEADER = new XFrameOptionsHeader();
    final static XSSProtectionHeader XSS_PROTECTION_HEADER = new XSSProtectionHeader();
    final static CacheControlHeader CACHE_CONTROL_HEADER = new CacheControlHeader();
    final static CsrfAuthorizer CSRF_AUTHORIZER = new CsrfAuthorizer();
    final static CsrfTokenGeneratorAuthorizer CSRF_TOKEN_GENERATOR_AUTHORIZER
        = new CsrfTokenGeneratorAuthorizer(new DefaultCsrfTokenGenerator());
    final static CorsAuthorizer CORS_AUTHORIZER = new CorsAuthorizer();
    final static IsAnonymousAuthorizer IS_ANONYMOUS_AUTHORIZER = new IsAnonymousAuthorizer();
    final static IsAuthenticatedAuthorizer IS_AUTHENTICATED_AUTHORIZER =new IsAuthenticatedAuthorizer();
    final static IsFullyAuthenticatedAuthorizer IS_FULLY_AUTHENTICATED_AUTHORIZER = new IsFullyAuthenticatedAuthorizer();
    final static IsRememberedAuthorizer IS_REMEMBERED_AUTHORIZER = new IsRememberedAuthorizer();

    static {
        CORS_AUTHORIZER.setAllowOrigin("*");
        CORS_AUTHORIZER.setAllowCredentials(true);
        final Set<HTTP_METHOD> methods = new HashSet<>();
        methods.add(HTTP_METHOD.GET);
        methods.add(HTTP_METHOD.PUT);
        methods.add(HTTP_METHOD.POST);
        methods.add(HTTP_METHOD.DELETE);
        methods.add(HTTP_METHOD.OPTIONS);
        CORS_AUTHORIZER.setAllowMethods(methods);
    }

    @Override
    public boolean isAuthorized(final WebContext context, final List<UserProfile> profiles, final String authorizersValue,
                                final Map<String, Authorizer> authorizersMap) {
        final List<Authorizer> authorizers = new ArrayList<>();
        String authorizerNames = authorizersValue;
        // the authorizers are not defined, we default to:
        if (authorizerNames == null) {
            authorizerNames = DefaultAuthorizers.CSRF + Pac4jConstants.ELEMENT_SEPARATOR + DefaultAuthorizers.SECURITYHEADERS;
        }

        // if we have an authorizer name (which may be a list of authorizer names)
        if (isNotBlank(authorizerNames)) {
            final String[] names = authorizerNames.split(Pac4jConstants.ELEMENT_SEPARATOR);
            final int nb = names.length;
            for (int i = 0; i < nb; i++) {
                final String name = names[i].trim();
                if (DefaultAuthorizers.HSTS.equalsIgnoreCase(name)) {
                    authorizers.add(STRICT_TRANSPORT_SECURITY_HEADER);
                } else if (DefaultAuthorizers.NOSNIFF.equalsIgnoreCase(name)) {
                    authorizers.add(X_CONTENT_TYPE_OPTIONS_HEADER);
                } else if (DefaultAuthorizers.NOFRAME.equalsIgnoreCase(name)) {
                    authorizers.add(X_FRAME_OPTIONS_HEADER);
                } else if (DefaultAuthorizers.XSSPROTECTION.equalsIgnoreCase(name)) {
                    authorizers.add(XSS_PROTECTION_HEADER);
                } else if (DefaultAuthorizers.NOCACHE.equalsIgnoreCase(name)) {
                    authorizers.add(CACHE_CONTROL_HEADER);
                } else if (DefaultAuthorizers.SECURITYHEADERS.equalsIgnoreCase(name)) {
                    authorizers.add(CACHE_CONTROL_HEADER);
                    authorizers.add(X_CONTENT_TYPE_OPTIONS_HEADER);
                    authorizers.add(STRICT_TRANSPORT_SECURITY_HEADER);
                    authorizers.add(X_FRAME_OPTIONS_HEADER);
                    authorizers.add(XSS_PROTECTION_HEADER);
                } else if (DefaultAuthorizers.CSRF_TOKEN.equalsIgnoreCase(name)) {
                    authorizers.add(CSRF_TOKEN_GENERATOR_AUTHORIZER);
                } else if (DefaultAuthorizers.CSRF_CHECK.equalsIgnoreCase(name)) {
                    authorizers.add(CSRF_AUTHORIZER);
                } else if (DefaultAuthorizers.CSRF.equalsIgnoreCase(name)) {
                    authorizers.add(CSRF_TOKEN_GENERATOR_AUTHORIZER);
                    authorizers.add(CSRF_AUTHORIZER);
                } else if (DefaultAuthorizers.ALLOW_AJAX_REQUESTS.equalsIgnoreCase(name)) {
                    authorizers.add(CORS_AUTHORIZER);
                } else if (DefaultAuthorizers.IS_ANONYMOUS.equalsIgnoreCase(name)) {
                    authorizers.add(IS_ANONYMOUS_AUTHORIZER);
                } else if (DefaultAuthorizers.IS_AUTHENTICATED.equalsIgnoreCase(name)) {
                    authorizers.add(IS_AUTHENTICATED_AUTHORIZER);
                } else if (DefaultAuthorizers.IS_FULLY_AUTHENTICATED.equalsIgnoreCase(name)) {
                    authorizers.add(IS_FULLY_AUTHENTICATED_AUTHORIZER);
                } else if (DefaultAuthorizers.IS_REMEMBERED.equalsIgnoreCase(name)) {
                    authorizers.add(IS_REMEMBERED_AUTHORIZER);
                } else {
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
        }
        return isAuthorized(context, profiles, authorizers);
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
