package org.pac4j.core.credentials;

import org.pac4j.core.util.CommonHelper;

/**
 * This credentials represents a token.
 *
 * @author Jerome Leleu
 * @since 1.7.0
 */
public class TokenCredentials extends Credentials {

    private static final long serialVersionUID = -4270718634364817595L;

    private String token;

    public TokenCredentials(final String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final TokenCredentials that = (TokenCredentials) o;

        return !(token != null ? !token.equals(that.token) : that.token != null);
    }

    @Override
    public int hashCode() {
        return token != null ? token.hashCode() : 0;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "token", this.token);
    }
}
