package org.pac4j.core.matching.checker;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.matching.matcher.*;
import org.pac4j.core.matching.matcher.csrf.CsrfTokenGeneratorMatcher;
import org.pac4j.core.matching.matcher.csrf.DefaultCsrfTokenGenerator;
import org.pac4j.core.util.Pac4jConstants;

import java.util.*;

import static org.pac4j.core.util.CommonHelper.*;

/**
 * Default way to check the matchers (with default matchers).
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
@Slf4j
public class DefaultMatchingChecker implements MatchingChecker {

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

        val matchers = computeMatchers(context, sessionStore, matchersValue, matchersMap, clients);
        return matches(context, sessionStore, matchers);
    }

    protected List<Matcher> computeMatchers(final WebContext context, final SessionStore sessionStore, final String matchersValue,
                                            final Map<String, Matcher> matchersMap, final List<Client> clients) {
        String matcherNames;
        if (isBlank(matchersValue)) {
            matcherNames = computeDefaultMatcherNames(context, sessionStore, clients, matchersMap);
        } else if (matchersValue.trim().startsWith(Pac4jConstants.ADD_ELEMENT)) {
            matcherNames = computeDefaultMatcherNames(context, sessionStore, clients, matchersMap) +
                Pac4jConstants.ELEMENT_SEPARATOR + substringAfter(matchersValue, Pac4jConstants.ADD_ELEMENT);
        } else {
            matcherNames = matchersValue;
        }
        return computeMatchersFromNames(matcherNames, matchersMap);
    }

    protected String computeDefaultMatcherNames(final WebContext context, final SessionStore sessionStore, final List<Client> clients,
                                                final Map<String, Matcher> matchersMap) {
        String name = DefaultMatchers.SECURITYHEADERS;
        if (sessionStore.getSessionId(context, false).isPresent()) {
            name += Pac4jConstants.ELEMENT_SEPARATOR + DefaultMatchers.CSRF_TOKEN;
            return name;
        }
        for (val client : clients) {
            if (client instanceof IndirectClient) {
                name += Pac4jConstants.ELEMENT_SEPARATOR + DefaultMatchers.CSRF_TOKEN;
                return name;
            }
        }
        return name;
    }

    protected List<Matcher> computeMatchersFromNames(final String matchersValue, final Map<String, Matcher> matchersMap) {
        assertNotNull("matchersMap", matchersMap);
        final List<Matcher> matchers = new ArrayList<>();
        final List<String> names = new ArrayList<>(Arrays.asList(matchersValue.split(Pac4jConstants.ELEMENT_SEPARATOR)));
        for (var i = 0; i < names.size(); ) {
            val name = names.get(i++).trim();
            if (DefaultMatchers.SECURITYHEADERS.equalsIgnoreCase(name)) {
                val results = retrieveMatchers(name, matchersMap);
                // the securityheaders shortcut has not been overriden, replace it by its associated matchers
                if (results.isEmpty()) {
                    names.add(i, DefaultMatchers.XSSPROTECTION);
                    names.add(i, DefaultMatchers.NOFRAME);
                    names.add(i, DefaultMatchers.HSTS);
                    names.add(i, DefaultMatchers.NOSNIFF);
                    names.add(i, DefaultMatchers.NOCACHE);
                } else {
                    matchers.addAll(results);
                }
            } else if (!DefaultMatchers.NONE.equalsIgnoreCase(name)) {
                val results = retrieveMatchers(name, matchersMap);
                // we must have matchers defined for this name
                assertTrue(!results.isEmpty(),
                    "The matcher '" + name + "' must be defined in the security configuration");
                matchers.addAll(results);
            }
        }
        return matchers;
    }

    protected List<Matcher> retrieveMatchers(final String matcherName, final Map<String, Matcher> matchersMap) {
        final List<Matcher> results = new ArrayList<>();
        for (val entry : matchersMap.entrySet()) {
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
            for (val matcher : matchers) {
                val matches = matcher.matches(context, sessionStore);
                LOGGER.debug("Checking matcher: {} -> {}", matcher, matches);
                if (!matches) {
                    return false;
                }
            }
        }
        return true;
    }
}
