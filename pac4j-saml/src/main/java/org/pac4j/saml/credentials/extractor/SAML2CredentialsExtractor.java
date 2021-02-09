package org.pac4j.saml.credentials.extractor;

import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.opensaml.saml.saml2.core.LogoutResponse;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.context.SAMLContextProvider;
import org.pac4j.saml.credentials.SAML2Credentials;
import org.pac4j.saml.profile.api.SAML2ProfileHandler;
import org.pac4j.saml.logout.impl.SAML2LogoutResponseBuilder;
import org.pac4j.saml.logout.impl.SAML2LogoutResponseMessageSender;
import org.pac4j.saml.transport.Pac4jSAMLResponse;

import java.util.Optional;

/**
 * Credentials extractor of SAML2 credentials.
 *
 * @author Jerome Leleu
 * @since 3.4.0
 */
public class SAML2CredentialsExtractor implements CredentialsExtractor {

    protected final SAMLContextProvider contextProvider;

    protected final SAML2ProfileHandler<AuthnRequest> profileHandler;

    protected final SAML2ProfileHandler<LogoutRequest> logoutProfileHandler;

    protected final String spLogoutResponseBindingType;

    protected SAML2LogoutResponseBuilder saml2LogoutResponseBuilder;

    protected final SAML2LogoutResponseMessageSender saml2LogoutResponseMessageSender;

    public SAML2CredentialsExtractor(final SAML2Client client) {
        this.contextProvider = client.getContextProvider();
        this.profileHandler = client.getProfileHandler();
        this.logoutProfileHandler = client.getLogoutProfileHandler();
        this.spLogoutResponseBindingType = client.getConfiguration().getSpLogoutResponseBindingType();
        this.saml2LogoutResponseBuilder = new SAML2LogoutResponseBuilder(spLogoutResponseBindingType);
        this.saml2LogoutResponseMessageSender = new SAML2LogoutResponseMessageSender(client.getSignatureSigningParametersProvider(),
            spLogoutResponseBindingType, false, client.getConfiguration().isSpLogoutRequestSigned());
    }

    @Override
    public Optional<Credentials> extract(final WebContext context, final SessionStore sessionStore) {
        final SAML2MessageContext samlContext = this.contextProvider.buildContext(context, sessionStore);
        final boolean logoutEndpoint = isLogoutEndpointRequest(context, samlContext);
        if (logoutEndpoint) {
            receiveLogout(samlContext);
            sendLogoutResponse(samlContext);
            adaptLogoutResponseToBinding(context, samlContext);
            return Optional.empty();
        }
        return receiveLogin(samlContext, context);
    }

    protected Optional<Credentials> receiveLogin(final SAML2MessageContext samlContext, final WebContext context) {
        final SAML2Credentials credentials = (SAML2Credentials) this.profileHandler.receive(samlContext);
        return Optional.ofNullable(credentials);
    }

    protected void adaptLogoutResponseToBinding(final WebContext context, final SAML2MessageContext samlContext) {
        final Pac4jSAMLResponse adapter = samlContext.getProfileRequestContextOutboundMessageTransportResponse();
        if (spLogoutResponseBindingType.equalsIgnoreCase(SAMLConstants.SAML2_POST_BINDING_URI)) {
            final String content = adapter.getOutgoingContent();
            throw HttpActionHelper.buildFormPostContentAction(context, content);
        } else {
            final String location = adapter.getRedirectUrl();
            throw HttpActionHelper.buildRedirectUrlAction(context, location);
        }
    }

    protected void sendLogoutResponse(final SAML2MessageContext samlContext) {
        final LogoutResponse logoutResponse = this.saml2LogoutResponseBuilder.build(samlContext);
        this.saml2LogoutResponseMessageSender.sendMessage(samlContext, logoutResponse,
            samlContext.getSAMLBindingContext().getRelayState());
    }

    protected void receiveLogout(final SAML2MessageContext samlContext) {
        this.logoutProfileHandler.receive(samlContext);
    }

    protected boolean isLogoutEndpointRequest(final WebContext context,
                                              final SAML2MessageContext samlContext) {
        return context.getRequestParameter(Pac4jConstants.LOGOUT_ENDPOINT_PARAMETER).isPresent();
    }

    public SAML2LogoutResponseBuilder getSaml2LogoutResponseBuilder() {
        return saml2LogoutResponseBuilder;
    }

    public void setSaml2LogoutResponseBuilder(final SAML2LogoutResponseBuilder saml2LogoutResponseBuilder) {
        this.saml2LogoutResponseBuilder = saml2LogoutResponseBuilder;
    }
}
