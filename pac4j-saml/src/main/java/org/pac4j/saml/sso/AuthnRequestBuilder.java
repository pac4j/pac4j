package org.pac4j.saml.sso;

import org.opensaml.saml.saml2.core.AuthnRequest;
import org.pac4j.saml.context.ExtendedSAMLMessageContext;

/**
 * Builds an authentication request for the idp.
 * @author Misagh Moayyed
 * @since 1.7
 */
public interface AuthnRequestBuilder {

    AuthnRequest build(final ExtendedSAMLMessageContext context);
}
