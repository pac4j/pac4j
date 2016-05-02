package org.pac4j.oauth.credentials;

import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.util.CommonHelper;

/**
 * This class represents an OAuth credentials for OAuth 1.0 &amp; 2.0 : a request token, a token and a verifier.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public abstract class OAuthCredentials extends Credentials {

    private static final long serialVersionUID = -7705033802712382951L;

    public OAuthCredentials(final String clientName) {
        setClientName(clientName);
    }

    public abstract boolean equals(Object o);

    public abstract int hashCode();

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "clientName", getClientName());
    }
}
