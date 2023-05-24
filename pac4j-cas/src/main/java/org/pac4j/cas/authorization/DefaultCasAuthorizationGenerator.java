package org.pac4j.cas.authorization;

import lombok.val;
import org.pac4j.core.authorization.generator.AuthorizationGenerator;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.profile.UserProfile;

import java.util.Optional;

/**
 * Default {@link AuthorizationGenerator} implementation
 * for a {@link org.pac4j.cas.client.CasClient} which is able to retrieve the isRemembered status
 * from the CAS response and put it in the profile.
 *
 * @author Michael Remond
 * @since 1.5.1
 */
public class DefaultCasAuthorizationGenerator implements AuthorizationGenerator {

    /** Constant <code>DEFAULT_REMEMBER_ME_ATTRIBUTE_NAME="longTermAuthenticationRequestTokenUsed"</code> */
    public static final String DEFAULT_REMEMBER_ME_ATTRIBUTE_NAME = "longTermAuthenticationRequestTokenUsed";

    // default name of the CAS attribute for remember me authentication (CAS 3.4.10+)
    protected String rememberMeAttributeName = DEFAULT_REMEMBER_ME_ATTRIBUTE_NAME;

    /**
     * <p>Constructor for DefaultCasAuthorizationGenerator.</p>
     */
    public DefaultCasAuthorizationGenerator() {
    }

    /**
     * <p>Constructor for DefaultCasAuthorizationGenerator.</p>
     *
     * @param rememberMeAttributeName a {@link String} object
     */
    public DefaultCasAuthorizationGenerator(final String rememberMeAttributeName) {
        this.rememberMeAttributeName = rememberMeAttributeName;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<UserProfile> generate(final CallContext ctx, final UserProfile profile) {
        val rememberMeValue = (String) profile.getAttribute(rememberMeAttributeName);
        val isRemembered = rememberMeValue != null && Boolean.parseBoolean(rememberMeValue);
        profile.setRemembered(isRemembered);
        return Optional.of(profile);
    }
}
