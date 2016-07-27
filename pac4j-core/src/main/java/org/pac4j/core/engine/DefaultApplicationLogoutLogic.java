package org.pac4j.core.engine;

import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.http.HttpActionAdapter;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.regex.Pattern;
import java.util.List;
import static org.pac4j.core.util.CommonHelper.*;


/**
 * <p>Default application logout logic:</p>
 * <p>After logout, the user is redirected to the url defined by the <code>url</code> request parameter if it matches the <code>logoutUrlPattern</code>.
 * Or the user is redirected to the <code>defaultUrl</code> if it is defined. Otherwise, a blank page is displayed.</p>
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class DefaultApplicationLogoutLogic<R, C extends WebContext> implements ApplicationLogoutLogic<R, C> {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public R perform(final C context, final Config config, final HttpActionAdapter<R, C> httpActionAdapter,
                       final String defaultUrl, final String inputLogoutUrlPattern) {

        logger.debug("=== APP LOGOUT ===");

        // default value
        final String logoutUrlPattern;
        if (inputLogoutUrlPattern == null) {
            logoutUrlPattern = Pac4jConstants.DEFAULT_LOGOUT_URL_PATTERN_VALUE;
        } else {
            logoutUrlPattern = inputLogoutUrlPattern;
        }

        // checks
        assertNotNull("context", context);
        assertNotNull("config", config);
        assertNotNull("httpActionAdapter", httpActionAdapter);
        assertNotBlank(Pac4jConstants.LOGOUT_URL_PATTERN, logoutUrlPattern);

        // logic
        final ProfileManager manager = new ProfileManager(context);

        // logout redirection if needed
        // this computation is done just before logout
        final List<CommonProfile> profiles = manager.getAll(true);
        final Clients clients = config.getClients();
        HttpAction redirection = null;

        for(CommonProfile profile : profiles) {
        	String clientName = profile.getClientName();
        	if(clientName != null) {
            	Client<Credentials, CommonProfile> client = clients.findClient(clientName);
            	if(client != null) {
                	redirection = client.logoutRedirect(context);
            	}
        	}
        	if (redirection != null) {
        		break;
        	}
        }

        // logout logic
        manager.logout();
        postLogout(context);

        // if we need a redirection (from client), then we don't care about the rest
        if (redirection != null) {
        	return httpActionAdapter.adapt(redirection.getCode(), context);
        }
        final String url = context.getRequestParameter(Pac4jConstants.URL);
        String redirectUrl = defaultUrl;
        if (url != null && Pattern.matches(logoutUrlPattern, url)) {
            redirectUrl = url;
        }
        logger.debug("redirectUrl: {}", redirectUrl);
        final HttpAction action;
        if (redirectUrl != null) {
            action = HttpAction.redirect("redirect", context, redirectUrl);
        } else {
            action = HttpAction.ok("ok", context);
        }

        return httpActionAdapter.adapt(action.getCode(), context);
    }

    /**
     * Specific post logout action.
     *
     * @param context the web context
     */
    protected void postLogout(final C context) {
    }
}
