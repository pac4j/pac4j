package org.pac4j.core.engine;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.http.HttpActionAdapter;
import org.pac4j.core.profile.ProfileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

import static org.pac4j.core.util.CommonHelper.*;

/**
 * <p>Default application logout logic:</p>
 * <p>After logout, the user is redirected to the url defined by the <code>url</code> request parameter if it matches the <code>logoutUrlPattern</code>.
 * Or the user is redirected to the <code>defaultUrl</code> if it is defined. Otherwise, a blank page is displayed.</p>
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class DefaultApplicationLogoutLogic<R extends Object> implements ApplicationLogoutLogic<R> {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public R perform(final WebContext context, final Config config, final HttpActionAdapter<R> httpActionAdapter,
                       final String defaultUrl, final String inputLogoutUrlPattern) {

        logger.debug("Perfoming application logout");

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
        manager.logout();
        postLogout(context);

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
    protected void postLogout(final WebContext context) {
    }
}
