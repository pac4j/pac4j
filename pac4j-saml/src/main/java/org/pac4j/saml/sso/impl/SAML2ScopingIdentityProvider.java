package org.pac4j.saml.sso.impl;

import org.pac4j.core.util.CommonHelper;

import java.io.Serializable;

/**
 * This is {@link SAML2ScopingIdentityProvider}.
 *
 * @author Misagh Moayyed
 * @since 5.1.2
 */
public class SAML2ScopingIdentityProvider implements Serializable {
    private final String providerId;

    private final String name;

    public SAML2ScopingIdentityProvider(final String providerId, final String name) {
        this.providerId = providerId;
        this.name = name;
    }

    public String getProviderId() {
        return providerId;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(),
            "providerId", this.providerId,
            "name", this.name);
    }
}
