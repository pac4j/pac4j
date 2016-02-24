package org.pac4j.oidc.credentials;

import org.pac4j.core.credentials.Credentials;

import com.nimbusds.oauth2.sdk.AuthorizationCode;
import org.pac4j.core.util.CommonHelper;

/**
 * Credentials containing the authorization code sent by the OpenID Connect server.
 * 
 * @author Michael Remond
 * @since 1.7.0
 */
public class OidcCredentials extends Credentials {

    private static final long serialVersionUID = 6772331801527223938L;

    private AuthorizationCode code;

    public OidcCredentials(final AuthorizationCode code, final String clientName) {
        this.code = code;
        this.setClientName(clientName);
    }

    public AuthorizationCode getCode() {
        return this.code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final OidcCredentials that = (OidcCredentials) o;
        return !(code != null ? !code.equals(that.code) : that.code != null);

    }

    @Override
    public int hashCode() {
        return code != null ? code.hashCode() : 0;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "code", this.code, "clientName", getClientName());
    }
}
