package org.pac4j.oidc.profile.google;

import org.pac4j.oidc.profile.OidcProfile;

import java.io.Serial;

/**
 * <p>This class is the user profile for Google (using OpenID Connect protocol) with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oidc.client.GoogleOidcClient}.</p>
 *
 * @author Jerome Leleu
 * @version 1.9.0
 */
public class GoogleOidcProfile extends OidcProfile {

    @Serial
    private static final long serialVersionUID = -6076954328349948251L;
}
