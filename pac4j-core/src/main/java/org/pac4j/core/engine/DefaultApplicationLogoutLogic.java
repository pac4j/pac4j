package org.pac4j.core.engine;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.http.HttpActionAdapter;
import org.pac4j.core.profile.ProfileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;
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
public class DefaultApplicationLogoutLogic<R, C extends WebContext> implements ApplicationLogoutLogic<R, C> {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private Function<C, ProfileManager> profileManagerFactory = context -> new ProfileManager(context);
    private boolean killSession;

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
        final ProfileManager manager = getProfileManager(context);
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
    protected void postLogout(final C context) {
        if (this.killSession) {
            context.invalidationSession();
        }
    }

    /**
     * Given a webcontext generate a profileManager for it.
     * Can be overridden for custom profile manager implementations
     * @param context the web context
     * @return profile manager implementation built from the context
     */
    protected ProfileManager getProfileManager(final C context) {
        return profileManagerFactory.apply(context);
    }

    public boolean isKillSession() {
        return killSession;
    }

    public void setKillSession(boolean killSession) {
        this.killSession = killSession;
    }

    public Function<C, ProfileManager> getProfileManagerFactory() {
        return profileManagerFactory;
    }

    public void setProfileManagerFactory(final Function<C, ProfileManager> factory) {
        this.profileManagerFactory = factory;
    }

}
