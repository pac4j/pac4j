package org.pac4j.http.credentials;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.TechnicalException;

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
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode
public class DigestCredentials extends TokenCredentials {

    private static final long serialVersionUID = -5130296967270874521L;
    @ToString.Include
    @Getter
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
    public String calculateServerDigest(boolean passwordAlreadyEncoded, String password) {
        return generateDigest(passwordAlreadyEncoded, username,
                realm, password, httpMethod, uri, qop, nonce, nc, cnonce);
    }

    /**
     * generate digest token based on RFC 2069 and RFC 2617 guidelines
     *
     * @return digest token
     */
    private String generateDigest(boolean passwordAlreadyEncoded, String username,
                                 String realm, String password, String httpMethod, String uri, String qop,
                                 String nonce, String nc, String cnonce) {
        String ha1;
        var a2 = httpMethod + ":" + uri;
        var ha2 = CredentialUtil.encryptMD5(a2);

        if (passwordAlreadyEncoded) {
            ha1 = password;
        } else {
            ha1 = CredentialUtil.encryptMD5(username + ":" + realm + ":" +password);
        }

        String digest;

        if (qop == null) {
            digest = CredentialUtil.encryptMD5(ha1, nonce + ":" + ha2);
        } else if ("auth".equals(qop)) {
            digest = CredentialUtil.encryptMD5(ha1, nonce + ":" + nc + ":" + cnonce + ":" + qop + ":" + ha2);
        } else {
            throw new TechnicalException("Invalid qop: '" + qop + "'");
        }

        return digest;
    }
}
