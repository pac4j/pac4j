package org.pac4j.saml.profile.api;

import org.opensaml.saml.common.SAMLObject;
import org.pac4j.saml.context.SAML2MessageContext;

/**
 * Sends a SAML object to the context given.
 * @author Misagh Moayyed
 * @since 1.7
 */
public interface SAML2MessageSender<T extends SAMLObject> {
    void sendMessage(SAML2MessageContext context,
                     T request,
                     Object state);
}
