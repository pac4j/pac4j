package org.pac4j.oauth.client;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.oauth.profile.bitbucket.BitbucketProfile;
import org.pac4j.oauth.profile.bitbucket.BitbucketProfileDefinition;
import org.pac4j.scribe.builder.api.BitBucketApi;

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
    protected void clientInit(final WebContext context) {
        configuration.setApi(new BitBucketApi());
        configuration.setProfileDefinition(new BitbucketProfileDefinition());
        setConfiguration(configuration);
        defaultLogoutActionBuilder((ctx, profile, targetUrl) -> RedirectAction.redirect("https://bitbucket.org/account/signout/"));

        super.clientInit(context);
    }
}
