package org.pac4j.core.client;

import org.pac4j.core.client.RedirectAction.RedirectType;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.http.AjaxRequestResolver;
import org.pac4j.core.http.DefaultAjaxRequestResolver;
import org.pac4j.core.http.DefaultCallbackUrlResolver;
import org.pac4j.core.http.CallbackUrlResolver;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;

/**
 * <p>This class is the default indirect (with redirection, stateful) implementation of an authentication client (whatever the mechanism).</p>
 * <p>The callback url is managed via the {@link #setCallbackUrl(String)} and {@link #getCallbackUrl()} methods. The way the callback url
 * is finally computed is done by the {@link #callbackUrlResolver} which returns by default the provided {@link #callbackUrl}.</p>
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public abstract class IndirectClient<C extends Credentials, U extends CommonProfile> extends BaseClient<C, U> {

    public final static String ATTEMPTED_AUTHENTICATION_SUFFIX = "$attemptedAuthentication";

    protected String callbackUrl;

    private boolean includeClientNameInCallbackUrl = true;

    private AjaxRequestResolver ajaxRequestResolver = new DefaultAjaxRequestResolver();

    protected CallbackUrlResolver callbackUrlResolver = new DefaultCallbackUrlResolver();

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotBlank("callbackUrl", this.callbackUrl);
        CommonHelper.assertNotNull("callbackUrlResolver", this.callbackUrlResolver);
        CommonHelper.assertNotNull("ajaxRequestResolver", this.ajaxRequestResolver);
    }

    @Override
    public final HttpAction redirect(final WebContext context) throws HttpAction {
        final RedirectAction action = getRedirectAction(context);
        if (action.getType() == RedirectType.REDIRECT) {
            return HttpAction.redirect("redirection via 302", context, action.getLocation());
        } else {
            return HttpAction.ok("redirection via 200", context, action.getContent());
        }
    }

    /**
     * <p>Get the redirectAction computed for this client. All the logic is encapsulated here. It should not be called be directly, the
     * {@link #redirect(WebContext)} should be generally called instead.</p>
     * <p>If an authentication has already been tried for this client and has failed (<code>null</code> credentials) or if the request is an AJAX one,
     * an authorized response (401 HTTP status code) is returned instead of a redirection.</p>
     *
     * @param context context
     * @return the redirection action
     * @throws HttpAction requires an additional HTTP action
     */
    public final RedirectAction getRedirectAction(final WebContext context) throws HttpAction {
        init(context);

        // it's an AJAX request -> unauthorized (instead of a redirection)
        if (ajaxRequestResolver.isAjax(context)) {
            logger.info("AJAX request detected -> returning 401");
            cleanRequestedUrl(context);
            throw HttpAction.unauthorized("AJAX request -> 401", context, null);
        }
        // authentication has already been tried -> unauthorized
        final String attemptedAuth = (String) context.getSessionAttribute(getName() + ATTEMPTED_AUTHENTICATION_SUFFIX);
        if (CommonHelper.isNotBlank(attemptedAuth)) {
            cleanAttemptedAuthentication(context);
            cleanRequestedUrl(context);
            throw HttpAction.unauthorized("authentication already tried -> forbidden", context, null);
        }

        return retrieveRedirectAction(context);
    }

    /**
     * Retrieve the redirect action.
     *
     * @param context the web context
     * @return the redirection action
     * @throws HttpAction requires a specific HTTP action if necessary
     */
    protected abstract RedirectAction retrieveRedirectAction(final WebContext context) throws HttpAction;

    /**
     * <p>Get the credentials from the web context. In some cases, a {@link HttpAction} may be thrown:</p>
     * <ul>
     * <li>if the <code>CasClient</code> receives a logout request, it returns a 200 HTTP status code</li>
     * <li>for the <code>IndirectBasicAuthClient</code>, if no credentials are sent to the callback url, an unauthorized response (401 HTTP status
     * code) is returned to request credentials through a popup.</li>
     * </ul>
     *
     * @param context the current web context
     * @return the credentials
     * @throws HttpAction whether an additional HTTP action is required
     */
    @Override
    public final C getCredentials(final WebContext context) throws HttpAction {
        init(context);
        final C credentials = retrieveCredentials(context);
        // no credentials -> save this authentication has already been tried and failed
        if (credentials == null) {
            context.setSessionAttribute(getName() + ATTEMPTED_AUTHENTICATION_SUFFIX, "true");
        } else {
            cleanAttemptedAuthentication(context);
        }
        return credentials;
    }

    private void cleanRequestedUrl(final WebContext context) {
        context.setSessionAttribute(Pac4jConstants.REQUESTED_URL, "");
    }

    private void cleanAttemptedAuthentication(final WebContext context) {
        context.setSessionAttribute(getName() + ATTEMPTED_AUTHENTICATION_SUFFIX, "");
    }

    public String computeFinalCallbackUrl(final WebContext context) {
        return callbackUrlResolver.compute(callbackUrl, context);
    }

    /**
     * Return the state parameter required by some security protocols like SAML or OAuth.
     * 
     * @param webContext web context
     * @return the state
     */
    protected String getStateParameter(WebContext webContext) {
        throw new UnsupportedOperationException("To be implemented in subclasses if required");
    }

    public boolean isIncludeClientNameInCallbackUrl() {
        return this.includeClientNameInCallbackUrl;
    }

    public void setIncludeClientNameInCallbackUrl(final boolean includeClientNameInCallbackUrl) {
        this.includeClientNameInCallbackUrl = includeClientNameInCallbackUrl;
    }

    public void setCallbackUrl(final String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getCallbackUrl() { return this.callbackUrl; }

    public AjaxRequestResolver getAjaxRequestResolver() {
        return ajaxRequestResolver;
    }

    public void setAjaxRequestResolver(final AjaxRequestResolver ajaxRequestResolver) {
        this.ajaxRequestResolver = ajaxRequestResolver;
    }

    public CallbackUrlResolver getCallbackUrlResolver() {
        return callbackUrlResolver;
    }

    public void setCallbackUrlResolver(final CallbackUrlResolver callbackUrlResolver) {
        this.callbackUrlResolver = callbackUrlResolver;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "name", getName(), "callbackUrl", this.callbackUrl,
                "callbackUrlResolver", this.callbackUrlResolver, "ajaxRequestResolver", this.ajaxRequestResolver);
    }
}
