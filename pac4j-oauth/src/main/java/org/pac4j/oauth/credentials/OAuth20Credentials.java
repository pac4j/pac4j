package org.pac4j.oauth.credentials;

import org.pac4j.core.util.CommonHelper;

/**
 * This class represents an OAuth credentials for OAuth 2.0 an authorization code.
 *
 * @author zhangzhenli
 * @since 1.9.0
 */
public class OAuth20Credentials extends OAuthCredentials {

    private String code;

    /**
     * For OAuth2 Authorization Code Flow.
     *
     * @param code       the authorization code
     * @param clientName the client name.
     */
    public OAuth20Credentials(String code, String clientName) {
        super(clientName);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OAuth20Credentials that = (OAuth20Credentials) o;

        return code != null ? code.equals(that.code) : that.code == null;

    }

    @Override
    public int hashCode() {
        return code != null ? code.hashCode() : 0;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(),
                "code='", code,
                "clientName", getClientName());
    }
}
