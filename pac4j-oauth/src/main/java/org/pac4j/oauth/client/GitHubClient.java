package org.pac4j.oauth.client;

import com.github.scribejava.apis.GitHubApi;
import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.oauth.profile.github.GitHubProfileDefinition;

import java.util.Optional;

/**
 * <p>This class is the OAuth client to authenticate users in GitHub.</p>
 * <p>The <i>scope</i> can be defined to require specific permissions from the user by using the {@link #setScope(String)} method.
 * By default, the <i>scope</i> is: <code>user</code>.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.github.GitHubProfile}.</p>
 * <p>More information at http://developer.github.com/v3/users/</p>
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class GitHubClient extends OAuth20Client {

    /** Constant <code>DEFAULT_SCOPE="user"</code> */
    public static final String DEFAULT_SCOPE = "user";

    /**
     * <p>Constructor for GitHubClient.</p>
     */
    public GitHubClient() {
        setScope(DEFAULT_SCOPE);
    }

    /**
     * <p>Constructor for GitHubClient.</p>
     *
     * @param key a {@link String} object
     * @param secret a {@link String} object
     */
    public GitHubClient(final String key, final String secret) {
        setScope(DEFAULT_SCOPE);
        setKey(key);
        setSecret(secret);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        configuration.setApi(GitHubApi.instance());
        configuration.setProfileDefinition(new GitHubProfileDefinition());
        configuration.setTokenAsHeader(true);
        setLogoutActionBuilderIfUndefined((ctx, profile, targetUrl) ->
            Optional.of(HttpActionHelper.buildRedirectUrlAction(ctx.webContext(), "https://github.com/logout")));

        super.internalInit(forceReinit);
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
