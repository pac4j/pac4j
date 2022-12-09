package org.pac4j.oauth.client;

import com.github.scribejava.apis.GitHubApi;
import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.oauth.profile.github.GitHubProfile;
import org.pac4j.oauth.profile.github.GitHubProfileDefinition;

import java.util.Optional;

/**
 * <p>This class is the OAuth client to authenticate users in GitHub.</p>
 * <p>The <i>scope</i> can be defined to require specific permissions from the user by using the {@link #setScope(String)} method.
 * By default, the <i>scope</i> is: <code>user</code>.</p>
 * <p>It returns a {@link GitHubProfile}.</p>
 * <p>More information at http://developer.github.com/v3/users/</p>
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class GitHubClient extends OAuth20Client {

    public static final String DEFAULT_SCOPE = "user";

    public GitHubClient() {
        setScope(DEFAULT_SCOPE);
    }

    public GitHubClient(final String key, final String secret) {
        setScope(DEFAULT_SCOPE);
        setKey(key);
        setSecret(secret);
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        configuration.setApi(GitHubApi.instance());
        configuration.setProfileDefinition(new GitHubProfileDefinition());
        configuration.setTokenAsHeader(true);
        setLogoutActionBuilderIfUndefined((ctx, session, profile, targetUrl) ->
            Optional.of(HttpActionHelper.buildRedirectUrlAction(ctx, "https://github.com/logout")));

        super.internalInit(forceReinit);
    }

    public String getScope() {
        return getConfiguration().getScope();
    }

    public void setScope(final String scope) {
        getConfiguration().setScope(scope);
    }
}
