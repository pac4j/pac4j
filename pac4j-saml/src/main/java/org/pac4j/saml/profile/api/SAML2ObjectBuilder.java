package org.pac4j.saml.profile.api;

import org.opensaml.saml.common.SAMLObject;
import org.pac4j.saml.context.SAML2MessageContext;

/**
 * Builds an authentication request for the idp.
 *
 * @author Misagh Moayyed
 * @since 1.7
 */
@FunctionalInterface
public interface SAML2ObjectBuilder<T extends SAMLObject> {

    /**
     * <p>build.</p>
     *
     * @param context a {@link SAML2MessageContext} object
     * @return a T object
     */
    T build(SAML2MessageContext context);
}
