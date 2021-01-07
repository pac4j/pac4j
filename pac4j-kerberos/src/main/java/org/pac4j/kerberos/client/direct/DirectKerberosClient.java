package org.pac4j.kerberos.client.direct;

import java.util.Optional;

import org.pac4j.core.client.DirectClient;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.kerberos.credentials.extractor.KerberosExtractor;

/**
 * <p>This class is the client to authenticate users directly based on Kerberos ticket.
 * (i.e. being a direct client it do not redirects to callback URLs).</p>
 *
 * @author Garry Boyce
 * @since 2.1.0
 */
public class DirectKerberosClient extends DirectClient {

    public DirectKerberosClient() {
    }

    public DirectKerberosClient(final Authenticator authenticator) {
        setAuthenticator(authenticator);
    }

    public DirectKerberosClient(final Authenticator authenticator, final ProfileCreator profileCreator) {
        setAuthenticator(authenticator);
        setProfileCreator(profileCreator);
    }

    @Override
    protected void internalInit() {
        defaultCredentialsExtractor(new KerberosExtractor());
    }

    @Override
    protected Optional<Credentials> retrieveCredentials(final WebContext context, final SessionStore sessionStore) {
        // Set the WWW-Authenticate: Negotiate header in case no credentials are found
        // to trigger the SPNEGO process by replying with 401 Unauthorized
        context.setResponseHeader(HttpConstants.AUTHENTICATE_HEADER, "Negotiate");
        return super.retrieveCredentials(context, sessionStore);
    }

}
