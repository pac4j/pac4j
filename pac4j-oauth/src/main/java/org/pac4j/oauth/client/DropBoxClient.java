package org.pac4j.oauth.client;

import org.pac4j.core.exception.http.RedirectionActionHelper;
import org.pac4j.oauth.profile.dropbox.DropBoxProfileCreator;
import org.pac4j.oauth.profile.dropbox.DropBoxProfileDefinition;
import org.pac4j.oauth.profile.dropbox.DropBoxProfile;
import org.pac4j.scribe.builder.api.DropboxApi20;

import java.util.Optional;

/**
 * <p>This class is the OAuth client to authenticate users in DropBox.</p>
 * <p>It returns a {@link DropBoxProfile}.</p>
 * <p>More information at https://www.dropbox.com/developers/reference/api#account-info</p>
 *
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class DropBoxClient extends OAuth20Client {

    public DropBoxClient() {
    }

    public DropBoxClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }

    @Override
    protected void internalInit() {
        configuration.setApi(DropboxApi20.INSTANCE);
        configuration.setProfileDefinition(new DropBoxProfileDefinition());

        defaultLogoutActionBuilder((ctx, profile, targetUrl) ->
            Optional.of(RedirectionActionHelper.buildRedirectUrlAction(ctx, "https://www.dropbox.com/logout")));
        defaultProfileCreator(new DropBoxProfileCreator(configuration, this));

        super.internalInit();
    }
}
