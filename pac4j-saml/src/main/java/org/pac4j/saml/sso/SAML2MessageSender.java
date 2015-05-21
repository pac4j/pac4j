package org.pac4j.saml.sso;

import org.opensaml.saml.common.SAMLObject;
import org.pac4j.saml.context.ExtendedSAMLMessageContext;

/**
 * @author Misagh Moayyed
 */
public interface SAML2MessageSender<T extends SAMLObject> {
    void sendMessage(final ExtendedSAMLMessageContext context,
                     final T request,
                     final Object state);
}
