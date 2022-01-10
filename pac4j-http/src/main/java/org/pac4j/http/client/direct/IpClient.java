package org.pac4j.http.client.direct;

import org.pac4j.core.client.DirectClient;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.http.credentials.extractor.IpExtractor;

/**
 * <p>This class is the client to authenticate users directly based on their IP address.</p>
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class IpClient extends DirectClient {

    public IpClient() {}

    public IpClient(final Authenticator tokenAuthenticator) {
        defaultAuthenticator(tokenAuthenticator);
    }

    public IpClient(final Authenticator tokenAuthenticator, final ProfileCreator profileCreator) {
        defaultAuthenticator(tokenAuthenticator);
        defaultProfileCreator(profileCreator);
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        defaultCredentialsExtractor(new IpExtractor());
    }
}
