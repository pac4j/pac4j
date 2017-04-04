package org.pac4j.oauth.client;

import com.github.scribejava.apis.GitHubApi;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.oauth.profile.github.GitHubProfile;
import org.pac4j.oauth.profile.github.GitHubProfileDefinition;

/**
 * <p>This class is the OAuth client to authenticate users in GitHub.</p>
 * <p>The <i>scope</i> can be defined to require specific permissions from the user by using the {@link #setScope(String)} method. By default,
 * the <i>scope</i> is: <code>user</code>.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.github.GitHubProfile}.</p>
 * <p>More information at http://developer.github.com/v3/users/</p>
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class GitHubClient extends OAuth20Client<GitHubProfile> {

    public final static String DEFAULT_SCOPE = "user";

    protected String scope = DEFAULT_SCOPE;

    public GitHubClient() {
    }

    public GitHubClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }

    @Override
    protected void clientInit(final WebContext context) {
        configuration.setApi(GitHubApi.instance());
        configuration.setProfileDefinition(new GitHubProfileDefinition());
        configuration.setScope(this.scope);
        setConfiguration(configuration);
        defaultLogoutActionBuilder((ctx, profile, targetUrl) -> RedirectAction.redirect("https://github.com/logout"));

        super.clientInit(context);
    }

    public String getScope() {
        return this.scope;
    }

    public void setScope(final String scope) {
        this.scope = scope;
    }
}
