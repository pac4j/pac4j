package org.pac4j.openid4vp.profile;

import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.profile.factory.ProfileFactory;

/**
 * The definition of the profile built from a verified presentation: a {@link VerifiableCredentialProfile}
 * holding the disclosed claims as attributes. Its identifier comes from the
 * {@link ProfileIdResolver} of the configuration.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
public class OpenId4VpProfileDefinition extends CommonProfileDefinition {

    /** Creates a definition for a {@link VerifiableCredentialProfile}. */
    public OpenId4VpProfileDefinition() {
        this(parameters -> new VerifiableCredentialProfile());
    }

    /**
     * Creates a definition with a custom profile factory.
     *
     * @param profileFactory the profile factory
     */
    public OpenId4VpProfileDefinition(final ProfileFactory profileFactory) {
        super(profileFactory);
    }
}
