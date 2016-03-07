package org.pac4j.core.matching;

import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Default way to check the matching.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public class DefaultMatchingChecker implements MatchingChecker {

    @Override
    public boolean matches(final WebContext context, final String matcherName, final Map<String, Matcher> matchersMap) {
        // if we have a matcher name (which may be a list of matchers names)
        if (CommonHelper.isNotBlank(matcherName)) {
            final List<Matcher> matchers = new ArrayList<>();
            // we must have matchers
            CommonHelper.assertNotNull("matchersMap", matchersMap);
            final String[] names = matcherName.split(Pac4jConstants.ELEMENT_SEPRATOR);
            final int nb = names.length;
            for (int i = 0; i < nb; i++) {
                final String name = names[i];
                final Matcher result = matchersMap.get(name);
                // we must have an matcher defined for this name
                CommonHelper.assertNotNull("matchersMap['" + name + "']", result);
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
}
