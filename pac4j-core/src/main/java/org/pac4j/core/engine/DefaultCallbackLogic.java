package org.pac4j.core.engine;

import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.IndirectClient;
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

import java.util.function.Function;

import static org.pac4j.core.util.CommonHelper.*;

/**
 * <p>Default callback logic:</p>
 * <p>The credentials are extracted from the current request to fetch the user profile (from the identity provider) which is then saved in the web session.
 * Finally, the user is redirected back to the originally requested url (or to the <code>defaultUrl</code>).</p>
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class DefaultCallbackLogic<R, C extends WebContext> implements CallbackLogic<R, C> {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private Function<C, ProfileManager> profileManagerFactory = context -> new ProfileManager(context);

    @Override
    public R perform(final C context, final Config config, final HttpActionAdapter<R, C> httpActionAdapter,
                     final String inputDefaultUrl, final Boolean inputMultiProfile, final Boolean inputRenewSession) {

        logger.debug("=== CALLBACK ===");

        // default values
        final String defaultUrl;
        if (inputDefaultUrl == null) {
            defaultUrl = Pac4jConstants.DEFAULT_URL_VALUE;
        } else {
            defaultUrl = inputDefaultUrl;
        }
        final boolean multiProfile;
        if (inputMultiProfile == null) {
            multiProfile = false;
        } else {
            multiProfile = inputMultiProfile;
        }
        final boolean renewSession;
        if (inputRenewSession == null) {
            renewSession = true;
        } else {
            renewSession = inputRenewSession;
        }

        // checks
        assertNotNull("context", context);
        assertNotNull("config", config);
        assertNotNull("httpActionAdapter", httpActionAdapter);
        assertNotBlank(Pac4jConstants.DEFAULT_URL, defaultUrl);
        final Clients clients = config.getClients();
        assertNotNull("clients", clients);

        // logic
        final Client client = clients.findClient(context);
        logger.debug("client: {}", client);
        assertNotNull("client", client);
        assertTrue(client instanceof IndirectClient, "only indirect clients are allowed on the callback url");

        HttpAction action;
        try {
            final Credentials credentials = client.getCredentials(context);
            logger.debug("credentials: {}", credentials);

            final CommonProfile profile = client.getUserProfile(credentials, context);
            logger.debug("profile: {}", profile);
            saveUserProfile(context, profile, multiProfile, renewSession);
            action = redirectToOriginallyRequestedUrl(context, defaultUrl);

        } catch (final HttpAction e) {
            logger.debug("extra HTTP action required in callback: {}", e.getCode());
            action = e;
        }

        return httpActionAdapter.adapt(action.getCode(), context);
    }

    protected void saveUserProfile(final C context, final CommonProfile profile,
                                   final boolean multiProfile, final boolean renewSession) {
        final ProfileManager manager = getProfileManager(context);
        if (profile != null) {
            manager.save(true, profile, multiProfile);
            if (renewSession) {
                renewSession(context);
            }
        }
    }

    protected void renewSession(final C context) {}

    protected HttpAction redirectToOriginallyRequestedUrl(final C context, final String defaultUrl) {
        final String requestedUrl = (String) context.getSessionAttribute(Pac4jConstants.REQUESTED_URL);
        String redirectUrl = defaultUrl;
        if (isNotBlank(requestedUrl)) {
            context.setSessionAttribute(Pac4jConstants.REQUESTED_URL, null);
            redirectUrl = requestedUrl;
        }
        logger.debug("redirectUrl: {}", redirectUrl);
        return HttpAction.redirect("redirect", context, redirectUrl);
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

    public Function<C, ProfileManager> getProfileManagerFactory() {
        return profileManagerFactory;
    }

    public void setProfileManagerFactory(final Function<C, ProfileManager> factory) {
        this.profileManagerFactory = factory;
    }

}
