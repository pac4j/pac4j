package org.pac4j.core.matching;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Matches all request paths except whitelisted (excluded) paths.
 *
 * @Author Rob Ward
 * @since 2.0.0
 */
public class PathMatcher implements Matcher {
    private final Set<String> excludedPaths = new HashSet<>();
    private final Set<Pattern> excludedPatterns = new HashSet<>();

    /**
     * Any path exactly matching this string will be excluded. Use this method if you are excluding a specific path.
     */
    public void addExcludedPath(final String path) {
        excludedPaths.add(path);
    }

    /**
     * Convenience method for excluding all paths starting with a prefix e.g. "/foo" would exclude "/foo", "/foo/bar", etc.
     */
    public void addExcludedBranch(final String root) {
        addExcludedRegex("^" + root + "(/.*)?$");
    }

    /**
     * Any path matching this regex will be excluded.
     */
    public void addExcludedRegex(final String regex) {
        excludedPatterns.add(Pattern.compile(regex));
    }

    @Override
    public boolean matches(final WebContext context) throws HttpAction {
        return matches(context.getPath());
    }

    // Returns true if a path should be authenticated, false to skip authentication.
    boolean matches(final String path) {

        if (excludedPaths.contains(path)) {
            return false;
        }

        for (Pattern pattern : excludedPatterns) {
            if (pattern.matcher(path).matches()) {
                return false;
            }
        }

        return true;
    }
}