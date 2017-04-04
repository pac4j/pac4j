package org.pac4j.oauth.client;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.oauth.profile.strava.StravaProfileDefinition;
import org.pac4j.oauth.profile.strava.StravaProfile;
import org.pac4j.scribe.builder.api.StravaApi20;

/**
 * <p>OAuth20Client for Strava.</p>
 * <p>Use the key as the client_id and secret as the client_secret, both provided by Strava at: <a href="https://www.strava.com/settings/api">https://www.strava.com/settings/api</a> </p>
 * <p>Set approvalPrompt to "force" if you want to force the authorization dialog to always display on Strava, otherwise let it to "auto" (default value). </p>
 * <p>More info at: <a href="http://strava.github.io/api/">http://strava.github.io/api/</a></p>
 *
 * @author Adrian Papusoi
 * @since 1.7.0
 */
public class StravaClient extends OAuth20Client<StravaProfile> {

    /**
     * comma delimited string of ‘view_private’ and/or ‘write’, leave blank for read-only permissions.
     */
    protected String scope = null;
    /**
     * approvalPrompt is by default "auto".   <br>
     * If "force", then the authorization dialog is always displayed by Strava.
     */
    private String approvalPrompt = "auto";

    public StravaClient() {
    }

    public StravaClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }

    @Override
    protected void clientInit(final WebContext context) {
        configuration.setApi(new StravaApi20(approvalPrompt));
        configuration.setProfileDefinition(new StravaProfileDefinition());
        configuration.setScope(this.scope);
        setConfiguration(configuration);
        defaultLogoutActionBuilder((ctx, profile, targetUrl) -> RedirectAction.redirect("https://www.strava.com/session"));

        super.clientInit(context);
    }

    public String getApprovalPrompt() {
        return approvalPrompt;
    }

    public void setApprovalPrompt(final String approvalPrompt) {
        this.approvalPrompt = approvalPrompt;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(final String scope) {
        this.scope = scope;
    }
}
