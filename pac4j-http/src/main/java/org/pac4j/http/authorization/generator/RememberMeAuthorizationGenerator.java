package org.pac4j.http.authorization.generator;

import org.pac4j.core.authorization.generator.AuthorizationGenerator;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;

/**
 * Save a form remember-me checkbox into the remember-me nature of the profile.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class RememberMeAuthorizationGenerator implements AuthorizationGenerator {

    private String rememberMeParameterName = "rme";

    private String rememberMeValue = "true";

    public RememberMeAuthorizationGenerator() {}

    public RememberMeAuthorizationGenerator(final String rememberMeParameterName, final String rememberMeValue) {
        setRememberMeParameterName(rememberMeParameterName);
        setRememberMeValue(rememberMeValue);
    }

    @Override
    public CommonProfile generate(final WebContext context, final CommonProfile profile) {
        final String rmeValue = context.getRequestParameter(rememberMeParameterName);
        if (rememberMeValue.equals(rmeValue)) {
            profile.setRemembered(true);
        }
        return profile;
    }

    public String getRememberMeParameterName() {
        return rememberMeParameterName;
    }

    public void setRememberMeParameterName(final String rememberMeParameterName) {
        CommonHelper.assertNotBlank("rememberMeParameterName", rememberMeParameterName);
        this.rememberMeParameterName = rememberMeParameterName;
    }

    public String getRememberMeValue() {
        return rememberMeValue;
    }

    public void setRememberMeValue(final String rememberMeValue) {
        CommonHelper.assertNotBlank("rememberMeValue", rememberMeValue);
        this.rememberMeValue = rememberMeValue;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "rememberMeParameterName", rememberMeParameterName,
            "rememberMeValue", rememberMeValue);
    }
}
