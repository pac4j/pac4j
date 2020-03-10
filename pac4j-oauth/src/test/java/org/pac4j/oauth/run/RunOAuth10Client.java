package org.pac4j.oauth.run;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.oauth.client.OAuth10Client;
import org.pac4j.oauth.config.OAuth10Configuration;
import org.pac4j.oauth.profile.bitbucket.BitbucketProfileDefinition;
import org.pac4j.scribe.builder.api.BitBucketApi;

/**
 * Run a manual test for the {@link OAuth10Client}.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public final class RunOAuth10Client extends RunBitbucketClient {

    public static void main(final String[] args) {
        new RunOAuth10Client().run();
    }

    @Override
    protected IndirectClient getClient() {
        final OAuth10Configuration config = new OAuth10Configuration();
        config.setKey("bjEt8BMpLwFDqZUvp6");
        config.setSecret("NN6fVXRTcV2qYVejVLZqxBRqHgn3ygD4");
        config.setApi(new BitBucketApi());
        config.setProfileDefinition(new BitbucketProfileDefinition());
        final OAuth10Client client = new OAuth10Client();
        client.setCallbackUrl(PAC4J_BASE_URL);
        client.setConfiguration(config);
        return client;
    }
}
