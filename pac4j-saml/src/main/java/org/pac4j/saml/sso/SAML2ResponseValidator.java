package org.pac4j.saml.sso;

import org.pac4j.core.credentials.Credentials;
import org.pac4j.saml.context.SAML2MessageContext;

/**
 * Defines operations needed to validate the response
 * from IdP.
 * @author Misagh Moayyed
 * @since 1.7
 */
public interface SAML2ResponseValidator {

    /**
     * Validates the SAML protocol response and the SAML SSO response.
     * The method decrypt encrypted assertions if any.
     *
     * @param context the context
     */
    Credentials validate(SAML2MessageContext context);

    void setMaximumAuthenticationLifetime(int maximumAuthenticationLifetime);

    void setAcceptedSkew(int acceptedSkew);
}
