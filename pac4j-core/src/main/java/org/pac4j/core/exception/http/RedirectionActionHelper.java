package org.pac4j.core.exception.http;

import org.pac4j.core.context.ContextHelper;
import org.pac4j.core.context.WebContext;

import java.util.Map;

/**
 * Helper to build {@link RedirectionAction}.
 *
 * @author Jerome LELEU
 * @since 4.0.0
 */
public final class RedirectionActionHelper {

    private static boolean useModernHttpCodes = true;

    /**
     * Build the appropriate redirection action for a location.
     *
     * @param context the web context
     * @param location the location
     * @return the appropriate redirection action
     */
    public static RedirectionAction buildRedirectUrlAction(final WebContext context, final String location) {
        if (ContextHelper.isPost(context) && useModernHttpCodes) {
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
        final String requestedUrl = context.getFullRequestURL();
        final Map<String, String[]> parameters = context.getRequestParameters();
        final StringBuilder buffer = new StringBuilder();
        buffer.append("<html>\n");
        buffer.append("<body>\n");
        buffer.append("<form action=\"" + escapeHtml(requestedUrl) + "\" name=\"f\" method=\"post\">\n");
        if (parameters != null) {
            for (final Map.Entry<String, String[]> entry : parameters.entrySet()) {
                final String[] values = entry.getValue();
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
        RedirectionActionHelper.useModernHttpCodes = useModernHttpCodes;
    }
}
