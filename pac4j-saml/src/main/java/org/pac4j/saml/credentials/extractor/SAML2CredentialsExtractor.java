package org.pac4j.saml.credentials.extractor;

import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.opensaml.saml.saml2.core.LogoutResponse;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.exception.http.RedirectionActionHelper;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.context.SAMLContextProvider;
import org.pac4j.saml.credentials.SAML2Credentials;
import org.pac4j.saml.metadata.SAML2ServiceProviderMetadataResolver;
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
public class SAML2CredentialsExtractor implements CredentialsExtractor<SAML2Credentials> {

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
    public Optional<SAML2Credentials> extract(final WebContext context) {
        final boolean logoutEndpoint = context.getRequestParameter(SAML2ServiceProviderMetadataResolver.LOGOUT_ENDPOINT_PARAMETER)
            .isPresent();
        final SAML2MessageContext samlContext = this.contextProvider.buildContext(context);
        if (logoutEndpoint) {
            // SAML logout request/response
            this.logoutProfileHandler.receive(samlContext);

            // return a logout response if necessary
            final LogoutResponse logoutResponse = this.saml2LogoutResponseBuilder.build(samlContext);
            this.saml2LogoutResponseMessageSender.sendMessage(samlContext, logoutResponse,
                samlContext.getSAMLBindingContext().getRelayState());

            final Pac4jSAMLResponse adapter = samlContext.getProfileRequestContextOutboundMessageTransportResponse();
            if (spLogoutResponseBindingType.equalsIgnoreCase(SAMLConstants.SAML2_POST_BINDING_URI)) {
                final String content = adapter.getOutgoingContent();
                throw RedirectionActionHelper.buildFormPostContentAction(context, content);
            } else {
                final String location = adapter.getRedirectUrl();
                throw RedirectionActionHelper.buildRedirectUrlAction(context, location);
            }

        } else {
            // SAML authn response
            final SAML2Credentials credentials = (SAML2Credentials) this.profileHandler.receive(samlContext);
            return Optional.ofNullable(credentials);
        }
    }

    public SAML2LogoutResponseBuilder getSaml2LogoutResponseBuilder() {
        return saml2LogoutResponseBuilder;
    }

    public void setSaml2LogoutResponseBuilder(final SAML2LogoutResponseBuilder saml2LogoutResponseBuilder) {
        this.saml2LogoutResponseBuilder = saml2LogoutResponseBuilder;
    }
}
