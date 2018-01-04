package org.pac4j.oauth.client;

import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.oauth.profile.bitbucket.BitbucketProfile;
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
public class BitbucketClient extends OAuth10Client<BitbucketProfile> {

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
        defaultLogoutActionBuilder((ctx, profile, targetUrl) ->
            Optional.of(RedirectAction.redirect("https://bitbucket.org/account/signout/"))
        );

        super.clientInit();
    }
}
