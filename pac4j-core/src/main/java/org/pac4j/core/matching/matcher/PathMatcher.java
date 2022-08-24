package org.pac4j.core.matching.matcher;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
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
    private final Set<String> includedPaths = new HashSet<>();
    private final Set<String> excludedPaths = new HashSet<>();
    private final Set<Pattern> excludedPatterns = new HashSet<>();

    private static boolean warnedRegexp;
    private static boolean warnedInclude;

    public PathMatcher() {}

    @Deprecated
    public PathMatcher(final String regexpPath) {
        logger.warn("Use `new PathMatcher().excludeRegex(regex)` for clarity instead of `new PathMatcher(regex)`");
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

    public PathMatcher excludePaths(final String... paths) {
        if (paths != null && paths.length > 0) {
            for (final var path : paths) {
                excludePath(path);
            }
        }
        return this;
    }

    public PathMatcher includePath(final String path) {
        warnInclude();
        validatePath(path);
        includedPaths.add(path);
        return this;
    }

    public PathMatcher includePaths(final String... paths) {
        if (paths != null && paths.length > 0) {
            for (final var path : paths) {
                includePath(path);
            }
        }
        return this;
    }

    /**
     * Convenience method for excluding all paths starting with a prefix e.g. "/foo" would exclude "/foo", "/foo/bar", etc.
     *
     * @param path the prefix for the paths to be excluded
     * @return this path matcher
     */
    public PathMatcher excludeBranch(final String path) {
        warnRegexp();
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
        warnRegexp();
        CommonHelper.assertNotBlank("regex", regex);

        if (!regex.startsWith("^") || !regex.endsWith("$")) {
            throw new TechnicalException("Your regular expression: '" + regex + "' must start with a ^ and end with a $ " +
                "to define a full path matching");
        }

        excludedPatterns.add(Pattern.compile(regex));
        return this;
    }

    protected void warnRegexp() {
        if (!warnedRegexp) {
            logger.warn("Be careful when using the 'excludeBranch' or 'excludeRegex' methods. "
                + "They use regular expressions and their definitions may be error prone. You could exclude more URLs than expected.");
            warnedRegexp = true;
        }
    }

    protected void warnInclude() {
        if (!warnedInclude) {
            logger.warn("Be careful when using the 'includePath' or 'includePaths' methods. "
                + "The security will only apply on these paths. It could not be secure enough.");
            warnedInclude = true;
        }
    }

    @Override
    public boolean matches(final WebContext context, final SessionStore sessionStore) {
        return matches(context.getPath());
    }

    // Returns true if a path should be authenticated, false to skip authentication.
    boolean matches(final String requestPath) {

        logger.debug("request path to match: {}", requestPath);

        if (!includedPaths.isEmpty()) {
            for (var path : includedPaths) {
                // accepts any request path starting with the included path
                if (requestPath != null && requestPath.startsWith(path)) {
                    return true;
                }
            }

            return false;
        }

        // just exclude the exact matching request path
        if (excludedPaths.contains(requestPath)) {
            return false;
        }

        for (var pattern : excludedPatterns) {
            if (pattern.matcher(requestPath).matches()) {
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
        paths.forEach(this::excludePath);
    }

    public void setExcludedPatterns(Collection<String> regularExpressions) {
        excludedPatterns.clear();
        regularExpressions.forEach(this::excludeRegex);
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

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "includedPaths", includedPaths,
            "excludedPaths", excludedPaths, "excludedPatterns", excludedPatterns);
    }
}
