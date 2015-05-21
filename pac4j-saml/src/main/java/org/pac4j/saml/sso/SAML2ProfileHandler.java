package org.pac4j.saml.sso;

import org.opensaml.saml.common.SAMLObject;
import org.pac4j.saml.context.ExtendedSAMLMessageContext;
import org.pac4j.core.credentials.Credentials;
/**
 * @author Misagh Moayyed
 */
public interface SAML2ProfileHandler<T extends SAMLObject> {
    void send(ExtendedSAMLMessageContext context, T msg, Object state);

    Credentials receive(ExtendedSAMLMessageContext context);
}
