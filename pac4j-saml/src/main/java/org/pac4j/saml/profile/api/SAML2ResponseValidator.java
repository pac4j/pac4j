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
     * Define the accepted clock skew (in seconds) between the SP and the IdP.
     *
     * @param clockSkewSeconds the clock skew in seconds
     */
    void setClockSkewSeconds(long clockSkewSeconds);

    /**
     * @deprecated use {@link #setClockSkewSeconds(long)}
     */
    @Deprecated
    default void setAcceptedSkew(final long acceptedSkew) {
        setClockSkewSeconds(acceptedSkew);
    }
}
