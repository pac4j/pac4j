package org.pac4j.core.authorization.checker;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.authorization.authorizer.*;
import org.pac4j.core.authorization.authorizer.csrf.CsrfAuthorizer;
import org.pac4j.core.authorization.authorizer.csrf.CsrfTokenGeneratorAuthorizer;
import org.pac4j.core.authorization.authorizer.csrf.DefaultCsrfTokenGenerator;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;

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
public class DefaultAuthorizationChecker implements AuthorizationChecker {

    final static StrictTransportSecurityHeader STRICT_TRANSPORT_SECURITY_HEADER = new StrictTransportSecurityHeader();
    final static XContentTypeOptionsHeader X_CONTENT_TYPE_OPTIONS_HEADER = new XContentTypeOptionsHeader();
    final static XFrameOptionsHeader X_FRAME_OPTIONS_HEADER = new XFrameOptionsHeader();
    final static XSSProtectionHeader XSS_PROTECTION_HEADER = new XSSProtectionHeader();
    final static CacheControlHeader CACHE_CONTROL_HEADER = new CacheControlHeader();
    final static CsrfAuthorizer CSRF_AUTHORIZER = new CsrfAuthorizer();
    final static CsrfTokenGeneratorAuthorizer CSRF_TOKEN_GENERATOR_AUTHORIZER = new CsrfTokenGeneratorAuthorizer(new DefaultCsrfTokenGenerator());

    @Override
    public boolean isAuthorized(final WebContext context, final List<UserProfile> profiles, final String authorizerNames, final Map<String, Authorizer> authorizersMap) {
        final List<Authorizer> authorizers = new ArrayList<>();
        // if we have an authorizer name (which may be a list of authorizer names)
        if (isNotBlank(authorizerNames)) {
            final String[] names = authorizerNames.split(Pac4jConstants.ELEMENT_SEPRATOR);
            final int nb = names.length;
            for (int i = 0; i < nb; i++) {
                final String name = names[i].trim();
                if ("hsts".equalsIgnoreCase(name)) {
                    authorizers.add(STRICT_TRANSPORT_SECURITY_HEADER);
                } else if ("nosniff".equalsIgnoreCase(name)) {
                    authorizers.add(X_CONTENT_TYPE_OPTIONS_HEADER);
                } else if ("noframe".equalsIgnoreCase(name)) {
                    authorizers.add(X_FRAME_OPTIONS_HEADER);
                } else if ("xssprotection".equalsIgnoreCase(name)) {
                    authorizers.add(XSS_PROTECTION_HEADER);
                } else if ("nocache".equalsIgnoreCase(name)) {
                    authorizers.add(CACHE_CONTROL_HEADER);
                } else if ("securityheaders".equalsIgnoreCase(name)) {
                    authorizers.add(CACHE_CONTROL_HEADER);
                    authorizers.add(X_CONTENT_TYPE_OPTIONS_HEADER);
                    authorizers.add(STRICT_TRANSPORT_SECURITY_HEADER);
                    authorizers.add(X_FRAME_OPTIONS_HEADER);
                    authorizers.add(XSS_PROTECTION_HEADER);
                } else if ("csrfToken".equalsIgnoreCase(name)) {
                    authorizers.add(CSRF_TOKEN_GENERATOR_AUTHORIZER);
                } else if ("csrfCheck".equalsIgnoreCase(name)) {
                    authorizers.add(CSRF_AUTHORIZER);
                } else if ("csrf".equalsIgnoreCase(name)) {
                    authorizers.add(CSRF_TOKEN_GENERATOR_AUTHORIZER);
                    authorizers.add(CSRF_AUTHORIZER);
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

    @Override
    public boolean isAuthorized(final WebContext context, final List<UserProfile> profiles, final List<Authorizer> authorizers) {
        // authorizations check comes after authentication and profile must not be null nor empty
        assertTrue(isNotEmpty(profiles), "profiles must not be null or empty");
        if (isNotEmpty(authorizers)) {
            // check authorizations using authorizers: all must be satisfied
            for (Authorizer authorizer : authorizers) {
                if (!authorizer.isAuthorized(context, profiles)) {
                    return false;
                }
            }
        }
        return true;
    }
}
