package org.pac4j.saml.redirect;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.redirect.RedirectionActionBuilder;
import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.saml.client.SAML2Client;

import java.util.Optional;

/**
 * Redirection action builder for SAML 2.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
@RequiredArgsConstructor
public class SAML2RedirectionActionBuilder implements RedirectionActionBuilder {

    private final SAML2Client client;

    @Override
    public Optional<RedirectionAction> getRedirectionAction(final CallContext ctx) {
        val context = this.client.getContextProvider().buildContext(ctx, this.client);
        val relayState = this.client.getStateGenerator().generateValue(ctx);

        val saml2ObjectBuilder = client.getConfiguration().getSamlAuthnRequestBuilder();
        val authnRequest = saml2ObjectBuilder.build(context);
        this.client.getWebSsoMessageSender().sendMessage(context, authnRequest, relayState);

        val adapter = context.getProfileRequestContextOutboundMessageTransportResponse();

        val webContext = ctx.webContext();

        val bindingType = this.client.getConfiguration().getAuthnRequestBindingType();
        if (SAMLConstants.SAML2_POST_BINDING_URI.equalsIgnoreCase(bindingType) ||
            SAMLConstants.SAML2_POST_SIMPLE_SIGN_BINDING_URI.equalsIgnoreCase(bindingType)) {
            val content = adapter.getOutgoingContent();
            return Optional.of(HttpActionHelper.buildFormPostContentAction(webContext, content));
        }
        val location = adapter.getRedirectUrl();
        return Optional.of(HttpActionHelper.buildRedirectUrlAction(webContext, location));
    }
}
