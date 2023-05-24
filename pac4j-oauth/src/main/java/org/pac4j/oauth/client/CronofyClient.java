package org.pac4j.oauth.client;

import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.oauth.profile.cronofy.CronofyProfileCreator;
import org.pac4j.oauth.profile.cronofy.CronofyProfileDefinition;
import org.pac4j.scribe.builder.api.CronofyApi20;

/**
 * <p>This class is the OAuth client to authenticate users with Cronofy using the OAuth protocol version 2.0.</p>
 *
 * @author Jerome LELEU
 * @since 5.3.1
 */
public class CronofyClient extends OAuth20Client {

    private String sdkIdentifier = Pac4jConstants.EMPTY_STRING;

    private String scope = "read_free_busy";

    /**
     * <p>Constructor for CronofyClient.</p>
     */
    public CronofyClient() {
    }

    /**
     * <p>Constructor for CronofyClient.</p>
     *
     * @param key a {@link String} object
     * @param secret a {@link String} object
     */
    public CronofyClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        configuration.setApi(new CronofyApi20(sdkIdentifier));
        configuration.setProfileDefinition(new CronofyProfileDefinition());
        configuration.setScope(scope);
        configuration.setWithState(true);
        setProfileCreatorIfUndefined(new CronofyProfileCreator(configuration, this));

        super.internalInit(forceReinit);
    }

    /**
     * <p>Getter for the field <code>scope</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getScope() {
        return scope;
    }

    /**
     * <p>Setter for the field <code>scope</code>.</p>
     *
     * @param scope a {@link String} object
     */
    public void setScope(final String scope) {
        this.scope = scope;
    }

    /**
     * <p>Getter for the field <code>sdkIdentifier</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getSdkIdentifier() {
        return sdkIdentifier;
    }

    /**
     * <p>Setter for the field <code>sdkIdentifier</code>.</p>
     *
     * @param sdkIdentifier a {@link String} object
     */
    public void setSdkIdentifier(final String sdkIdentifier) {
        this.sdkIdentifier = sdkIdentifier;
    }
}
