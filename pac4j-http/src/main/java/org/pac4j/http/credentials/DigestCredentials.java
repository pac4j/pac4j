package org.pac4j.http.credentials;

import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.util.CommonHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * <p>This credentials is retrieved from a HTTP request.</p>
 * <p>A user profile can be attached with the credentials if it has been created by a {@link org.pac4j.http.credentials.authenticator.Authenticator}.
 * In that case, the {@link org.pac4j.http.profile.creator.AuthenticatorProfileCreator} must be used to retrieve the attached user profile.</p>
 *
 * @author Mircea Carasel
 */
public class DigestCredentials extends TokenCredentials {

    private String username;
    private String response;

    private String realm;
    private String nonce;
    private String uri;
    private String cnonce;
    private String nc;
    private String qop;

    private String httpMethod;

    public DigestCredentials(final String token, final String httpMethod, final String clientName) {
        super(token, clientName);
        Map<String, String> valueMap = parseTokenValue(token);

        username = valueMap.get("username");
        response = valueMap.get("response");

        if (CommonHelper.isBlank(username) || CommonHelper.isBlank(response)) {
            throw new CredentialsException("Bad format of the digest auth header");
        }
        realm = valueMap.get("realm");
        nonce = valueMap.get("nonce");
        uri = valueMap.get("uri");
        cnonce = valueMap.get("cnonce");
        nc = valueMap.get("nc");
        qop = valueMap.get("qop");
        this.httpMethod = httpMethod;
    }

    public String getUsername() {
        return username;
    }

    public String getResponse() {
        return response;
    }

    public String calculateServerDigest(boolean passwordAlreadyEncoded, String password) {
        return generateDigest(passwordAlreadyEncoded, username,
                realm, password, httpMethod, uri, qop, nonce, nc, cnonce);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DigestCredentials that = (DigestCredentials) o;

        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        return !(response != null ? !response.equals(that.response) : that.response != null);
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (response != null ? response.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "username", this.username, "response", "[PROTECTED]",
                "clientName", getClientName());
    }

    private Map<String, String> parseTokenValue(String token) {
        StringTokenizer tokenizer = new StringTokenizer(token, ", ");
        String keyval;
        Map map = new HashMap<String, String>();
        while (tokenizer.hasMoreElements()) {
            keyval = tokenizer.nextToken();
            if (keyval.contains("=")) {
                String key = keyval.substring(0, keyval.indexOf("="));
                String value = keyval.substring(keyval.indexOf("=") + 1);
                map.put(key.trim(), value.replaceAll("\"", "").trim());
            }
        }
        return map;
    }

    private String generateDigest(boolean passwordAlreadyEncoded, String username,
                                 String realm, String password, String httpMethod, String uri, String qop,
                                 String nonce, String nc, String cnonce) throws IllegalArgumentException {
        String ha1;
        String a2 = httpMethod + ":" + uri;
        String ha2 = CredentialUtil.H(a2);

        if (passwordAlreadyEncoded) {
            ha1 = password;
        }
        else {
            ha1 = CredentialUtil.H(username + ":" + realm + ":" +password);
        }

        String digest;

        if (qop == null) {
            // as per RFC 2069 compliant clients (also reaffirmed by RFC 2617)
            digest = CredentialUtil.KD(ha1, nonce + ":" + ha2);
        }
        else if ("auth".equals(qop)) {
            // As per RFC 2617 compliant clients
            digest = CredentialUtil.KD(ha1, nonce + ":" + nc + ":" + cnonce + ":" + qop + ":" + ha2);
        }
        else {
            throw new IllegalArgumentException("Invalid qop: '"
                    + qop + "'");
        }

        return digest;
    }
}
