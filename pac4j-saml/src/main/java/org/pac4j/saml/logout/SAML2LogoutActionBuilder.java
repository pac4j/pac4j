package org.pac4j.saml.logout;

import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.core.logout.LogoutActionBuilder;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.generator.ValueGenerator;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.context.SAMLContextProvider;
import org.pac4j.saml.logout.impl.SAML2LogoutRequestBuilder;
import org.pac4j.saml.profile.SAML2Profile;
import org.pac4j.saml.profile.api.SAML2ProfileHandler;

import java.util.Optional;

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

    protected final ValueGenerator stateGenerator;

    public SAML2LogoutActionBuilder(final SAML2Client client) {
        this.logoutProfileHandler = client.getLogoutProfileHandler();
        this.contextProvider = client.getContextProvider();
        this.configuration = client.getConfiguration();
        this.stateGenerator = client.getStateGenerator();
        this.saml2LogoutRequestBuilder = new SAML2LogoutRequestBuilder(configuration);
    }

    @Override
    public Optional<RedirectionAction> getLogoutAction(final WebContext context, final SessionStore sessionStore,
                                                       final UserProfile currentProfile, final String targetUrl) {
        if (currentProfile instanceof SAML2Profile) {
            final var saml2Profile = (SAML2Profile) currentProfile;
            final var samlContext = this.contextProvider.buildContext(context, sessionStore);
            final var relayState = this.stateGenerator.generateValue(context, sessionStore);

            final var logoutRequest = this.saml2LogoutRequestBuilder.build(samlContext, saml2Profile);
            this.logoutProfileHandler.send(samlContext, logoutRequest, relayState);

            final var adapter = samlContext.getProfileRequestContextOutboundMessageTransportResponse();
            if (this.configuration.getSpLogoutRequestBindingType().equalsIgnoreCase(SAMLConstants.SAML2_POST_BINDING_URI)) {
                final var content = adapter.getOutgoingContent();
                return Optional.of(HttpActionHelper.buildFormPostContentAction(context, content));
            }
            final var location = adapter.getRedirectUrl();
            return Optional.of(HttpActionHelper.buildRedirectUrlAction(context, location));
        } else {
            return Optional.empty();
        }
    }
}
