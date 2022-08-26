package org.pac4j.core.util;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.WebContextHelper;
import org.pac4j.core.exception.http.*;

/**
 * Helper to build the appropriate {@link HttpAction}.
 *
 * @author Jerome LELEU
 * @since 4.0.0
 */
public final class HttpActionHelper {

    private static boolean useModernHttpCodes = true;

    private static boolean alwaysUse401ForUnauthenticated = true;

    /**
     * Build the action for unauthenticated users.
     *
     * @param context the web context
     * @return the appropriate HTTP action
     */
    public static HttpAction buildUnauthenticatedAction(final WebContext context) {
        final var hasHeader = context.getResponseHeader(HttpConstants.AUTHENTICATE_HEADER).isPresent();
        if (alwaysUse401ForUnauthenticated) {
            // add the WWW-Authenticate header to be compliant with the HTTP spec if it does not already exist
            if (!hasHeader) {
                context.setResponseHeader(HttpConstants.AUTHENTICATE_HEADER, HttpConstants.BEARER_HEADER_PREFIX + "realm=\"pac4j\"");
            }
            return new UnauthorizedAction();
        } else {
            if (hasHeader) {
                return new UnauthorizedAction();
            } else {
                return new ForbiddenAction();
            }
        }
    }

    /**
     * Build the appropriate redirection action for a location.
     *
     * @param context the web context
     * @param location the location
     * @return the appropriate redirection action
     */
    public static RedirectionAction buildRedirectUrlAction(final WebContext context, final String location) {
        if (WebContextHelper.isPost(context) && useModernHttpCodes) {
            return new SeeOtherAction(location);
        } else {
            return new FoundAction(location);
        }
    }

    /**
     * Build the appropriate redirection action for a content which is a form post.
     *
     * @param context the web context
     * @param content the content
     * @return the appropriate redirection action
     */
    public static RedirectionAction buildFormPostContentAction(final WebContext context, final String content) {
        return new OkAction(content);
    }

    /**
     * Build a form POST content from the web context.
     *
     * @param context the web context
     * @return the form POST content
     */
    public static String buildFormPostContent(final WebContext context) {
        final var requestedUrl = context.getFullRequestURL();
        final var parameters = context.getRequestParameters();
        final var buffer = new StringBuilder();
        buffer.append("<html>\n");
        buffer.append("<body>\n");
        buffer.append("<form action=\"" + escapeHtml(requestedUrl) + "\" name=\"f\" method=\"post\">\n");
        if (parameters != null) {
            for (final var entry : parameters.entrySet()) {
                final var values = entry.getValue();
                if (values != null && values.length > 0) {
                    buffer.append("<input type='hidden' name=\"" + escapeHtml(entry.getKey()) + "\" value=\"" + values[0] + "\" />\n");
                }
            }
        }
        buffer.append("<input value='POST' type='submit' />\n");
        buffer.append("</form>\n");
        buffer.append("<script type='text/javascript'>document.forms['f'].submit();</script>\n");
        buffer.append("</body>\n");
        buffer.append("</html>\n");
        return buffer.toString();
    }

    protected static String escapeHtml(final String s) {
        return s.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll("\"", "&quot;");
    }

    public static boolean isUseModernHttpCodes() {
        return useModernHttpCodes;
    }

    public static void setUseModernHttpCodes(final boolean useModernHttpCodes) {
        HttpActionHelper.useModernHttpCodes = useModernHttpCodes;
    }

    public static boolean isAlwaysUse401ForUnauthenticated() {
        return alwaysUse401ForUnauthenticated;
    }

    public static void setAlwaysUse401ForUnauthenticated(final boolean alwaysUse401ForUnauthenticated) {
        HttpActionHelper.alwaysUse401ForUnauthenticated = alwaysUse401ForUnauthenticated;
    }
}
