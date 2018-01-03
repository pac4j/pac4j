package org.pac4j.http.authorization.generator;

import org.pac4j.core.authorization.generator.AuthorizationGenerator;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;

import java.util.Optional;

/**
 * Save a form remember-me checkbox into the remember-me nature of the profile.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class RememberMeAuthorizationGenerator<U extends CommonProfile> implements AuthorizationGenerator<U> {

    private String rememberMeParameterName = "rme";

    private String rememberMeValue = "true";

    public RememberMeAuthorizationGenerator() {
    }

    public RememberMeAuthorizationGenerator(final String rememberMeParameterName, final String rememberMeValue) {
        setRememberMeParameterName(rememberMeParameterName);
        setRememberMeValue(rememberMeValue);
    }

    @Override
    public U generate(final WebContext context, final U profile) {
        final Optional<String> rmeValue = context.getRequestParameter(rememberMeParameterName);
        rmeValue.ifPresent(
            v -> {
                if (rememberMeValue.equals(v)) {
                    profile.setRemembered(true);
                }
            }
        );
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
