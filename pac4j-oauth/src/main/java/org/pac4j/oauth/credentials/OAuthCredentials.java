package org.pac4j.oauth.credentials;

import org.pac4j.core.credentials.Credentials;

/**
 * This class represents an OAuth credentials for OAuth 1.0 and 2.0: a request token, a token and a verifier.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public abstract class OAuthCredentials extends Credentials {

    private static final long serialVersionUID = -7705033802712382951L;

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();
}
