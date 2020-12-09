package org.pac4j.core.matching.checker;

import org.pac4j.core.client.Client;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.matching.matcher.*;
import org.pac4j.core.matching.matcher.csrf.CsrfTokenGeneratorMatcher;
import org.pac4j.core.matching.matcher.csrf.DefaultCsrfTokenGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.pac4j.core.util.CommonHelper.*;

/**
 * Default way to check the matchers (with default matchers).
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public class DefaultMatchingChecker implements MatchingChecker {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMatchingChecker.class);

    protected static final Matcher GET_MATCHER = new HttpMethodMatcher(HttpConstants.HTTP_METHOD.GET);
    protected static final Matcher POST_MATCHER = new HttpMethodMatcher(HttpConstants.HTTP_METHOD.POST);
    protected static final Matcher PUT_MATCHER = new HttpMethodMatcher(HttpConstants.HTTP_METHOD.PUT);
    protected static final Matcher DELETE_MATCHER = new HttpMethodMatcher(HttpConstants.HTTP_METHOD.DELETE);

    protected static final StrictTransportSecurityMatcher STRICT_TRANSPORT_MATCHER = new StrictTransportSecurityMatcher();
    protected static final XContentTypeOptionsMatcher X_CONTENT_TYPE_OPTIONS_MATCHER = new XContentTypeOptionsMatcher();
    protected static final XFrameOptionsMatcher X_FRAME_OPTIONS_MATCHER = new XFrameOptionsMatcher();
    protected static final XSSProtectionMatcher XSS_PROTECTION_MATCHER = new XSSProtectionMatcher();
    protected static final CacheControlMatcher CACHE_CONTROL_MATCHER = new CacheControlMatcher();
    protected static final CsrfTokenGeneratorMatcher CSRF_TOKEN_MATCHER = new CsrfTokenGeneratorMatcher(new DefaultCsrfTokenGenerator());
    static final List<Matcher> SECURITY_HEADERS_MATCHERS = Arrays.asList(CACHE_CONTROL_MATCHER, X_CONTENT_TYPE_OPTIONS_MATCHER,
        STRICT_TRANSPORT_MATCHER, X_FRAME_OPTIONS_MATCHER, XSS_PROTECTION_MATCHER);
    protected static final CorsMatcher CORS_MATCHER = new CorsMatcher();

    static {
        CORS_MATCHER.setAllowOrigin("*");
        CORS_MATCHER.setAllowCredentials(true);
        final Set<HttpConstants.HTTP_METHOD> methods = new HashSet<>();
        methods.add(HttpConstants.HTTP_METHOD.GET);
        methods.add(HttpConstants.HTTP_METHOD.PUT);
        methods.add(HttpConstants.HTTP_METHOD.POST);
        methods.add(HttpConstants.HTTP_METHOD.DELETE);
        methods.add(HttpConstants.HTTP_METHOD.OPTIONS);
        CORS_MATCHER.setAllowMethods(methods);
    }

    @Override
    public boolean matches(final WebContext context, final String matchersValue, final Map<String, Matcher> matchersMap,
                           final List<Client> clients) {

        final List<Matcher> matchers = computeMatchers(context, matchersValue, matchersMap, clients);
        return matches(context, matchers);
    }

    protected List<Matcher> computeMatchers(final WebContext context, final String matchersValue, final Map<String, Matcher> matchersMap,
                                            final List<Client> clients) {
        final List<Matcher> matchers;
        if (isBlank(matchersValue)) {
            matchers = computeDefaultMatchers(context, clients);
        } else {
            if (matchersValue.trim().startsWith(Pac4jConstants.ADD_ELEMENT)) {
                final String matcherNames = substringAfter(matchersValue, Pac4jConstants.ADD_ELEMENT);
                matchers = computeDefaultMatchers(context, clients);
                matchers.addAll(computeMatchersFromNames(matcherNames, matchersMap));
            } else {
                matchers = computeMatchersFromNames(matchersValue, matchersMap);
            }
        }
        return matchers;
    }

    protected List<Matcher> computeDefaultMatchers(final WebContext context, final List<Client> clients) {
        final List<Matcher> matchers = new ArrayList<>();
        matchers.addAll(SECURITY_HEADERS_MATCHERS);
        if (context.getSessionStore().getSessionId(context, false).isPresent()) {
            matchers.add(CSRF_TOKEN_MATCHER);
            return matchers;
        }
        for (final Client client : clients) {
            if (client instanceof IndirectClient) {
                matchers.add(CSRF_TOKEN_MATCHER);
                return matchers;
            }
        }
        return matchers;
    }

    protected List<Matcher> computeMatchersFromNames(final String matchersValue, final Map<String, Matcher> matchersMap) {
        assertNotNull("matchersMap", matchersMap);
        final List<Matcher> matchers = new ArrayList<>();
        final String[] names = matchersValue.split(Pac4jConstants.ELEMENT_SEPARATOR);
        final int nb = names.length;
        for (int i = 0; i < nb; i++) {
            final String name = names[i].trim();
            if (!DefaultMatchers.NONE.equalsIgnoreCase(name)) {
                final List<Matcher> results = retrieveMatchers(name, matchersMap);
                // we must have matchers defined for this name
                assertTrue(results != null && results.size() > 0, "The matcher '" + name + "' must exist");
                matchers.addAll(results);
            }
        }
        return matchers;
    }

    protected List<Matcher> retrieveMatchers(final String matcherName, final Map<String, Matcher> matchersMap) {
        final List<Matcher> results = new ArrayList<>();
        for (final Map.Entry<String, Matcher> entry : matchersMap.entrySet()) {
            if (areEqualsIgnoreCaseAndTrim(entry.getKey(), matcherName)) {
                results.add(entry.getValue());
                break;
            }
        }
        if (results.size() == 0) {
            if (DefaultMatchers.HSTS.equalsIgnoreCase(matcherName)) {
                return Arrays.asList(STRICT_TRANSPORT_MATCHER);
            } else if (DefaultMatchers.NOSNIFF.equalsIgnoreCase(matcherName)) {
                return Arrays.asList(X_CONTENT_TYPE_OPTIONS_MATCHER);
            } else if (DefaultMatchers.NOFRAME.equalsIgnoreCase(matcherName)) {
                return Arrays.asList(X_FRAME_OPTIONS_MATCHER);
            } else if (DefaultMatchers.XSSPROTECTION.equalsIgnoreCase(matcherName)) {
                return Arrays.asList(XSS_PROTECTION_MATCHER);
            } else if (DefaultMatchers.NOCACHE.equalsIgnoreCase(matcherName)) {
                return Arrays.asList(CACHE_CONTROL_MATCHER);
            } else if (DefaultMatchers.SECURITYHEADERS.equalsIgnoreCase(matcherName)) {
                return SECURITY_HEADERS_MATCHERS;
            } else if (DefaultMatchers.CSRF_TOKEN.equalsIgnoreCase(matcherName)) {
                return Arrays.asList(CSRF_TOKEN_MATCHER);
            } else if (DefaultMatchers.ALLOW_AJAX_REQUESTS.equalsIgnoreCase(matcherName)) {
                return Arrays.asList(CORS_MATCHER);
            } else if (DefaultMatchers.GET.equalsIgnoreCase(matcherName)) {
                return Arrays.asList(GET_MATCHER);
            } else if (DefaultMatchers.POST.equalsIgnoreCase(matcherName)) {
                return Arrays.asList(POST_MATCHER);
            } else if (DefaultMatchers.PUT.equalsIgnoreCase(matcherName)) {
                return Arrays.asList(PUT_MATCHER);
            } else if (DefaultMatchers.DELETE.equalsIgnoreCase(matcherName)) {
                return Arrays.asList(DELETE_MATCHER);
            }
        }
        return results;
    }


    protected boolean matches(final WebContext context, final List<Matcher> matchers) {
        if (!matchers.isEmpty()) {
            // check matching using matchers: all must be satisfied
            for (final Matcher matcher : matchers) {
                final boolean matches = matcher.matches(context);
                LOGGER.debug("Checking matcher: {} -> {}", matcher, matches);
                if (!matches) {
                    return false;
                }
            }
        }
        return true;
    }
}
