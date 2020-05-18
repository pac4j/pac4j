package org.pac4j.saml.logout.impl;

import org.opensaml.saml.saml2.core.StatusResponseType;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.pac4j.core.context.ContextHelper;
import org.pac4j.core.context.WebContext;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.profile.api.SAML2ResponseValidator;
import org.pac4j.saml.profile.impl.AbstractSAML2MessageReceiver;
import org.pac4j.saml.transport.AbstractPac4jDecoder;
import org.pac4j.saml.transport.Pac4jHTTPPostDecoder;
import org.pac4j.saml.transport.Pac4jHTTPRedirectDeflateDecoder;
import org.pac4j.saml.util.Configuration;

import java.util.Optional;

/**
 * Receives the SAML2 logout messages.
 *
 * @author Jerome Leleu
 * @since 3.4.0
 */
public class SAML2LogoutMessageReceiver extends AbstractSAML2MessageReceiver {

    private static final String SAML2_SLO_PROFILE_URI = "urn:oasis:names:tc:SAML:2.0:profiles:SSO:logout";

    public SAML2LogoutMessageReceiver(final SAML2ResponseValidator validator) {
        super(validator);
    }

    @Override
    protected AbstractPac4jDecoder getDecoder(final WebContext webContext) {
        final AbstractPac4jDecoder decoder;
        if (ContextHelper.isPost(webContext)) {
            decoder = new Pac4jHTTPPostDecoder(webContext);
            try {
                decoder.setParserPool(Configuration.getParserPool());
                decoder.initialize();
                decoder.decode();

            } catch (final Exception e) {
                throw new SAMLException("Error decoding POST SAML message", e);
            }
        } else if (ContextHelper.isGet(webContext)) {
            decoder = new Pac4jHTTPRedirectDeflateDecoder(webContext);

            try {
                decoder.setParserPool(Configuration.getParserPool());
                decoder.initialize();
                decoder.decode();

            } catch (final Exception e) {
                throw new SAMLException("Error decoding HTTP-Redirect SAML message", e);
            }
        } else {
            throw new SAMLException("Only GET or POST requests are accepted");
        }
        return decoder;
    }

    @Override
    protected Optional<Endpoint> getEndpoint(SAML2MessageContext context, StatusResponseType response) {
        return Optional.empty();
    }

    @Override
    protected String getProfileUri() {
        return SAML2_SLO_PROFILE_URI;
    }

}
