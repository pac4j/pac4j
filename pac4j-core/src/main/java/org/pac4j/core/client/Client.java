package org.pac4j.core.client;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.profile.factory.ProfileManagerFactory;

import java.util.Optional;

/**
 * <p>This interface is the core class of the library. It represents an authentication mechanism to validate user's credentials and
 * retrieve his user profile.</p>
 * <p>Clients can be "indirect": in that case, credentials are not provided with the HTTP request, but the user must be redirected to
 * an identity provider to perform login, the original requested url being saved and restored after the authentication process is done.</p>
 * <p>The {@link #getRedirectionAction(WebContext, SessionStore)} method is called to get the redirection to the identity provider,
 * the {@link #getCredentials(WebContext, SessionStore, ProfileManagerFactory)} method is used to retrieve the credentials provided
 * by the remote identity provider and the {@link #getUserProfile(Credentials, WebContext, SessionStore)} method is called
 * to get the user profile from the identity provider and based on the provided credentials.</p>
 * <p>Clients can be "direct": in that case, credentials are provided along with the HTTP request and validated by the application.</p>
 * <p>The {@link #getRedirectionAction(WebContext, SessionStore)} method is not used,
 * the {@link #getCredentials(WebContext, SessionStore, ProfileManagerFactory)} method is used to retrieve and validate the credentials
 * provided and the {@link #getUserProfile(Credentials, WebContext, SessionStore)} method is called
 * to get the user profile from the appropriate system.</p>
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public interface Client {

    /**
     * Get the name of the client.
     *
     * @return the name of the client
     */
    String getName();

    /**
     * <p>Return the redirection action to the authentication provider (indirect clients).</p>
     *
     * @param context the current web context
     * @param sessionStore the session store
     * @param profileManagerFactory the profile manager factory
     * @return the redirection to perform (optional)
     */
    Optional<RedirectionAction> getRedirectionAction(WebContext context, SessionStore sessionStore,
                                                     ProfileManagerFactory profileManagerFactory);

    /**
     * <p>Get the credentials from the web context. If no validation was made remotely (direct client), credentials must be validated at
     * this step.</p>
     *
     * @param context the current web context
     * @param sessionStore the session store
     * @param profileManagerFactory the profile manager factory
     * @return the credentials (optional)
     */
    Optional<Credentials> getCredentials(WebContext context, SessionStore sessionStore,
                                         ProfileManagerFactory profileManagerFactory);

    /**
     * Get the user profile based on the provided credentials.
     *
     * @param credentials credentials
     * @param context web context
     * @param sessionStore the session store
     * @return the user profile (optional)
     */
    Optional<UserProfile> getUserProfile(Credentials credentials, WebContext context, SessionStore sessionStore);

    /**
     * Renew the user profile.
     *
     * @param profile the user profile
     * @param context the current web context
     * @param sessionStore the session store
     * @return the renewed user profile (optional).
     */
    Optional<UserProfile> renewUserProfile(UserProfile profile, WebContext context, SessionStore sessionStore);

    /**
     * <p>Return the logout action (indirect clients).</p>
     *
     * @param context the current web context
     * @param sessionStore the session store
     * @param profileManagerFactory the profile manager factory
     * @param currentProfile the currentProfile
     * @param targetUrl the target url after logout
     * @return the redirection to perform (optional)
     */
    Optional<RedirectionAction> getLogoutAction(WebContext context, SessionStore sessionStore,
                                                ProfileManagerFactory profileManagerFactory,
                                                UserProfile currentProfile, String targetUrl);
}
