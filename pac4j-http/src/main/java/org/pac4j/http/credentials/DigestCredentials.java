package org.pac4j.http.credentials;

import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;

/**
 * <p>This credentials is retrieved from a HTTP request.</p>
 * <p>A user profile can be attached with the credentials if it has been created by a
 * {@link org.pac4j.core.credentials.authenticator.Authenticator}.
 * In that case, the {@link org.pac4j.core.profile.creator.AuthenticatorProfileCreator} must be used to retrieve the attached user profile.
 * </p>
 *
 * @author Mircea Carasel
 * @since 1.9.0
 */
public class DigestCredentials extends TokenCredentials {

    private static final long serialVersionUID = -5130296967270874521L;
    private String username;

    private String realm;
    private String nonce;
    private String uri;
    private String cnonce;
    private String nc;
    private String qop;

    private String httpMethod;

    /**
     * the token represents the client response attribute value in digest authorization header
     *
     * @param token the token
     * @param httpMethod the HTTP method
     * @param username the user name
     * @param realm the realm
     * @param nonce nonce
     * @param uri uri
     * @param cnonce cnonce
     * @param nc nc
     * @param qop qop
     */
    public DigestCredentials(final String token, final String httpMethod, final String username, final String realm,
                             final String nonce, final String uri, final String cnonce, final String nc,
                             final String qop) {
        super(token);

        this.username = username;
        this.realm = realm;
        this.nonce = nonce;
        this.uri = uri;
        this.cnonce = cnonce;
        this.nc = nc;
        this.qop = qop;
        this.httpMethod = httpMethod;
    }

    /**
     * This calculates the server digest value based on user stored password. If the server stores password in clear format
     * then passwordAlreadyEncoded should be false. If the server stores the password in ha1, digest then the
     * passwordAlreadyEncoded should be true.
     * @param passwordAlreadyEncoded false if the server stored password is in clear, true otherwise
     * @param password user password stored server-side
     * @return digest value. This value must match the client "response" value in the Authorization http header
     * for a successful digest authentication
     */
    public String calculateServerDigest(final boolean passwordAlreadyEncoded, final String password) {
        return generateDigest(passwordAlreadyEncoded, username,
                realm, password, httpMethod, uri, qop, nonce, nc, cnonce);
    }

    /**
     * generate digest token based on RFC 2069 and RFC 2617 guidelines
     *
     * @return digest token
     */
    private String generateDigest(final boolean passwordAlreadyEncoded, final String username,
                                  final String realm, final String password, final String httpMethod, final String uri, final String qop,
                                  final String nonce, final String nc, final String cnonce) {
        final String ha1;
        final String a2 = httpMethod + ":" + uri;
        final String ha2 = CredentialUtil.encryptMD5(a2);

        if (passwordAlreadyEncoded) {
            ha1 = password;
        } else {
            ha1 = CredentialUtil.encryptMD5(username + ":" + realm + ":" +password);
        }

        final String digest;

        if (qop == null) {
            digest = CredentialUtil.encryptMD5(ha1, nonce + ":" + ha2);
        } else if ("auth".equals(qop)) {
            digest = CredentialUtil.encryptMD5(ha1, nonce + ":" + nc + ":" + cnonce + ":" + qop + ":" + ha2);
        } else {
            throw new TechnicalException("Invalid qop: '" + qop + "'");
        }

        return digest;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        final DigestCredentials that = (DigestCredentials) o;

        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        if (realm != null ? !realm.equals(that.realm) : that.realm != null) return false;
        if (nonce != null ? !nonce.equals(that.nonce) : that.nonce != null) return false;
        if (uri != null ? !uri.equals(that.uri) : that.uri != null) return false;
        if (cnonce != null ? !cnonce.equals(that.cnonce) : that.cnonce != null) return false;
        if (nc != null ? !nc.equals(that.nc) : that.nc != null) return false;
        return (qop != null? qop.equals(that.qop) : that.qop == null)
            && !(httpMethod != null ? !httpMethod.equals(that.httpMethod) : that.httpMethod != null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (realm != null ? realm.hashCode() : 0);
        result = 31 * result + (nonce != null ? nonce.hashCode() : 0);
        result = 31 * result + (uri != null ? uri.hashCode() : 0);
        result = 31 * result + (cnonce != null ? cnonce.hashCode() : 0);
        result = 31 * result + (nc != null ? nc.hashCode() : 0);
        result = 31 * result + (qop != null ? qop.hashCode() : 0);
        result = 31 * result + (httpMethod != null ? httpMethod.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "username", this.username, "response", "[PROTECTED]");
    }
}
