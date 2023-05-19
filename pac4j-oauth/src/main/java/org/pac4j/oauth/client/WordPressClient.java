package org.pac4j.oauth.client;

import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.oauth.profile.wordpress.WordPressProfileDefinition;
import org.pac4j.scribe.builder.api.WordPressApi20;

import java.util.Optional;

/**
 * <p>This class is the OAuth client to authenticate users in WordPress.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.wordpress.WordPressProfile}.</p>
 * <p>More information at http://developer.wordpress.com/docs/oauth2/</p>
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class WordPressClient extends OAuth20Client {

    /**
     * <p>Constructor for WordPressClient.</p>
     */
    public WordPressClient() {
    }

    /**
     * <p>Constructor for WordPressClient.</p>
     *
     * @param key a {@link String} object
     * @param secret a {@link String} object
     */
    public WordPressClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        configuration.setApi(new WordPressApi20());
        configuration.setProfileDefinition(new WordPressProfileDefinition());
        configuration.setTokenAsHeader(true);
        setLogoutActionBuilderIfUndefined((ctx, profile, targetUrl) ->
            Optional.of(HttpActionHelper.buildRedirectUrlAction(ctx.webContext(), "https://wordpress.com/wp-login.php?action=logout")));

        super.internalInit(forceReinit);
    }
}
