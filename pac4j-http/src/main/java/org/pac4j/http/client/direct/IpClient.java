package org.pac4j.http.client.direct;

import org.pac4j.core.client.DirectClientV2;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.http.credentials.extractor.IpExtractor;

/**
 * <p>This class is the client to authenticate users directly based on their IP address.</p>
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class IpClient extends DirectClientV2<TokenCredentials, CommonProfile> {

    public IpClient() {}

    public IpClient(final Authenticator tokenAuthenticator) {
        setAuthenticator(tokenAuthenticator);
    }

    public IpClient(final Authenticator tokenAuthenticator, final ProfileCreator profileCreator) {
        setAuthenticator(tokenAuthenticator);
        setProfileCreator(profileCreator);
    }

    @Override
    protected void internalInit(final WebContext context) {
        setCredentialsExtractor(new IpExtractor(getName()));
    }
}
