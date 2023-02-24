package org.pac4j.oauth.profile.figshare;

import org.pac4j.oauth.profile.OAuth20Profile;

/**
 * FigShare profile.
 *
 * @author Vassilis Virvilis
 * @since 3.8.0
 */
public class FigShareProfile extends OAuth20Profile {
    /**
     * <p>getLastName.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getLastName() {
        return (String) getAttribute(FigShareProfileDefinition.LAST_NAME);
    }

    /** {@inheritDoc} */
    @Override
    public String getFamilyName() {
        return getLastName();
    }
}
