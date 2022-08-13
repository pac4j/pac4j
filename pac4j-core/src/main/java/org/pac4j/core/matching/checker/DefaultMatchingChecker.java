package org.pac4j.core.matching.checker;

import static org.pac4j.core.util.CommonHelper.areEqualsIgnoreCaseAndTrim;
import static org.pac4j.core.util.CommonHelper.assertNotNull;
import static org.pac4j.core.util.CommonHelper.assertTrue;
import static org.pac4j.core.util.CommonHelper.isBlank;
import static org.pac4j.core.util.CommonHelper.substringAfter;
import static org.pac4j.core.util.CommonHelper.substringBetween;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pac4j.core.client.Client;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.matching.matcher.CacheControlMatcher;
import org.pac4j.core.matching.matcher.CorsMatcher;
import org.pac4j.core.matching.matcher.DefaultMatchers;
import org.pac4j.core.matching.matcher.HttpMethodMatcher;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.matching.matcher.StrictTransportSecurityMatcher;
import org.pac4j.core.matching.matcher.XContentTypeOptionsMatcher;
import org.pac4j.core.matching.matcher.XFrameOptionsMatcher;
import org.pac4j.core.matching.matcher.XSSProtectionMatcher;
import org.pac4j.core.matching.matcher.csrf.CsrfTokenGeneratorMatcher;
import org.pac4j.core.matching.matcher.csrf.DefaultCsrfTokenGenerator;
import org.pac4j.core.util.Pac4jConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public boolean matches(final WebContext context, final SessionStore sessionStore, final String matchersValue,
                           final Map<String, Matcher> matchersMap, final List<Client> clients) {

        final var matchers = computeMatchers(context, sessionStore, matchersValue, matchersMap, clients);
        return matches(context, sessionStore, matchers);
    }

    protected List<Matcher> computeMatchers(final WebContext context, final SessionStore sessionStore, final String matchersValue,
                                            final Map<String, Matcher> matchersMap, final List<Client> clients) {
        final List<Matcher> matchers;
        if (isBlank(matchersValue)) {
            matchers = computeDefaultMatchers(context, sessionStore, clients);
        } else {
            if (matchersValue.trim().startsWith(Pac4jConstants.ADD_ELEMENT) || matchersValue.trim().startsWith(Pac4jConstants.REMOVE_ELEMENT)) {
                matchers = computeDefaultMatchers(context, sessionStore, clients);
                
                final var removedMatcherNames = substringAfter(matchersValue, Pac4jConstants.REMOVE_ELEMENT);
				removeMatchersFromNames(removedMatcherNames, matchers);
                
                
                final var addedMatcherNames = substringBetween(matchersValue, Pac4jConstants.ADD_ELEMENT, Pac4jConstants.REMOVE_ELEMENT);
                matchers.addAll(addMatchersFromNames(addedMatcherNames, matchersMap));
                
            } else {
                matchers = addMatchersFromNames(matchersValue, matchersMap);
            }
        }
        return matchers;
    }

    protected List<Matcher> computeDefaultMatchers(final WebContext context, final SessionStore sessionStore, final List<Client> clients) {
        final List<Matcher> matchers = new ArrayList<>();
        matchers.addAll(SECURITY_HEADERS_MATCHERS);
        if (sessionStore.getSessionId(context, false).isPresent()) {
            matchers.add(CSRF_TOKEN_MATCHER);
            return matchers;
        }
        for (final var client : clients) {
            if (client instanceof IndirectClient) {
                matchers.add(CSRF_TOKEN_MATCHER);
                return matchers;
            }
        }
        return matchers;
    }

    protected List<Matcher> addMatchersFromNames(final String matchersValue, final Map<String, Matcher> matchersMap) {
        assertNotNull("matchersMap", matchersMap);
        final List<Matcher> matchers = new ArrayList<>();
        final var names = matchersValue.split(Pac4jConstants.ELEMENT_SEPARATOR);
        final var nb = names.length;
        for (var i = 0; i < nb; i++) {
            final var name = names[i].trim();
            if (!DefaultMatchers.NONE.equalsIgnoreCase(name)) {
                final var results = retrieveMatchers(name, matchersMap);
                // we must have matchers defined for this name
                assertTrue(results != null && results.size() > 0,
                    "The matcher '" + name + "' must be defined in the security configuration");
                matchers.addAll(results);
            }
        }
        return matchers;
    }

	protected List<Matcher> removeMatchersFromNames(final String matchersValue, final List<Matcher> matchers) {
		final var names = matchersValue.split(Pac4jConstants.ELEMENT_SEPARATOR);
		final var nb = names.length;
		for (var i = 0; i < nb; i++) {
			final var name = names[i].trim();
			if (!DefaultMatchers.NONE.equalsIgnoreCase(name)) {
				final var results = retrieveMatchers(name, Collections.emptyMap());
				// we must have matchers defined for this name
				assertTrue(results != null && results.size() > 0,
						"The matcher '" + name + "' must be defined in the security configuration");
				matchers.removeAll(results);
			}
		}
		return matchers;
	}

    protected List<Matcher> retrieveMatchers(final String matcherName, final Map<String, Matcher> matchersMap) {
        final List<Matcher> results = new ArrayList<>();
        for (final var entry : matchersMap.entrySet()) {
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


    protected boolean matches(final WebContext context, final SessionStore sessionStore, final List<Matcher> matchers) {
        if (!matchers.isEmpty()) {
            // check matching using matchers: all must be satisfied
            for (final var matcher : matchers) {
                final var matches = matcher.matches(context, sessionStore);
                LOGGER.debug("Checking matcher: {} -> {}", matcher, matches);
                if (!matches) {
                    return false;
                }
            }
        }
        return true;
    }
}
