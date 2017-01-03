package org.pac4j.core.matching;

import org.pac4j.core.exception.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * To match requests by excluding path.
 *
 * @deprecated use {@link org.pac4j.core.matching.PathMatcher} instead.
 * @author Jerome Leleu
 * @since 1.8.1
 */
@Deprecated
public final class ExcludedPathMatcher extends PathMatcher {

    private final static Logger logger = LoggerFactory.getLogger(ExcludedPathMatcher.class);

    public ExcludedPathMatcher() {}

    public ExcludedPathMatcher(final String excludePath) {
        setExcludePath(excludePath);
    }

    public String getExcludePath() {
        return super.getExcludedPatterns().iterator().next().pattern();
    }

    public void setExcludePath(String excludePath) {
        if (!super.getExcludedPatterns().isEmpty())
        {
            String msg = "ExcludedPathMatcher does not support excluding multiple paths. Use PathMatcher instead.";
            logger.error(msg);
            throw new TechnicalException(msg);
        }
        super.excludeRegex(excludePath);
    }
}
