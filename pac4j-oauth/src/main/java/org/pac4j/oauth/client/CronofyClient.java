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

    public CronofyClient() {
    }

    public CronofyClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        configuration.setApi(new CronofyApi20(sdkIdentifier));
        configuration.setProfileDefinition(new CronofyProfileDefinition());
        configuration.setScope(scope);
        configuration.setWithState(true);
        setProfileCreatorIfUndefined(new CronofyProfileCreator(configuration, this));

        super.internalInit(forceReinit);
    }

    public String getScope() {
        return scope;
    }

    public void setScope(final String scope) {
        this.scope = scope;
    }

    public String getSdkIdentifier() {
        return sdkIdentifier;
    }

    public void setSdkIdentifier(final String sdkIdentifier) {
        this.sdkIdentifier = sdkIdentifier;
    }
}
