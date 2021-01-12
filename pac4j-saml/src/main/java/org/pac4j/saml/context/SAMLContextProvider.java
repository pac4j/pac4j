package org.pac4j.saml.context;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;

/**
 * Builds the saml context for SP and the IDP.
 * @author Misagh Moayyed
 * @since 1.7
 */
public interface SAMLContextProvider {
    SAML2MessageContext buildServiceProviderContext(WebContext webContext, SessionStore sessionStore);

    SAML2MessageContext buildContext(WebContext webContext, SessionStore sessionStore);
}
