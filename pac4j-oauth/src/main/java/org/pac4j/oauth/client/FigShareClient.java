package org.pac4j.oauth.client;

import lombok.val;
import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.oauth.profile.figshare.FigShareProfileCreator;
import org.pac4j.oauth.profile.figshare.FigShareProfileDefinition;
import org.pac4j.scribe.builder.api.FigShareApi20;

import java.util.Optional;

/**
 * <p>This class is the OAuth client to authenticate users in FigShare (using OAuth 2.0 protocol).</p>
 * <p>It returns a {@link FigShareProfileDefinition}.</p>
 * <p>More information at https://docs.figshare.com/old_docs/oauth/</p>
 *
 * @author Vassilis Virivilis
 * @since 3.8
 */
public class FigShareClient extends OAuth20Client {

    @Override
    protected void internalInit(final boolean forceReinit) {
        val api = new FigShareApi20();
        configuration.setApi(api);

        val profileDefinition = new FigShareProfileDefinition();

        profileDefinition.setProfileId("id");

        configuration.setProfileDefinition(profileDefinition);
        configuration.setScope("all");
        configuration.setWithState(true);

        setProfileCreatorIfUndefined(new FigShareProfileCreator(configuration, this));
        setLogoutActionBuilderIfUndefined((ctx, store, profile, targetUrl) -> Optional
            .of(HttpActionHelper.buildRedirectUrlAction(ctx, "https://figshare.com/account/logout")));

        super.internalInit(forceReinit);
    }
}
