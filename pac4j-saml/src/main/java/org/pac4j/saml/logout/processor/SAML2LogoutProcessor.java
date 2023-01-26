package org.pac4j.saml.logout.processor;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.opensaml.saml.saml2.core.LogoutResponse;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.NoContentAction;
import org.pac4j.core.exception.http.OkAction;
import org.pac4j.core.logout.processor.LogoutProcessor;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.context.SAMLContextProvider;
import org.pac4j.saml.credentials.SAML2Credentials;
import org.pac4j.saml.exceptions.SAMLException;
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

    @Setter
    private String postLogoutURL;

    /**
     * When set to false, will cause the validator
     * to not throw back exceptions and expect adaptations of those exceptions
     * when the response is successfully validated. Instead, the validator should successfully
     * move on without throwing {@link OkAction}.
     */
    @Getter
    @Setter
    private boolean actionOnSuccess = true;

    public SAML2LogoutProcessor(final SAML2Client client) {
        this.contextProvider = client.getContextProvider();
        this.saml2Client = client;
        this.spLogoutResponseBindingType = client.getConfiguration().getSpLogoutResponseBindingType();
        this.saml2LogoutResponseBuilder = new SAML2LogoutResponseBuilder(spLogoutResponseBindingType);
        this.saml2LogoutResponseMessageSender = new SAML2LogoutResponseMessageSender(client.getSignatureSigningParametersProvider(),
            spLogoutResponseBindingType, false, client.getConfiguration().isSpLogoutRequestSigned());
        this.postLogoutURL = client.getConfiguration().getPostLogoutURL();
    }

    @Override
    public HttpAction processLogout(final CallContext ctx, final Credentials credentials) {
        val saml2Credentials = (SAML2Credentials) credentials;
        val message = (SAMLObject) saml2Credentials.getContext().getMessageContext().getMessage();
        val samlContext = this.contextProvider.buildContext(ctx, this.saml2Client);

        // IDP-initiated
        if (message instanceof LogoutRequest) {
            sendLogoutResponse(samlContext);
            return adaptLogoutResponseToBinding(ctx.webContext(), samlContext);

        // SP-initiated
        } else if (message instanceof LogoutResponse) {
            val action = handlePostLogoutResponse(samlContext);
            if (action != null) {
                return action;
            }
            return NoContentAction.INSTANCE;
        }

        throw new SAMLException("SAML message must be a LogoutRequest or LogoutResponse type");
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

    protected HttpAction handlePostLogoutResponse(final SAML2MessageContext context) {
        if (CommonHelper.isNotBlank(postLogoutURL)) {
            // if custom post logout URL is present then redirect to it
            return new FoundAction(postLogoutURL);
        }
        // nothing to reply to the logout response
        return this.actionOnSuccess ? new OkAction(Pac4jConstants.EMPTY_STRING) : null;
    }
}
