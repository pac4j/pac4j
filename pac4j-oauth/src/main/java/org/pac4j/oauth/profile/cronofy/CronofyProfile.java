package org.pac4j.oauth.profile.cronofy;

import org.pac4j.oauth.profile.OAuth20Profile;

import java.io.Serial;

/**
 * <p>This class is the user profile for Cronofy with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.CronofyClient}.</p>
 *
 * @author Jerome Leleu
 * @since 5.3.1
 */
public class CronofyProfile extends OAuth20Profile {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * <p>getAccountId.</p>
     *
     * @return a {@link String} object
     */
    public String getAccountId() {
        return (String) getAttribute(CronofyProfileDefinition.ACCOUNT_ID);
    }

    /**
     * <p>getProviderName.</p>
     *
     * @return a {@link String} object
     */
    public String getProviderName() {
        return (String) getAttribute(CronofyProfileDefinition.PROVIDER_NAME);
    }

    /**
     * <p>getProfileId.</p>
     *
     * @return a {@link String} object
     */
    public String getProfileId() {
        return (String) getAttribute(CronofyProfileDefinition.PROFILE_ID);
    }

    /**
     * <p>getProfileName.</p>
     *
     * @return a {@link String} object
     */
    public String getProfileName() {
        return (String) getAttribute(CronofyProfileDefinition.PROFILE_NAME);
    }

    /**
     * <p>getProviderService.</p>
     *
     * @return a {@link String} object
     */
    public String getProviderService() {
        return (String) getAttribute(CronofyProfileDefinition.PROVIDER_SERVICE);
    }
}
