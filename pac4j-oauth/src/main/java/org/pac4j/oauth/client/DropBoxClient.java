package org.pac4j.oauth.client;

import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.oauth.profile.dropbox.DropBoxProfileCreator;
import org.pac4j.oauth.profile.dropbox.DropBoxProfileDefinition;
import org.pac4j.scribe.builder.api.DropboxApi20;

import java.util.Optional;

/**
 * <p>This class is the OAuth client to authenticate users in DropBox.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.dropbox.DropBoxProfile}.</p>
 * <p>More information at https://www.dropbox.com/developers/reference/api#account-info</p>
 *
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class DropBoxClient extends OAuth20Client {

    /**
     * <p>Constructor for DropBoxClient.</p>
     */
    public DropBoxClient() {
    }

    /**
     * <p>Constructor for DropBoxClient.</p>
     *
     * @param key a {@link java.lang.String} object
     * @param secret a {@link java.lang.String} object
     */
    public DropBoxClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        configuration.setApi(DropboxApi20.INSTANCE);
        configuration.setProfileDefinition(new DropBoxProfileDefinition());

        setLogoutActionBuilderIfUndefined((ctx, profile, targetUrl) ->
            Optional.of(HttpActionHelper.buildRedirectUrlAction(ctx.webContext(), "https://www.dropbox.com/logout")));
        setProfileCreatorIfUndefined(new DropBoxProfileCreator(configuration, this));

        super.internalInit(forceReinit);
    }
}
