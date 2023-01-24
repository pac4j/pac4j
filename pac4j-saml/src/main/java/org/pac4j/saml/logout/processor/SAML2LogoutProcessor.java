package org.pac4j.saml.logout.processor;

import lombok.val;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.logout.processor.LogoutProcessor;
import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.context.SAMLContextProvider;
import org.pac4j.saml.logout.impl.SAML2LogoutResponseBuilder;
import org.pac4j.saml.logout.impl.SAML2LogoutResponseMessageSender;

/**
 * The SAML2 logout processor.
 *
 * @author Jerome LELEU
 * @since 6.0.0
 */
public class SAML2LogoutProcessor implements LogoutProcessor {

    private final SAMLContextProvider contextProvider;

    private final SAML2Client saml2Client;

    private final String spLogoutResponseBindingType;

    private final SAML2LogoutResponseBuilder saml2LogoutResponseBuilder;

    private final SAML2LogoutResponseMessageSender saml2LogoutResponseMessageSender;

    public SAML2LogoutProcessor(final SAML2Client client) {
        this.contextProvider = client.getContextProvider();
        this.saml2Client = client;
        this.spLogoutResponseBindingType = client.getConfiguration().getSpLogoutResponseBindingType();
        this.saml2LogoutResponseBuilder = new SAML2LogoutResponseBuilder(spLogoutResponseBindingType);
        this.saml2LogoutResponseMessageSender = new SAML2LogoutResponseMessageSender(client.getSignatureSigningParametersProvider(),
            spLogoutResponseBindingType, false, client.getConfiguration().isSpLogoutRequestSigned());
    }

    @Override
    public HttpAction processLogout(final CallContext ctx, final Credentials credentials) {
        val samlContext = this.contextProvider.buildContext(ctx, this.saml2Client);

        sendLogoutResponse(samlContext);

        return adaptLogoutResponseToBinding(ctx.webContext(), samlContext);
    }

    protected void sendLogoutResponse(final SAML2MessageContext samlContext) {
        val logoutResponse = this.saml2LogoutResponseBuilder.build(samlContext);
        this.saml2LogoutResponseMessageSender.sendMessage(samlContext, logoutResponse,
            samlContext.getSAMLBindingContext().getRelayState());
    }

    protected HttpAction adaptLogoutResponseToBinding(final WebContext context, final SAML2MessageContext samlContext) {
        val adapter = samlContext.getProfileRequestContextOutboundMessageTransportResponse();
        if (spLogoutResponseBindingType.equalsIgnoreCase(SAMLConstants.SAML2_POST_BINDING_URI)) {
            val content = adapter.getOutgoingContent();
            return HttpActionHelper.buildFormPostContentAction(context, content);
        } else {
            val location = adapter.getRedirectUrl();
            return HttpActionHelper.buildRedirectUrlAction(context, location);
        }
    }
}
