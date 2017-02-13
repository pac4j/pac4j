package org.pac4j.oauth.client;

import com.github.scribejava.apis.Foursquare2Api;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.oauth.profile.foursquare.FoursquareProfile;
import org.pac4j.oauth.profile.foursquare.FoursquareProfileCreator;
import org.pac4j.oauth.profile.foursquare.FoursquareProfileDefinition;

/**
 * <p>This class is the OAuth client to authenticate users in Foursquare.
 * It returns a {@link org.pac4j.oauth.profile.foursquare.FoursquareProfile}.</p>
 * <p>More information at https://developer.foursquare.com/overview/auth.html</p>
 *
 * @author Alexey Ogarkov
 * @since 1.5.0
 */
public class FoursquareClient extends OAuth20Client<FoursquareProfile>{

    public FoursquareClient() {}

    public FoursquareClient(String key, String secret) {
        setKey(key);
        setSecret(secret);
    }

    @Override
    protected void clientInit(final WebContext context) {
        configuration.setApi(Foursquare2Api.instance());
        configuration.setProfileDefinition(new FoursquareProfileDefinition());
        configuration.setScope("user");
        setConfiguration(configuration);
        defaultProfileCreator(new FoursquareProfileCreator(configuration));
        defaultLogoutActionBuilder((ctx, profile, targetUrl) -> RedirectAction.redirect("https://www.foursquare.com/logout"));

        super.clientInit(context);
    }
}
