package org.pac4j.core.matching.matcher;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Matches all request paths except whitelisted (excluded) paths.
 *
 * @author Rob Ward
 * @since 2.0.0
 */
public class PathMatcher implements Matcher {
    private static final Logger logger = LoggerFactory.getLogger(PathMatcher.class);
    private final Set<String> excludedPaths = new HashSet<>();
    private final Set<Pattern> excludedPatterns = new HashSet<>();

    private static boolean warned;

    public PathMatcher() {}

    public PathMatcher(final String regexpPath) {
        excludeRegex(regexpPath);
    }

    /**
     * Any path exactly matching this string will be excluded. Use this method if you are excluding a specific path.
     *
     * @param path the path to be excluded
     * @return this path matcher
     */
    public PathMatcher excludePath(final String path) {
        validatePath(path);
        excludedPaths.add(path);
        return this;
    }

    /**
     * Convenience method for excluding all paths starting with a prefix e.g. "/foo" would exclude "/foo", "/foo/bar", etc.
     *
     * @param path the prefix for the paths to be excluded
     * @return this path matcher
     */
    public PathMatcher excludeBranch(final String path) {
        validatePath(path);
        excludedPatterns.add(Pattern.compile("^" + path + "(/.*)?$"));
        return this;
    }

    /**
     * Any path matching this regex will be excluded.
     *
     * @param regex the regular expression matching the paths to be excluded
     * @return this path matcher
     */
    public PathMatcher excludeRegex(final String regex) {
        CommonHelper.assertNotBlank("regex", regex);
        if (!warned) {
            logger.warn("Excluding paths with regexes is an advanced feature: be careful when defining your regular expression " +
                "to avoid any security issues!");
            warned = true;
        }

        if (!regex.startsWith("^") || !regex.endsWith("$")) {
            throw new TechnicalException("Your regular expression: '" + regex + "' must start with a ^ and end with a $ " +
                "to define a full path matching");
        }

        excludedPatterns.add(Pattern.compile(regex));
        return this;
    }

    @Override
    public boolean matches(final WebContext context) {
        return matches(context.getPath());
    }

    // Returns true if a path should be authenticated, false to skip authentication.
    boolean matches(final String path) {

        logger.debug("path to match: {}", path);

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

    public Set<String> getExcludedPaths() {
        return excludedPaths;
    }

    public Set<Pattern> getExcludedPatterns() {
        return excludedPatterns;
    }

    public void setExcludedPaths(Collection<String> paths) {
        excludedPaths.clear();
        paths.forEach(path -> excludePath(path));
    }

    public void setExcludedPatterns(Collection<String> regularExpressions) {
        excludedPatterns.clear();
        regularExpressions.forEach(regex -> excludeRegex(regex));
    }

    public void setExcludedPath(final String path) {
        excludedPaths.clear();
        excludePath(path);
    }

    public void setExcludedPattern(final String regularExpression) {
        excludedPatterns.clear();
        excludeRegex(regularExpression);
    }

    private static void validatePath(String path) {
        CommonHelper.assertNotBlank("path", path);
        if (!path.startsWith("/")) {
            throw new TechnicalException("Excluded path must begin with a /");
        }
    }
}
