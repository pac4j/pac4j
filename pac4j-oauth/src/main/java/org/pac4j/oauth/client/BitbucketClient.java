package org.pac4j.oauth.client;

import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.oauth.profile.bitbucket.BitbucketProfile;
import org.pac4j.oauth.profile.bitbucket.BitbucketProfileDefinition;
import org.pac4j.scribe.builder.api.BitBucketApi;

/**
 * This class is the OAuth client to authenticate users in Bitbucket.
 *
 * It returns a {@link BitbucketProfile}.
 *
 * @author Sebastian Sdorra
 * @since 1.5.1
 */
public class BitbucketClient extends OAuth10Client {

    public BitbucketClient() {
    }

    public BitbucketClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }

    @Override
    protected void clientInit() {
        configuration.setApi(new BitBucketApi());
        configuration.setProfileDefinition(new BitbucketProfileDefinition());
        defaultLogoutActionBuilder((ctx, profile, targetUrl) -> new FoundAction("https://bitbucket.org/account/signout/"));

        super.clientInit();
    }
}
