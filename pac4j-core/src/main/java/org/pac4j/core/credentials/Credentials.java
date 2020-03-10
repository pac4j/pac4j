package org.pac4j.core.credentials;

import org.pac4j.core.profile.CommonProfile;

import java.io.Serializable;

/**
 * This class represents the base credentials.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public abstract class Credentials implements Serializable {

    private static final long serialVersionUID = 4864923514027378583L;

    private CommonProfile userProfile = null;

    public CommonProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(final CommonProfile userProfile) {
        this.userProfile = userProfile;
    }

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();
}
