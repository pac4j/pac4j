package org.pac4j.saml.logout;

import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.logout.LogoutActionBuilder;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.core.state.StateGenerator;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.client.SAML2ClientConfiguration;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.context.SAMLContextProvider;
import org.pac4j.saml.profile.SAML2Profile;
import org.pac4j.saml.sso.SAML2ProfileHandler;
import org.pac4j.saml.sso.impl.*;
import org.pac4j.saml.transport.Pac4jSAMLResponse;

/**
 * Logout action builder for SAML 2.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class SAML2LogoutActionBuilder<U extends SAML2Profile> implements LogoutActionBuilder<U> {

    protected SAML2LogoutRequestBuilder saml2LogoutRequestBuilder;

    protected final SAML2ProfileHandler<LogoutRequest> logoutProfileHandler;

    protected final SAMLContextProvider contextProvider;

    protected final SAML2ClientConfiguration configuration;

    protected final StateGenerator stateGenerator;

    public SAML2LogoutActionBuilder(final SAML2Client client) {
        this.logoutProfileHandler = client.getLogoutProfileHandler();
        this.contextProvider = client.getContextProvider();
        this.configuration = client.getConfiguration();
        this.stateGenerator = client.getStateGenerator();
        this.saml2LogoutRequestBuilder = new SAML2LogoutRequestBuilder(configuration.getSpLogoutRequestBindingType());
    }

    @Override
    public RedirectAction getLogoutAction(final WebContext context, final U currentProfile, final String targetUrl) {
        final SAML2MessageContext samlContext = this.contextProvider.buildContext(context);
        final String relayState = this.stateGenerator.generateState(context);

        final LogoutRequest logoutRequest = this.saml2LogoutRequestBuilder.build(samlContext, currentProfile);
        this.logoutProfileHandler.send(samlContext, logoutRequest, relayState);

        final Pac4jSAMLResponse adapter = samlContext.getProfileRequestContextOutboundMessageTransportResponse();
        if (this.configuration.getSpLogoutRequestBindingType().equalsIgnoreCase(SAMLConstants.SAML2_POST_BINDING_URI)) {
            final String content = adapter.getOutgoingContent();
            return RedirectAction.success(content);
        }
        final String location = adapter.getRedirectUrl();
        return RedirectAction.redirect(location);
    }
}
