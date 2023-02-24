package org.pac4j.saml.profile.api;

import org.pac4j.core.credentials.Credentials;
import org.pac4j.saml.context.SAML2MessageContext;

/**
 * Defines operations needed to validate the response
 * from IdP.
 *
 * @author Misagh Moayyed
 * @since 1.7
 */
public interface SAML2ResponseValidator {

    /**
     * Validates the SAML protocol response and the SAML SSO response.
     * The method decrypt encrypted assertions if any.
     *
     * @param context the context
     * @return the SAML credentials
     */
    Credentials validate(SAML2MessageContext context);

    /**
     * <p>setAcceptedSkew.</p>
     *
     * @param acceptedSkew a long
     */
    void setAcceptedSkew(long acceptedSkew);
}
