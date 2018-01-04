package org.pac4j.saml.logout;

import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.logout.LogoutActionBuilder;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.client.SAML2ClientConfiguration;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.profile.SAML2Profile;
import org.pac4j.saml.sso.SAML2ObjectBuilder;
import org.pac4j.saml.sso.SAML2ProfileHandler;
import org.pac4j.saml.sso.SAML2ResponseValidator;
import org.pac4j.saml.sso.impl.*;
import org.pac4j.saml.transport.Pac4jSAMLResponse;

import java.util.Optional;

/**
 * Logout action builder for SAML 2.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class SAML2LogoutActionBuilder<U extends SAML2Profile> implements LogoutActionBuilder<U> {

    protected SAML2ObjectBuilder<LogoutRequest> saml2LogoutObjectBuilder;

    protected SAML2ProfileHandler<LogoutRequest> logoutProfileHandler;

    protected SAML2ResponseValidator logoutResponseValidator;

    private final SAML2Client client;

    public SAML2LogoutActionBuilder(final SAML2Client client) {
        CommonHelper.assertNotNull("client", client);
        this.client = client;
        final SAML2ClientConfiguration cfg = client.getConfiguration();
        this.saml2LogoutObjectBuilder = new SAML2LogoutRequestBuilder(cfg.getDestinationBindingType());
        this.logoutResponseValidator = new SAML2LogoutResponseValidator(this.client.getSignatureTrustEngineProvider());
        this.logoutProfileHandler = new SAML2LogoutProfileHandler(
                new SAML2LogoutMessageSender(this.client.getSignatureSigningParametersProvider(),
                        cfg.getDestinationBindingType(), false, cfg.isAuthnRequestSigned()),
                new SAML2WebSSOMessageReceiver(this.logoutResponseValidator));
    }

    @Override
    public Optional<RedirectAction> getLogoutAction(final WebContext context, final U currentProfile, final String targetUrl) {
        final SAML2MessageContext samlContext = this.client.getContextProvider().buildContext(context);
        final String relayState = this.client.getStateParameter(context);

        final LogoutRequest logoutRequest = this.saml2LogoutObjectBuilder.build(samlContext);
        this.logoutProfileHandler.send(samlContext, logoutRequest, relayState);

        final Pac4jSAMLResponse adapter = samlContext.getProfileRequestContextOutboundMessageTransportResponse();
        if (this.client.getConfiguration().getDestinationBindingType().equalsIgnoreCase(SAMLConstants.SAML2_POST_BINDING_URI)) {
            final String content = adapter.getOutgoingContent();
            return Optional.ofNullable(RedirectAction.success(content));
        }
        final String location = adapter.getRedirectUrl();
        return Optional.ofNullable(RedirectAction.redirect(location));
    }
}
