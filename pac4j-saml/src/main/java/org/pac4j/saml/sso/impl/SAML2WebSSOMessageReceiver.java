package org.pac4j.saml.sso.impl;

import org.pac4j.core.context.WebContext;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.profile.api.SAML2ResponseValidator;
import org.pac4j.saml.profile.impl.AbstractSAML2MessageReceiver;
import org.pac4j.saml.transport.AbstractPac4jDecoder;
import org.pac4j.saml.transport.Pac4jHTTPPostDecoder;
import org.pac4j.saml.util.Configuration;

/**
 * @author Misagh Moayyed
 */
public class SAML2WebSSOMessageReceiver extends AbstractSAML2MessageReceiver {

    private static final String SAML2_WEBSSO_PROFILE_URI = "urn:oasis:names:tc:SAML:2.0:profiles:SSO:browser";

    public SAML2WebSSOMessageReceiver(final SAML2ResponseValidator validator, final boolean cas5Compatibility) {
        super(validator, cas5Compatibility);
    }

    @Override
    protected AbstractPac4jDecoder getDecoder(final WebContext webContext) {
        final Pac4jHTTPPostDecoder decoder = new Pac4jHTTPPostDecoder(webContext, cas5Compatibility);
        try {
            decoder.setParserPool(Configuration.getParserPool());
            decoder.initialize();
            decoder.decode();

        } catch (final Exception e) {
            throw new SAMLException("Error decoding SAML message", e);
        }
        return decoder;
    }

    @Override
    protected String getProfileUri() {
        return SAML2_WEBSSO_PROFILE_URI;
    }
}
