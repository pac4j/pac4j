package org.pac4j.saml.sso.impl;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * This is {@link org.pac4j.saml.sso.impl.SAML2ScopingIdentityProvider}.
 *
 * @author Misagh Moayyed
 * @since 5.1.2
 */
@Getter
@ToString
public class SAML2ScopingIdentityProvider implements Serializable {
    private final String providerId;

    private final String name;

    /**
     * <p>Constructor for SAML2ScopingIdentityProvider.</p>
     *
     * @param providerId a {@link java.lang.String} object
     * @param name a {@link java.lang.String} object
     */
    public SAML2ScopingIdentityProvider(final String providerId, final String name) {
        this.providerId = providerId;
        this.name = name;
    }
}
