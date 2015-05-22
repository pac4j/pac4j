package org.pac4j.saml.sso;

import org.opensaml.saml.common.SAMLObject;
import org.pac4j.saml.context.ExtendedSAMLMessageContext;

/**
 * Sends a SAML object to the context given.
 * @author Misagh Moayyed
 * @since 1.7
 */
public interface SAML2MessageSender<T extends SAMLObject> {
    void sendMessage(ExtendedSAMLMessageContext context,
                     T request,
                     Object state);
}
