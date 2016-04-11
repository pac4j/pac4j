package org.pac4j.oidc.client;

import org.pac4j.oidc.profile.GoogleOidcProfile;

/**
 * <p>This class is the OpenID Connect client to authenticate users in Google.</p>
 * <p>More information at: https://developers.google.com/identity/protocols/OpenIDConnect</p>
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class GoogleOidcClient extends OidcClient<GoogleOidcProfile> {

    public GoogleOidcClient() {
        setDiscoveryURI("https://accounts.google.com/.well-known/openid-configuration");
    }

    @Override
    protected GoogleOidcProfile createProfile() {
        return new GoogleOidcProfile();
    }
}
