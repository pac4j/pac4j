package org.pac4j.http.credentials.authenticator;

import org.pac4j.core.profile.CommonProfile;
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
public abstract class AbstractRegexpAuthenticator<U extends CommonProfile> extends ProfileDefinitionAware<U> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected String regexpPattern;

    protected Pattern pattern;

    public void setRegexpPattern(final String regexpPattern) {
        CommonHelper.assertNotNull("regexpPattern", regexpPattern);
        this.regexpPattern = regexpPattern;
        this.pattern = Pattern.compile(regexpPattern);
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "regexpPattern", this.regexpPattern);
    }
}
