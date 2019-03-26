package org.pac4j.kerberos.client.direct;

import java.util.Optional;

import org.pac4j.core.client.DirectClient;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.kerberos.credentials.KerberosCredentials;
import org.pac4j.kerberos.credentials.extractor.KerberosExtractor;

/**
 * <p>This class is the client to authenticate users directly based on Kerberos ticket.
 * (i.e. being a direct client it do not redirects to callback URLs).</p>
 *
 * @author Garry Boyce
 * @since 2.1.0
 */
public class DirectKerberosClient extends DirectClient<KerberosCredentials> {

    public DirectKerberosClient() {
    }

    public DirectKerberosClient(final Authenticator authenticator) {
        setAuthenticator(authenticator);
    }

    public DirectKerberosClient(final Authenticator authenticator, final ProfileCreator<KerberosCredentials> profileCreator) {
        setAuthenticator(authenticator);
        setProfileCreator(profileCreator);
    }

    @Override
    protected void clientInit() {
        defaultCredentialsExtractor(new KerberosExtractor());
    }

    @Override
    protected Optional<KerberosCredentials> retrieveCredentials(WebContext context) {
        // Set the WWW-Authenticate: Negotiate header in case no credentials are found
        // to trigger the SPNEGO process by replying with 401 Unauthorized
        context.setResponseHeader(HttpConstants.AUTHENTICATE_HEADER, "Negotiate");
        return super.retrieveCredentials(context);
    }

}
