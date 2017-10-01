package org.pac4j.cas.authorization;

import org.pac4j.cas.client.CasClient;
import org.pac4j.core.authorization.generator.AuthorizationGenerator;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;

/**
 * Default {@link AuthorizationGenerator} implementation for a {@link CasClient} which is able
 * to retrieve the isRemembered status from the CAS response and put it in the profile.
 *
 * @author Michael Remond
 * @since 1.5.1
 *
 * @param <U> the profile
 */
public class DefaultCasAuthorizationGenerator<U extends CommonProfile> implements AuthorizationGenerator<U> {

    public static final String DEFAULT_REMEMBER_ME_ATTRIBUTE_NAME = "longTermAuthenticationRequestTokenUsed";

    // default name of the CAS attribute for remember me authentication (CAS 3.4.10+)
    protected String rememberMeAttributeName = DEFAULT_REMEMBER_ME_ATTRIBUTE_NAME;

    public DefaultCasAuthorizationGenerator() {
    }

    public DefaultCasAuthorizationGenerator(String rememberMeAttributeName) {
        this.rememberMeAttributeName = rememberMeAttributeName;
    }

    @Override
    public U generate(final WebContext context, final U profile) {
        String rememberMeValue = (String) profile.getAttribute(rememberMeAttributeName);
        boolean isRemembered = rememberMeValue != null && Boolean.parseBoolean(rememberMeValue);
        profile.setRemembered(isRemembered);
        return profile;
    }
}
