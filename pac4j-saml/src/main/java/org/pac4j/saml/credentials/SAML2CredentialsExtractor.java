package org.pac4j.saml.credentials;

import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.context.SAMLContextProvider;
import org.pac4j.saml.metadata.SAML2ServiceProviderMetadataResolver;
import org.pac4j.saml.sso.SAML2ProfileHandler;

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

    public SAML2CredentialsExtractor(final SAML2Client client) {
        this.contextProvider = client.getContextProvider();
        this.profileHandler = client.getProfileHandler();
        this.logoutProfileHandler = client.getLogoutProfileHandler();
    }

    @Override
    public SAML2Credentials extract(WebContext context) {
        final boolean logoutEndpoint = context.getRequestParameter(SAML2ServiceProviderMetadataResolver.LOGOUT_ENDPOINT_PARAMETER) != null;
        final SAML2MessageContext samlContext = this.contextProvider.buildContext(context);
        if (logoutEndpoint) {
            // SAML logout request/response
            this.logoutProfileHandler.receive(samlContext);
            return null;
        } else {
            // SAML authn response
            final SAML2Credentials credentials = (SAML2Credentials) this.profileHandler.receive(samlContext);
            return credentials;
        }
    }
}
