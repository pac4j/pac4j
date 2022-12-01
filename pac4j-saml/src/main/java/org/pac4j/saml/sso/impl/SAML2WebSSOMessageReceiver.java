package org.pac4j.saml.sso.impl;

import lombok.val;
import org.opensaml.saml.saml2.core.StatusResponseType;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.pac4j.core.context.WebContext;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.profile.api.SAML2ResponseValidator;
import org.pac4j.saml.profile.impl.AbstractSAML2MessageReceiver;
import org.pac4j.saml.transport.AbstractPac4jDecoder;
import org.pac4j.saml.transport.Pac4jHTTPPostDecoder;
import org.pac4j.saml.util.Configuration;

import java.util.Optional;

/**
 * @author Misagh Moayyed
 */
public class SAML2WebSSOMessageReceiver extends AbstractSAML2MessageReceiver {

    private static final String SAML2_WEBSSO_PROFILE_URI = "urn:oasis:names:tc:SAML:2.0:profiles:SSO:browser";

    public SAML2WebSSOMessageReceiver(final SAML2ResponseValidator validator,
                                      final SAML2Configuration saml2Configuration) {
        super(validator, saml2Configuration);
    }

    @Override
    protected AbstractPac4jDecoder getDecoder(final WebContext webContext) {
        val decoder = new Pac4jHTTPPostDecoder(webContext);
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
    protected Optional<Endpoint> getEndpoint(final SAML2MessageContext context, final StatusResponseType response) {
        return Optional.of(
            context.getSPAssertionConsumerService(response)
        );
    }

    @Override
    protected String getProfileUri() {
        return SAML2_WEBSSO_PROFILE_URI;
    }
}
