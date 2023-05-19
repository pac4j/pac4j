package org.pac4j.kerberos.client.direct;

import org.pac4j.core.client.DirectClient;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.kerberos.credentials.extractor.KerberosExtractor;

import java.util.Optional;

/**
 * <p>This class is the client to authenticate users directly based on Kerberos ticket.
 * (i.e. being a direct client it do not redirects to callback URLs).</p>
 *
 * @author Garry Boyce
 * @since 2.1.0
 */
public class DirectKerberosClient extends DirectClient {

    /**
     * <p>Constructor for DirectKerberosClient.</p>
     */
    public DirectKerberosClient() {
    }

    /**
     * <p>Constructor for DirectKerberosClient.</p>
     *
     * @param authenticator a {@link Authenticator} object
     */
    public DirectKerberosClient(final Authenticator authenticator) {
        setAuthenticator(authenticator);
    }

    /**
     * <p>Constructor for DirectKerberosClient.</p>
     *
     * @param authenticator a {@link Authenticator} object
     * @param profileCreator a {@link ProfileCreator} object
     */
    public DirectKerberosClient(final Authenticator authenticator, final ProfileCreator profileCreator) {
        setAuthenticator(authenticator);
        setProfileCreator(profileCreator);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        setCredentialsExtractorIfUndefined(new KerberosExtractor());
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Credentials> getCredentials(final CallContext ctx) {
        // Set the WWW-Authenticate: Negotiate header in case no credentials are found
        // to trigger the SPNEGO process by replying with 401 Unauthorized
        ctx.webContext().setResponseHeader(HttpConstants.AUTHENTICATE_HEADER, "Negotiate");
        return super.getCredentials(ctx);
    }
}
