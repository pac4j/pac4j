package org.pac4j.core.matching;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * To match requests by excluding path.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public final class ExcludedPathMatcher implements Matcher {

    private final static Logger logger = LoggerFactory.getLogger(ExcludedPathMatcher.class);

    private String excludePath;

    private Pattern pattern;

    public ExcludedPathMatcher() {
    }

    public ExcludedPathMatcher(final String excludePath) {
        setExcludePath(excludePath);
    }

    @Override
    public boolean matches(final WebContext context) {
        if (pattern != null) {
            final String path = context.getPath();
            logger.debug("path to match: {}", path);
            return !pattern.matcher(path).matches();
        }
        return true;
    }

    public String getExcludePath() {
        return excludePath;
    }

    public void setExcludePath(String excludePath) {
        this.excludePath = excludePath;
        if (CommonHelper.isNotBlank(excludePath)) {
            logger.warn("Excluding paths is an advanced feature: be careful when defining your regular expression to avoid any security issue!");
            if (!excludePath.startsWith("^") || !excludePath.endsWith("$")) {
                final String msg = "Your regular expression: '" + excludePath + "' must start with a ^ and ends with a $ to define a full path matching";
                logger.error(msg);
                throw new TechnicalException(msg);
            }
            pattern = Pattern.compile(excludePath);
        }
    }
}
