package org.pac4j.oauth.client;

import java.util.Optional;

import org.pac4j.core.exception.http.RedirectionActionHelper;

import org.pac4j.oauth.profile.figshare.FigShareProfileCreator;
import org.pac4j.oauth.profile.figshare.FigShareProfileDefinition;
import org.pac4j.scribe.builder.api.FigShareApi20;

/**
 * <p>This class is the OAuth client to authenticate users in FigShare (using OAuth 2.0 protocol).</p>
 * <p>It returns a {@link com.biovista.org.pac4j.oauth.profile.figshare.FigShareProfile}.</p>
 * <p>More information at https://docs.figshare.com/old_docs/oauth/</p>
 *
 * @author Vassilis Virivilis
 * @since 3.8
 */
public class FigShareClient extends OAuth20Client {
    @Override
    protected void clientInit() {
        final FigShareApi20 api = new FigShareApi20();
        configuration.setApi(api);

        final FigShareProfileDefinition profileDefinition = new FigShareProfileDefinition();

        profileDefinition.setProfileId("id");

        configuration.setProfileDefinition(profileDefinition);
        configuration.setScope("all");
        configuration.setWithState(true);

        defaultProfileCreator(new FigShareProfileCreator(configuration, this));
        defaultLogoutActionBuilder((ctx, profile, targetUrl) -> Optional
            .of(RedirectionActionHelper.buildRedirectUrlAction(ctx, "https://figshare.com/account/logout")));

        super.clientInit();
    }
}
