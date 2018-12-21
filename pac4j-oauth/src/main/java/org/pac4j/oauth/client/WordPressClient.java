package org.pac4j.oauth.client;

import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.oauth.profile.wordpress.WordPressProfileDefinition;
import org.pac4j.oauth.profile.wordpress.WordPressProfile;
import org.pac4j.scribe.builder.api.WordPressApi20;

/**
 * <p>This class is the OAuth client to authenticate users in WordPress.</p>
 * <p>It returns a {@link WordPressProfile}.</p>
 * <p>More information at http://developer.wordpress.com/docs/oauth2/</p>
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class WordPressClient extends OAuth20Client {

    public WordPressClient() {
    }

    public WordPressClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }

    @Override
    protected void clientInit() {
        configuration.setApi(new WordPressApi20());
        configuration.setProfileDefinition(new WordPressProfileDefinition());
        configuration.setTokenAsHeader(true);
        defaultLogoutActionBuilder((ctx, profile, targetUrl) ->
            new FoundAction("https://wordpress.com/wp-login.php?action=logout"));

        super.clientInit();
    }
}
