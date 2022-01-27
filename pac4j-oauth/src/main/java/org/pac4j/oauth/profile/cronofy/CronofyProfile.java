package org.pac4j.oauth.profile.cronofy;

import org.pac4j.oauth.profile.OAuth20Profile;

/**
 * <p>This class is the user profile for Cronofy with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.CronofyClient}.</p>
 *
 * @author Jerome Leleu
 * @since 5.3.1
 */
public class CronofyProfile extends OAuth20Profile {

    private static final long serialVersionUID = 1L;

    public String getAccountId() {
        return (String) getAttribute(CronofyProfileDefinition.ACCOUNT_ID);
    }

    public String getProviderName() {
        return (String) getAttribute(CronofyProfileDefinition.PROVIDER_NAME);
    }

    public String getProfileId() {
        return (String) getAttribute(CronofyProfileDefinition.PROFILE_ID);
    }

    public String getProfileName() {
        return (String) getAttribute(CronofyProfileDefinition.PROFILE_NAME);
    }

    public String getProviderService() {
        return (String) getAttribute(CronofyProfileDefinition.PROVIDER_SERVICE);
    }
}
