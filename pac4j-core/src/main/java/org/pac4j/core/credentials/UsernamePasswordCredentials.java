package org.pac4j.core.credentials;

import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.util.CommonHelper;

/**
 * This class represents a username and a password credentials
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class UsernamePasswordCredentials extends Credentials {

    private static final long serialVersionUID = -7229878989627796565L;

    private String username;

    private String password;

    public UsernamePasswordCredentials(final String username, final String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final UsernamePasswordCredentials that = (UsernamePasswordCredentials) o;

        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        return !(password != null ? !password.equals(that.password) : that.password != null);
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), Pac4jConstants.USERNAME, this.username,
                Pac4jConstants.PASSWORD, "[PROTECTED]");
    }
}
