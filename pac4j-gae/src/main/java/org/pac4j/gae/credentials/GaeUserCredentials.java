package org.pac4j.gae.credentials;

import org.pac4j.core.credentials.Credentials;

import com.google.appengine.api.users.User;
import org.pac4j.core.util.CommonHelper;

/**
 * Credential for Google App Engine.
 *
 * @author Patrice de Saint Steban
 * @since 1.6.0
 */
public class GaeUserCredentials extends Credentials {

    private static final long serialVersionUID = -135519596194113906L;

    private User user;

    public void setUser(final User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final GaeUserCredentials that = (GaeUserCredentials) o;

        return !(user != null ? !user.equals(that.user) : that.user != null);

    }

    @Override
    public int hashCode() {
        return user != null ? user.hashCode() : 0;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "user", this.user);
    }
}
