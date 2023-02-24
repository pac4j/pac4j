package org.pac4j.saml.profile;

import org.pac4j.core.profile.definition.CommonProfileDefinition;

/**
 * This is the dedicated class to hold the profile definition
 * for SAML2, when building the final user profile.
 *
 * @see SAML2Profile
 * @see org.pac4j.saml.credentials.authenticator.SAML2Authenticator
 * @author Misagh Moayyed
 * @version 5.0.0
 */
public class SAML2ProfileDefinition extends CommonProfileDefinition {
    /**
     * <p>Constructor for SAML2ProfileDefinition.</p>
     */
    public SAML2ProfileDefinition() {
        super(parameters -> new SAML2Profile());
    }
}
