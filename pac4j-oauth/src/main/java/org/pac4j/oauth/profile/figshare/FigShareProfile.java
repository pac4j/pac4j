package org.pac4j.oauth.profile.figshare;

import org.pac4j.oauth.profile.OAuth20Profile;

/**
 * FigShare profile.
 *
 * @author Vassilis Virvilis
 * @since 3.8.0
 */
public class FigShareProfile extends OAuth20Profile {
    public String getLastName() {
        return (String) getAttribute(FigShareProfileDefinition.LAST_NAME);
    }

    @Override
    public String getFamilyName() {
        return getLastName();
    }
}
