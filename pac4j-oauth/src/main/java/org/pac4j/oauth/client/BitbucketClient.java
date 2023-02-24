package org.pac4j.oauth.client;

import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.oauth.profile.bitbucket.BitbucketProfileDefinition;
import org.pac4j.scribe.builder.api.BitBucketApi;

import java.util.Optional;

/**
 * This class is the OAuth client to authenticate users in Bitbucket.
 *
 * It returns a {@link org.pac4j.oauth.profile.bitbucket.BitbucketProfile}.
 *
 * @author Sebastian Sdorra
 * @since 1.5.1
 */
public class BitbucketClient extends OAuth10Client {

    /**
     * <p>Constructor for BitbucketClient.</p>
     */
    public BitbucketClient() {
    }

    /**
     * <p>Constructor for BitbucketClient.</p>
     *
     * @param key a {@link java.lang.String} object
     * @param secret a {@link java.lang.String} object
     */
    public BitbucketClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        configuration.setApi(new BitBucketApi());
        configuration.setProfileDefinition(new BitbucketProfileDefinition());
        setLogoutActionBuilderIfUndefined((ctx, profile, targetUrl) ->
            Optional.of(HttpActionHelper.buildRedirectUrlAction(ctx.webContext(), "https://bitbucket.org/account/signout/")));

        super.internalInit(forceReinit);
    }
}
