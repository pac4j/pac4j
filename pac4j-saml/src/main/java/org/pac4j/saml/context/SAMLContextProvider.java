package org.pac4j.saml.context;

import org.pac4j.core.context.WebContext;

/**
 * Builds the saml context for SP and the IDP.
 * @author Misagh Moayyed
 * @since 1.7
 */
public interface SAMLContextProvider {
    SAML2MessageContext buildServiceProviderContext(WebContext webContext);

    SAML2MessageContext buildContext(WebContext webContext);
}
