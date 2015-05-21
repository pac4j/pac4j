package org.pac4j.saml.sso;

import org.pac4j.core.credentials.Credentials;
import org.pac4j.saml.context.ExtendedSAMLMessageContext;

/**
 * @author Misagh Moayyed
 */
public interface SAML2MessageReceiver {
    Credentials receiveMessage(ExtendedSAMLMessageContext context);
}
