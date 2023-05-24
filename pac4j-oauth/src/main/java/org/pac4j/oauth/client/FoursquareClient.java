package org.pac4j.oauth.client;

import com.github.scribejava.apis.Foursquare2Api;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.oauth.profile.foursquare.FoursquareProfileCreator;
import org.pac4j.oauth.profile.foursquare.FoursquareProfileDefinition;

import java.util.Optional;

/**
 * <p>This class is the OAuth client to authenticate users in Foursquare.
 * It returns a {@link org.pac4j.oauth.profile.foursquare.FoursquareProfile}.</p>
 * <p>More information at https://developer.foursquare.com/overview/auth.html</p>
 *
 * @author Alexey Ogarkov
 * @since 1.5.0
 */
public class FoursquareClient extends OAuth20Client {

    /**
     * <p>Constructor for FoursquareClient.</p>
     */
    public FoursquareClient() {}

    /**
     * <p>Constructor for FoursquareClient.</p>
     *
     * @param key a {@link String} object
     * @param secret a {@link String} object
     */
    public FoursquareClient(String key, String secret) {
        setKey(key);
        setSecret(secret);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        CommonHelper.assertNotNull("configuration", configuration);
        configuration.setApi(Foursquare2Api.instance());
        configuration.setProfileDefinition(new FoursquareProfileDefinition());
        configuration.setScope("user");
        setProfileCreatorIfUndefined(new FoursquareProfileCreator(configuration, this));
        setLogoutActionBuilderIfUndefined((ctx, profile, targetUrl) ->
            Optional.of(HttpActionHelper.buildRedirectUrlAction(ctx.webContext(), "https://www.foursquare.com/logout")));

        super.internalInit(forceReinit);
    }
}
