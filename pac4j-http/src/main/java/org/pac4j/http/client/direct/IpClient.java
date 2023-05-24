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

    /**
     * <p>Constructor for IpClient.</p>
     */
    public IpClient() {}

    /**
     * <p>Constructor for IpClient.</p>
     *
     * @param tokenAuthenticator a {@link Authenticator} object
     */
    public IpClient(final Authenticator tokenAuthenticator) {
        setAuthenticatorIfUndefined(tokenAuthenticator);
    }

    /**
     * <p>Constructor for IpClient.</p>
     *
     * @param tokenAuthenticator a {@link Authenticator} object
     * @param profileCreator a {@link ProfileCreator} object
     */
    public IpClient(final Authenticator tokenAuthenticator, final ProfileCreator profileCreator) {
        setAuthenticatorIfUndefined(tokenAuthenticator);
        setProfileCreatorIfUndefined(profileCreator);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        setCredentialsExtractorIfUndefined(new IpExtractor());
    }
}
