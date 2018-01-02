package org.pac4j.core.client;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.redirect.RedirectAction;

/**
 * <p>This interface is the core class of the library. It represents an authentication mechanism to validate user's credentials and
 * retrieve his user profile.</p>
 * <p>Clients can be "indirect": in that case, credentials are not provided with the HTTP request, but the user must be redirected to
 * an identity provider to perform login, the original requested url being saved and restored after the authentication process is done.</p>
 * <p>The {@link #redirect(WebContext)} method is called to redirect the user to the identity provider,
 * the {@link #getCredentials(WebContext)} method is used to retrieve the credentials provided by the remote identity provider and
 * the {@link #getUserProfile(Credentials, WebContext)} method is called to get the user profile from the identity provider and based
 * on the provided credentials.</p>
 * <p>Clients can be "direct": in that case, credentials are provided along with the HTTP request and validated by the application.</p>
 * <p>The {@link #redirect(WebContext)} method is not used, the {@link #getCredentials(WebContext)} method is used to retrieve
 * and validate the credentials provided and the {@link #getUserProfile(Credentials, WebContext)} method is called to get the user profile
 * from the appropriate system.</p>
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public interface Client<C extends Credentials, U extends CommonProfile> {

    /**
     * Get the name of the client.
     *
     * @return the name of the client
     */
    String getName();

    /**
     * <p>Redirect to the authentication provider for an indirect client.</p>
     *
     * @param context the current web context
     * @return the performed redirection
     */
    HttpAction redirect(WebContext context);

    /**
     * <p>Get the credentials from the web context. If no validation was made remotely (direct client), credentials must be validated at
     * this step.</p>
     *
     * @param context the current web context
     * @return the credentials
     */
    C getCredentials(WebContext context);

    /**
     * Get the user profile based on the provided credentials.
     *
     * @param credentials credentials
     * @param context web context
     * @return the user profile
     */
    U getUserProfile(C credentials, WebContext context);

    /**
     * <p>Return the logout action (indirect clients).</p>
     *
     * @param context the current web context
     * @param currentProfile the currentProfile
     * @param targetUrl the target url after logout
     * @return the redirection
     */
    RedirectAction getLogoutAction(WebContext context, U currentProfile, String targetUrl);
}
