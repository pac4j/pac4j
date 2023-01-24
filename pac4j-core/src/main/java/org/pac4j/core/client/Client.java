package org.pac4j.core.client;

import org.pac4j.core.context.CallContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.profile.UserProfile;

import java.util.Optional;

/**
 * This interface is the core class of the library.
 * It represents an authentication mechanism to validate user's credentials
 * and retrieve his user profile.
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
     * @param ctx the current context
     * @return the redirection to perform (optional)
     */
    Optional<RedirectionAction> getRedirectionAction(CallContext ctx);

    /**
     * Get the credentials from the context.
     *
     * @param ctx the current context
     * @return the credentials (optional)
     */
    Optional<Credentials> getCredentials(CallContext ctx);

    /**
     * Validate the credentials.
     *
     * @param ctx the current context
     * @param credentials the authentication credentials
     * @return the credentials (optional)
     */
    Optional<Credentials> validateCredentials(CallContext ctx, Credentials credentials);

    /**
     * Get the user profile based on the credentials.
     *
     * @param ctx the context
     * @param credentials the authentication credentials
     * @return the user profile (optional)
     */
    Optional<UserProfile> getUserProfile(CallContext ctx, Credentials credentials);

    /**
     * Renew the user profile.
     *
     * @param ctx the current context
     * @param profile the user profile
     * @return the renewed user profile (optional).
     */
    Optional<UserProfile> renewUserProfile(CallContext ctx, UserProfile profile);

    /**
     * Process the logout.
     *
     * @param ctx the current context
     * @param credentials the logout credentials
     * @return the resulting HTTP action
     */
    HttpAction processLogout(CallContext ctx, Credentials credentials);

    /**
     * <p>Return the logout action (indirect clients).</p>
     *
     * @param ctx the current context
     * @param currentProfile the currentProfile
     * @param targetUrl the target url after logout
     * @return the redirection to perform (optional)
     */
    Optional<RedirectionAction> getLogoutAction(CallContext ctx, UserProfile currentProfile, String targetUrl);
}
