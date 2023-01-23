package org.pac4j.saml.profile.api;

import org.opensaml.saml.common.SAMLObject;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.core.credentials.AuthenticationCredentials;

/**
 * Handles a SAML protocol profile.
 * @author Misagh Moayyed
 * @since 1.7
 */
public interface SAML2ProfileHandler<T extends SAMLObject> {
    void send(SAML2MessageContext context, T msg, Object state);

    AuthenticationCredentials receive(SAML2MessageContext context);
}
