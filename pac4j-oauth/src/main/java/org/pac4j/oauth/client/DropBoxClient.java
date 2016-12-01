package org.pac4j.oauth.client;

import org.pac4j.core.context.WebContext;
import org.pac4j.oauth.profile.dropbox.DropBoxProfileDefinition;
import org.pac4j.oauth.profile.dropbox.DropBoxProfile;
import org.pac4j.scribe.builder.api.DropboxApi20;

/**
 * <p>This class is the OAuth client to authenticate users in DropBox.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.dropbox.DropBoxProfile}.</p>
 * <p>More information at https://www.dropbox.com/developers/reference/api#account-info</p>
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class DropBoxClient extends OAuth20Client<DropBoxProfile> {
    
    public DropBoxClient() {
    }
    
    public DropBoxClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }

    @Override
    protected void internalInit(final WebContext context) {
        configuration.setApi(DropboxApi20.INSTANCE);
        configuration.setProfileDefinition(new DropBoxProfileDefinition());
        configuration.setHasGrantType(true);
        setConfiguration(configuration);

        super.internalInit(context);
    }
}
