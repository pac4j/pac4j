package org.pac4j.http.client.direct;

import org.pac4j.core.client.DirectClientV2;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.authenticator.UsernamePasswordAuthenticator;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.credentials.extractor.BasicAuthExtractor;
import org.pac4j.core.profile.creator.ProfileCreator;

/**
 * <p>This class is the client to authenticate users directly through HTTP basic auth.</p>
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class DirectBasicAuthClient extends DirectClientV2<UsernamePasswordCredentials, CommonProfile> {

    public DirectBasicAuthClient() {}

    public DirectBasicAuthClient(final Authenticator usernamePasswordAuthenticator) {
        setAuthenticator(usernamePasswordAuthenticator);
    }

    public DirectBasicAuthClient(final Authenticator usernamePasswordAuthenticator,
                                 final ProfileCreator profileCreator) {
        setAuthenticator(usernamePasswordAuthenticator);
        setProfileCreator(profileCreator);
    }

    @Override
    protected void internalInit(final WebContext context) {
        setCredentialsExtractor(new BasicAuthExtractor(getName()));
        super.internalInit(context);
        assertAuthenticatorTypes(UsernamePasswordAuthenticator.class);
    }
}
