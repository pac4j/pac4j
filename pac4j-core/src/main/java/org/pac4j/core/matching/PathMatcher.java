package org.pac4j.core.matching;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final static Logger logger = LoggerFactory.getLogger(PathMatcher.class);
    private final Set<String> excludedPaths = new HashSet<>();
    private final Set<Pattern> excludedPatterns = new HashSet<>();

    /**
     * Any path exactly matching this string will be excluded. Use this method if you are excluding a specific path.
     *
     * @param path the path to be excluded
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
     */
    public PathMatcher excludeRegex(final String regex) {
        CommonHelper.assertNotBlank("regex", regex);
        logger.warn("Excluding paths with regexes is an advanced feature: be careful when defining your regular expression to avoid any security issues!");

        if (!regex.startsWith("^") || !regex.endsWith("$")) {
            final String msg = "Your regular expression: '" + regex + "' must start with a ^ and end with a $ to define a full path matching";
            logger.error(msg);
            throw new TechnicalException(msg);
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

    protected Set<String> getExcludedPaths() {
        return excludedPaths;
    }

    protected Set<Pattern> getExcludedPatterns() {
        return excludedPatterns;
    }

    private static void validatePath(String path) {
        CommonHelper.assertNotBlank("path", path);
        if (!path.startsWith("/")) {
            final String msg = "Excluded path must begin with a /";
            logger.error(msg);
            throw new TechnicalException(msg);
        }
    }
}