package org.pac4j.saml.sso;

import org.opensaml.saml.common.SAMLObject;
import org.pac4j.saml.context.ExtendedSAMLMessageContext;
import org.pac4j.core.credentials.Credentials;

/**
 * Handles a SAML protocol profile.
 * @author Misagh Moayyed
 * @since 1.7
 */
public interface SAML2ProfileHandler<T extends SAMLObject> {
    void send(ExtendedSAMLMessageContext context, T msg, Object state);

    Credentials receive(ExtendedSAMLMessageContext context);
}
