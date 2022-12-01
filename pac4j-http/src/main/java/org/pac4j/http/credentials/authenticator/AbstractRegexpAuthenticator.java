package org.pac4j.http.credentials.authenticator;

import lombok.ToString;
import org.pac4j.core.profile.definition.ProfileDefinitionAware;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * Abstract authenticator based on regular expressions.
 *
 * @author Jerome Leleu
 * @since 3.3.0
 */
@ToString(onlyExplicitlyIncluded = true)
public abstract class AbstractRegexpAuthenticator extends ProfileDefinitionAware {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @ToString.Include
    protected String regexpPattern;

    protected Pattern pattern;

    public void setRegexpPattern(final String regexpPattern) {
        CommonHelper.assertNotNull("regexpPattern", regexpPattern);
        this.regexpPattern = regexpPattern;
        this.pattern = Pattern.compile(regexpPattern);
    }
}
