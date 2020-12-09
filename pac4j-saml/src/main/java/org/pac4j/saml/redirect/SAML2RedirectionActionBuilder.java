package org.pac4j.saml.redirect;

import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.core.redirect.RedirectionActionBuilder;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.profile.api.SAML2ObjectBuilder;
import org.pac4j.saml.sso.impl.SAML2AuthnRequestBuilder;
import org.pac4j.saml.transport.Pac4jSAMLResponse;

import java.util.Optional;

/**
 * Redirection action builder for SAML 2.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class SAML2RedirectionActionBuilder implements RedirectionActionBuilder {

    protected SAML2ObjectBuilder<AuthnRequest> saml2ObjectBuilder;

    private final SAML2Client client;

    public SAML2RedirectionActionBuilder(final SAML2Client client) {
        CommonHelper.assertNotNull("client", client);
        this.client = client;
        final SAML2Configuration cfg = client.getConfiguration();
        this.saml2ObjectBuilder = new SAML2AuthnRequestBuilder(cfg);
    }

    @Override
    public Optional<RedirectionAction> getRedirectionAction(final WebContext wc) {
        final SAML2MessageContext context = this.client.getContextProvider().buildContext(wc);
        final String relayState = this.client.getStateGenerator().generateValue(wc);

        final AuthnRequest authnRequest = this.saml2ObjectBuilder.build(context);
        this.client.getProfileHandler().send(context, authnRequest, relayState);

        final Pac4jSAMLResponse adapter = context.getProfileRequestContextOutboundMessageTransportResponse();

        final String bindingType = this.client.getConfiguration().getAuthnRequestBindingType();
        if (SAMLConstants.SAML2_POST_BINDING_URI.equalsIgnoreCase(bindingType) ||
            SAMLConstants.SAML2_POST_SIMPLE_SIGN_BINDING_URI.equalsIgnoreCase(bindingType)) {
            final String content = adapter.getOutgoingContent();
            return Optional.of(HttpActionHelper.buildFormPostContentAction(wc, content));
        }
        final String location = adapter.getRedirectUrl();
        return Optional.of(HttpActionHelper.buildRedirectUrlAction(wc, location));
    }
}
