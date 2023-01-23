package org.pac4j.saml.profile.api;

import org.pac4j.core.credentials.AuthenticationCredentials;
import org.pac4j.saml.context.SAML2MessageContext;

/**
 * Defined ops to handle receiving saml messages from IdPs.
 * @author Misagh Moayyed
 * @since 1.7
 */
@FunctionalInterface
public interface SAML2MessageReceiver {
    AuthenticationCredentials receiveMessage(SAML2MessageContext context);
}
