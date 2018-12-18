package org.pac4j.saml.logout;

import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.logout.LogoutActionBuilder;
import org.pac4j.core.logout.handler.LogoutHandler;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.core.state.StateGenerator;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.context.SAMLContextProvider;
import org.pac4j.saml.logout.impl.SAML2LogoutRequestBuilder;
import org.pac4j.saml.profile.SAML2Profile;
import org.pac4j.saml.profile.api.SAML2ProfileHandler;
import org.pac4j.saml.transport.Pac4jSAMLResponse;

/**
 * Logout action builder for SAML 2.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class SAML2LogoutActionBuilder implements LogoutActionBuilder {

    protected SAML2LogoutRequestBuilder saml2LogoutRequestBuilder;

    protected final SAML2ProfileHandler<LogoutRequest> logoutProfileHandler;

    protected final SAMLContextProvider contextProvider;

    protected final SAML2Configuration configuration;

    protected final StateGenerator stateGenerator;

    protected final LogoutHandler logoutHandler;

    public SAML2LogoutActionBuilder(final SAML2Client client) {
        this.logoutProfileHandler = client.getLogoutProfileHandler();
        this.contextProvider = client.getContextProvider();
        this.configuration = client.getConfiguration();
        this.stateGenerator = client.getStateGenerator();
        this.saml2LogoutRequestBuilder = new SAML2LogoutRequestBuilder(configuration.getSpLogoutRequestBindingType());
        this.logoutHandler = client.getConfiguration().getLogoutHandler();
    }

    @Override
    public RedirectAction getLogoutAction(final WebContext context, final CommonProfile currentProfile, final String targetUrl) {
        if (currentProfile instanceof SAML2Profile) {
            final SAML2Profile saml2Profile = (SAML2Profile) currentProfile;
            final SAML2MessageContext samlContext = this.contextProvider.buildContext(context);
            final String relayState = this.stateGenerator.generateState(context);

            final LogoutRequest logoutRequest = this.saml2LogoutRequestBuilder.build(samlContext, saml2Profile);
            this.logoutProfileHandler.send(samlContext, logoutRequest, relayState);

            // we won't get any session index from the logout response so we call the local logout before calling the IdP
            this.logoutHandler.destroySessionFront(context, saml2Profile.getSessionIndex());

            final Pac4jSAMLResponse adapter = samlContext.getProfileRequestContextOutboundMessageTransportResponse();
            if (this.configuration.getSpLogoutRequestBindingType().equalsIgnoreCase(SAMLConstants.SAML2_POST_BINDING_URI)) {
                final String content = adapter.getOutgoingContent();
                return RedirectAction.success(content);
            }
            final String location = adapter.getRedirectUrl();
            return RedirectAction.redirect(location);
        } else {
            return null;
        }
    }
}
