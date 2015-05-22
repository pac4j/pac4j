package org.pac4j.saml.sso;

import org.pac4j.core.credentials.Credentials;
import org.pac4j.saml.context.ExtendedSAMLMessageContext;

/**
 * Defines operations needed to validate the response
 * from IdP.
 * @author Misagh Moayyed
 * @since 1.7
 */
public interface SAML2ResponseValidator {

    Credentials validate(ExtendedSAMLMessageContext context);
}
