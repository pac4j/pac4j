package org.pac4j.saml.context;

import org.pac4j.core.context.WebContext;

/**
 * Builds the saml context for SP and the IDP.
 * @author Misagh Moayyed
 * @author Misagh Moayyed
 * @since 1.7
 */
public interface SAMLContextProvider {
    ExtendedSAMLMessageContext buildServiceProviderContext(WebContext webContext);

    ExtendedSAMLMessageContext buildContext(WebContext webContext);
}
