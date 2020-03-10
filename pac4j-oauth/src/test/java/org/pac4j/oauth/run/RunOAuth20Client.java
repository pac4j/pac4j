package org.pac4j.oauth.run;

import com.github.scribejava.apis.GitHubApi;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.oauth.client.GitHubClient;
import org.pac4j.oauth.client.OAuth20Client;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.profile.github.GitHubProfileDefinition;

/**
 * Run a manual test for the {@link GitHubClient}.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public final class RunOAuth20Client extends RunGithubClient {

    public static void main(final String[] args) {
        new RunOAuth20Client().run();
    }

    @Override
    protected IndirectClient getClient() {
        final OAuth20Configuration config = new OAuth20Configuration();
        config.setApi(GitHubApi.instance());
        config.setProfileDefinition(new GitHubProfileDefinition());
        config.setScope("user");
        config.setKey("62374f5573a89a8f9900");
        config.setSecret("01dd26d60447677ceb7399fb4c744f545bb86359");
        final OAuth20Client client = new OAuth20Client();
        client.setConfiguration(config);
        client.setCallbackUrl(PAC4J_BASE_URL);
        return client;
    }
}
