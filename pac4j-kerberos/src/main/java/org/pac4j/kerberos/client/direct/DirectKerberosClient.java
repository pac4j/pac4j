package org.pac4j.kerberos.client.direct;

import org.pac4j.core.client.DirectClient;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.kerberos.credentials.KerberosCredentials;
import org.pac4j.kerberos.credentials.extractor.KerberosExtractor;
import org.pac4j.kerberos.profile.KerberosProfile;

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

    public DirectKerberosClient(final Authenticator authenticator, final ProfileCreator<KerberosCredentials,
        KerberosProfile> profileCreator) {
        setAuthenticator(authenticator);
        setProfileCreator(profileCreator);
    }

    @Override
    protected void clientInit() {
        defaultCredentialsExtractor(new KerberosExtractor());
    }
}
