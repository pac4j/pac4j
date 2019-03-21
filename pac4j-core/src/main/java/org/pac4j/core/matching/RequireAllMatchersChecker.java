package org.pac4j.core.matching;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Check that all matchers are satisfied.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public class RequireAllMatchersChecker implements MatchingChecker {

    private static final String GET_MATCHER_NAME = "get";
    private static final Matcher GET_MATCHER = new HttpMethodMatcher(HttpConstants.HTTP_METHOD.GET);
    private static final String POST_MATCHER_NAME = "post";
    private static final Matcher POST_MATCHER = new HttpMethodMatcher(HttpConstants.HTTP_METHOD.POST);
    private static final String PUT_MATCHER_NAME = "put";
    private static final Matcher PUT_MATCHER = new HttpMethodMatcher(HttpConstants.HTTP_METHOD.PUT);
    private static final String DELETE_MATCHER_NAME = "delete";
    private static final Matcher DELETE_MATCHER = new HttpMethodMatcher(HttpConstants.HTTP_METHOD.DELETE);

    @Override
    public boolean matches(final WebContext context, final String matcherNames, final Map<String, Matcher> matchersMap) {
        // if we have a matcher name (which may be a list of matchers names)
        if (CommonHelper.isNotBlank(matcherNames)) {
            final List<Matcher> matchers = new ArrayList<>();
            // we must have matchers
            CommonHelper.assertNotNull("matchersMap", matchersMap);
            final Map<String, Matcher> allMatchers = buildAllMatchers(matchersMap);
            final String[] names = matcherNames.split(Pac4jConstants.ELEMENT_SEPARATOR);
            final int nb = names.length;
            for (int i = 0; i < nb; i++) {
                final String name = names[i];
                Matcher result = null;
                for (final Map.Entry<String, Matcher> entry : allMatchers.entrySet()) {
                    if (CommonHelper.areEqualsIgnoreCaseAndTrim(entry.getKey(), name)) {
                        result = entry.getValue();
                        break;
                    }
                }
                // we must have an matcher defined for this name
                CommonHelper.assertNotNull("allMatchers['" + name + "']", result);
                matchers.add(result);
            }
            if (!matchers.isEmpty()) {
                // check matching using matchers: all must be satisfied
                for (Matcher matcher : matchers) {
                    if (!matcher.matches(context)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private Map<String, Matcher> buildAllMatchers(final Map<String, Matcher> matchersMap) {
        final Map<String, Matcher> allMatchers = new HashMap<>();
        allMatchers.putAll(matchersMap);
        addDefaultMatcherIfNotDefined(allMatchers, GET_MATCHER_NAME, GET_MATCHER);
        addDefaultMatcherIfNotDefined(allMatchers, POST_MATCHER_NAME, POST_MATCHER);
        addDefaultMatcherIfNotDefined(allMatchers, PUT_MATCHER_NAME, PUT_MATCHER);
        addDefaultMatcherIfNotDefined(allMatchers, DELETE_MATCHER_NAME, DELETE_MATCHER);
        return allMatchers;
    }

    private void addDefaultMatcherIfNotDefined(final Map<String, Matcher> allMatchers, final String name, final Matcher matcher) {
        if (!allMatchers.containsKey(name)) {
            allMatchers.put(name, matcher);
        }
    }
}
