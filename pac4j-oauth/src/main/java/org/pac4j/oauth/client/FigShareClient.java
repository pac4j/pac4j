package org.pac4j.oauth.client;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.oauth.config.OAuth20Configuration;

import org.pac4j.oauth.profile.figshare.FigShareProfile;
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
public class FigShareClient extends GenericOAuth20Client<FigShareProfile> {
    public FigShareClient() {
        super(new BiFunction<String, String, FigShareApi20>() {
            @Override
            public FigShareApi20 apply(String authUrl, String tokenUrl) {
                return new FigShareApi20(authUrl, tokenUrl);
            }
        }, new Supplier<FigShareProfileDefinition>() {
            @Override
            public FigShareProfileDefinition get() {
                return new FigShareProfileDefinition();
            }
        }, new BiFunction<OAuth20Configuration, IndirectClient, FigShareProfileCreator>() {
            @Override
            public FigShareProfileCreator apply(OAuth20Configuration configuration, IndirectClient client) {
                return new FigShareProfileCreator(configuration, client);
            }
        }, new Supplier<String>() {
            @Override
            public String get() {
                return "https://figshare.com/account/logout";
            }
        });

        setAuthUrl("https://figshare.com/account/applications/authorize");
        setTokenUrl("https://api.figshare.com/v2/token");
        setProfileUrl("https://api.figshare.com/v2/account");
        setProfileId("id");
        setScope("all");
        setWithState(true);
    }
}
