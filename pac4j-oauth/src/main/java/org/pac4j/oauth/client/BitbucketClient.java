package org.pac4j.oauth.client;

import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.oauth.profile.bitbucket.BitbucketProfile;
import org.pac4j.oauth.profile.bitbucket.BitbucketProfileDefinition;
import org.pac4j.scribe.builder.api.BitBucketApi;

import java.util.Optional;

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
    protected void internalInit(final boolean forceReinit) {
        configuration.setApi(new BitBucketApi());
        configuration.setProfileDefinition(new BitbucketProfileDefinition());
        defaultLogoutActionBuilder((ctx, session, profile, targetUrl) ->
            Optional.of(HttpActionHelper.buildRedirectUrlAction(ctx, "https://bitbucket.org/account/signout/")));

        super.internalInit(forceReinit);
    }
}
