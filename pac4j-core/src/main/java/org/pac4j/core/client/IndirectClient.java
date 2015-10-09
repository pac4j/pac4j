/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.pac4j.core.client;

import org.pac4j.core.client.RedirectAction.RedirectType;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.http.AjaxRequestResolver;
import org.pac4j.core.http.DefaultAjaxRequestResolver;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;

/**
 * <p>This class is the default indirect (with redirection, stateful) implementation of an authentication client (whatever the protocol).
 * It has the core concepts:</p>
 * <ul>
 * <li>The callback url is handled through the {@link #setCallbackUrl(String)} and {@link #getCallbackUrl()} methods</li>
 * <li>The concept of "direct" redirection is defined through the {@link #isDirectRedirection()} method: if true, the
 * {@link #redirect(WebContext, boolean)} method will always return the redirection to the provider where as if it's false, the
 * redirection url will be the callback url with an additional parameter: {@link #NEEDS_CLIENT_REDIRECTION_PARAMETER} to require the
 * redirection, which will be handled <b>later</b> in the {@link #getCredentials(WebContext)} method.
 * To force a direct redirection, the {@link #getRedirectAction(WebContext, boolean)} must be used with <code>true</code> for the
 * <code>protectedTarget</code> parameter</li>
 * <li>If you enable "contextual redirects" by using the {@link #setEnableContextualRedirects(boolean)}, you can use relative callback urls
 * which will be completed according to the current host, port and scheme. Disabled by default.</li>
 * </ul>
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public abstract class IndirectClient<C extends Credentials, U extends CommonProfile> extends BaseClient<C, U> {

    public final static String NEEDS_CLIENT_REDIRECTION_PARAMETER = "needs_client_redirection";

    public final static String ATTEMPTED_AUTHENTICATION_SUFFIX = "$attemptedAuthentication";

    protected String callbackUrl;

    private boolean includeClientNameInCallbackUrl = true;

    private boolean enableContextualRedirects = false;

    private AjaxRequestResolver ajaxRequestResolver = new DefaultAjaxRequestResolver();

    public String getContextualCallbackUrl(final WebContext context) {
        return prependHostToUrlIfNotPresent(this.callbackUrl, context);
    }

    /**
     * Define if this client has a direct redirection.
     * 
     * @return if this client has a direct redirection
     */
    protected abstract boolean isDirectRedirection();

    /**
     * <p>Redirect to the authentication provider by updating the WebContext accordingly.</p>
     * <p>Though, if this client requires an indirect redirection, it will return a redirection to the callback url (with an additionnal parameter requesting a
     * redirection). Whatever the kind of client's redirection, the <code>protectedTarget</code> parameter set to <code>true</code> enforces
     * a direct redirection.
     * <p>If an authentication has already been tried for this client and has failed (previous <code>null</code> credentials) and if the target
     * is protected (<code>protectedTarget</code> set to <code>true</code>), a forbidden response (403 HTTP status code) is returned.</p>
     * <p>If the request is an AJAX one, an authorized response (401 HTTP status code) is returned instead of a redirection.</p>
     *
     * @param context the current web context
     * @param protectedTarget whether the target url is protected
     * @throws RequiresHttpAction whether an additional HTTP action is required
     */
    @Override
    public final void redirect(final WebContext context, final boolean protectedTarget)
            throws RequiresHttpAction {
        final RedirectAction action = getRedirectAction(context, protectedTarget);
        if (action.getType() == RedirectType.REDIRECT) {
            context.setResponseStatus(HttpConstants.TEMP_REDIRECT);
            context.setResponseHeader(HttpConstants.LOCATION_HEADER, action.getLocation());
        } else if (action.getType() == RedirectType.SUCCESS) {
            context.setResponseStatus(HttpConstants.OK);
            context.writeResponseContent(action.getContent());
        }
    }

    /**
     * Get the redirectAction computed for this client. All the logic is encapsulated here. It should not be called be directly, the
     * {@link #redirect(WebContext, boolean)} should be generally called instead.
     * 
     * @param context context
     * @param protectedTarget requires authentication
     * @return the redirection action
     * @throws RequiresHttpAction requires an additional HTTP action
     */
    public final RedirectAction getRedirectAction(final WebContext context, final boolean protectedTarget) throws RequiresHttpAction {
        init();
        // it's an AJAX request -> unauthorized (instead of a redirection)
        if (ajaxRequestResolver.isAjax(context)) {
            logger.info("AJAX request detected -> returning 401");
            cleanRequestedUrl(context);
            throw RequiresHttpAction.unauthorized("AJAX request -> 401", context, null);
        }
        // authentication has already been tried
        final String attemptedAuth = (String) context.getSessionAttribute(getName() + ATTEMPTED_AUTHENTICATION_SUFFIX);
        if (CommonHelper.isNotBlank(attemptedAuth)) {
            context.setSessionAttribute(getName() + ATTEMPTED_AUTHENTICATION_SUFFIX, null);
            // protected target -> forbidden
            if (protectedTarget) {
                logger.error("authentication already tried and protected target -> forbidden");
                cleanRequestedUrl(context);
                throw RequiresHttpAction.forbidden("authentication already tried -> forbidden", context);
            }
        }
        // it's a direct redirection or force the redirection because the target is protected -> return the real redirection
        if (isDirectRedirection() || protectedTarget) {
            return retrieveRedirectAction(context);
        } else {
            // return an intermediate url which is the callback url with a specific parameter requiring redirection
            final String intermediateUrl = CommonHelper.addParameter(getContextualCallbackUrl(context),
                    NEEDS_CLIENT_REDIRECTION_PARAMETER, "true");
            return RedirectAction.redirect(intermediateUrl);
        }
    }

    private void cleanRequestedUrl(final WebContext context) {
        context.setSessionAttribute(Pac4jConstants.REQUESTED_URL, null);
    }

    /**
     * Return the redirection url to the provider, requested from an anonymous page.
     *
     * @param context the current web context
     * @return the redirection url to the provider.
     */
    public String getRedirectionUrl(final WebContext context) {
        try {
            return getRedirectAction(context, false).getLocation();
        } catch (final RequiresHttpAction e) {
            return null;
        }
    }

    protected abstract RedirectAction retrieveRedirectAction(final WebContext context);

    /**
     * <p>Get the credentials from the web context. In some cases, a {@link RequiresHttpAction} may be thrown instead:</p>
     * <ul>
     * <li>if this client requires an indirect redirection, the redirection will be actually performed by these method and not by the
     * {@link #redirect(WebContext, boolean)} one (302 HTTP status code)</li>
     * <li>if the <code>CasClient</code> receives a logout request, it returns a 200 HTTP status code</li>
     * <li>for the <code>IndirectBasicAuthClient</code>, if no credentials are sent to the callback url, an unauthorized response (401 HTTP status
     * code) is returned to request credentials through a popup.</li>
     * </ul>
     *
     * @param context the current web context
     * @return the credentials
     * @throws RequiresHttpAction whether an additional HTTP action is required
     */
    @Override
    public final C getCredentials(final WebContext context) throws RequiresHttpAction {
        init();
        final String value = context.getRequestParameter(NEEDS_CLIENT_REDIRECTION_PARAMETER);
        // needs redirection -> return the redirection url
        if (CommonHelper.isNotBlank(value)) {
            final RedirectAction action = retrieveRedirectAction(context);
            final String message = "Needs client redirection";
            if (action.getType() == RedirectType.SUCCESS) {
                throw RequiresHttpAction.ok(message, context, action.getContent());
            } else {
                // it's a redirect
                throw RequiresHttpAction.redirect(message, context, action.getLocation());
            }
        } else {
            // else get the credentials
            final C credentials = retrieveCredentials(context);
            // no credentials -> save this authentication has already been tried and failed
            if (credentials == null) {
                context.setSessionAttribute(getName() + ATTEMPTED_AUTHENTICATION_SUFFIX, "true");
            } else {
                context.setSessionAttribute(getName() + ATTEMPTED_AUTHENTICATION_SUFFIX, null);
            }
            return credentials;
        }
    }

    protected abstract C retrieveCredentials(final WebContext context) throws RequiresHttpAction;

    /**
     * Returns if contextual redirects are enabled for this client
     * 
     * @return if contextual redirects are enabled for this client
     */
    public boolean isEnableContextualRedirects() {
        return this.enableContextualRedirects;
    }

    /**
     * Sets whether contextual redirects are enabled for this client
     * 
     * @param enableContextualRedirects enable contextual redirects
     */
    public void setEnableContextualRedirects(final boolean enableContextualRedirects) {
        this.enableContextualRedirects = enableContextualRedirects;
    }

    protected String prependHostToUrlIfNotPresent(final String url, final WebContext webContext) {
        if (webContext != null && this.enableContextualRedirects && url != null && !url.startsWith("http://")
                && !url.startsWith("https://")) {
            final StringBuilder sb = new StringBuilder();

            sb.append(webContext.getScheme()).append("://").append(webContext.getServerName());

            if (webContext.getServerPort() != HttpConstants.DEFAULT_PORT) {
                sb.append(":").append(webContext.getServerPort());
            }

            sb.append(url.startsWith("/") ? url : "/" + url);

            return sb.toString();
        }

        return url;
    }

    /**
     * Return the state parameter required by some security protocols like SAML or OAuth.
     * 
     * @param webContext web context
     * @return the state
     */
    protected String getStateParameter(WebContext webContext) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Returns if the client name should be implicitly added to the callback url if it is not already specified
     *
     * @return if the client name should be implicitly added to the callback url if it is not already specified
     */
    public boolean isIncludeClientNameInCallbackUrl() {
        return this.includeClientNameInCallbackUrl;
    }

    /**
     * Sets whether the client name should be implicitly added to the callback url for this client.
     *
     * @param includeClientNameInCallbackUrl enable inclusion of the client name in the callback url.
     */
    public void setIncludeClientNameInCallbackUrl(final boolean includeClientNameInCallbackUrl) {
        this.includeClientNameInCallbackUrl = includeClientNameInCallbackUrl;
    }

    public void setCallbackUrl(final String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getCallbackUrl() {
        return this.callbackUrl;
    }

    public AjaxRequestResolver getAjaxRequestResolver() {
        return ajaxRequestResolver;
    }

    public void setAjaxRequestResolver(AjaxRequestResolver ajaxRequestResolver) {
        this.ajaxRequestResolver = ajaxRequestResolver;
    }
}
