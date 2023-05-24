package org.pac4j.oauth.client;

import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.oauth.profile.strava.StravaProfileDefinition;
import org.pac4j.scribe.builder.api.StravaApi20;

import java.util.Optional;

/**
 * <p>OAuth20Client for Strava.</p>
 * <p>Use the key as the client_id and secret as the client_secret, both provided by Strava at:
 * <a href="https://www.strava.com/settings/api">https://www.strava.com/settings/api</a> </p>
 * <p>Set approvalPrompt to "force" if you want to force the authorization dialog to always display on Strava,
 * otherwise let it to "auto" (default value).</p>
 * <p>The scope is a comma delimited string of ‘view_private’ and/or ‘write’, leave blank for read-only permissions.</p>
 * <p>More info at: <a href="http://strava.github.io/api/">http://strava.github.io/api/</a></p>
 *
 * @author Adrian Papusoi
 * @since 1.7.0
 */
public class StravaClient extends OAuth20Client {

    /**
     * approvalPrompt is by default "auto".   <br>
     * If "force", then the authorization dialog is always displayed by Strava.
     */
    private String approvalPrompt = "auto";

    /**
     * <p>Constructor for StravaClient.</p>
     */
    public StravaClient() {
    }

    /**
     * <p>Constructor for StravaClient.</p>
     *
     * @param key a {@link String} object
     * @param secret a {@link String} object
     */
    public StravaClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        configuration.setApi(new StravaApi20(approvalPrompt));
        configuration.setProfileDefinition(new StravaProfileDefinition());
        setLogoutActionBuilderIfUndefined((ctx, profile, targetUrl) ->
            Optional.of(HttpActionHelper.buildRedirectUrlAction(ctx.webContext(), "https://www.strava.com/session")));

        super.internalInit(forceReinit);
    }

    /**
     * <p>Getter for the field <code>approvalPrompt</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getApprovalPrompt() {
        return approvalPrompt;
    }

    /**
     * <p>Setter for the field <code>approvalPrompt</code>.</p>
     *
     * @param approvalPrompt a {@link String} object
     */
    public void setApprovalPrompt(final String approvalPrompt) {
        this.approvalPrompt = approvalPrompt;
    }

    /**
     * <p>getScope.</p>
     *
     * @return a {@link String} object
     */
    public String getScope() {
        return getConfiguration().getScope();
    }

    /**
     * <p>setScope.</p>
     *
     * @param scope a {@link String} object
     */
    public void setScope(final String scope) {
        getConfiguration().setScope(scope);
    }
}
