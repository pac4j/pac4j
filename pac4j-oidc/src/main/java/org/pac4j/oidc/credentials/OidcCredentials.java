package org.pac4j.oidc.credentials;

import com.nimbusds.jwt.JWT;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import org.pac4j.core.credentials.Credentials;

import com.nimbusds.oauth2.sdk.AuthorizationCode;
import org.pac4j.core.util.CommonHelper;

/**
 * Credentials containing the authorization code sent by the OpenID Connect server.
 *
 * @author Michael Remond
 * @author Jerome Leleu
 * @since 1.7.0
 */
public class OidcCredentials extends Credentials {

    private static final long serialVersionUID = 6772331801527223938L;

    private AuthorizationCode code;
    private AccessToken accessToken;
    private RefreshToken refreshToken;
    private JWT idToken;

    public AuthorizationCode getCode() {
        return code;
    }

    public void setCode(final AuthorizationCode code) {
        this.code = code;
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(final AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public RefreshToken getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(final RefreshToken refreshToken) {
        this.refreshToken = refreshToken;
    }

    public JWT getIdToken() {
        return idToken;
    }

    public void setIdToken(final JWT idToken) {
        this.idToken = idToken;
    }

    @Override
    public boolean equals(final Object o) {
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
        return CommonHelper.toNiceString(this.getClass(), "code", this.code, "accessToken", accessToken,
                "refreshToken", refreshToken, "idToken", idToken);
    }
}
