package org.pac4j.core.redirect;

import org.pac4j.core.client.Client;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.util.CommonHelper;

import java.util.Map;

/**
 * Indicates the action when the {@link Client} requires a redirection to achieve user authentication. Valid redirection
 * types are:
 * <ul>
 * <li>REDIRECT (HTTP 302)</li>
 * <li>SUCCESS (HTTP 200)</li>
 * </ul>
 *
 * @author Michael Remond
 * @since 1.5.0
 */
public class RedirectAction {

    public enum RedirectType {
        REDIRECT, SUCCESS
    }

    private RedirectType type;

    private String location;

    private String content;

    private RedirectAction() {}

    public static RedirectAction redirect(final String location) {
        RedirectAction action = new RedirectAction();
        action.type = RedirectType.REDIRECT;
        action.location = location;
        return action;
    }

    public static RedirectAction success(final String content) {
        RedirectAction action = new RedirectAction();
        action.type = RedirectType.SUCCESS;
        action.content = content;
        return action;
    }

    public static RedirectAction post(final String location, final Map<String, String> data) {
        RedirectAction action = new RedirectAction();
        action.type = RedirectType.SUCCESS;
        final StringBuilder buffer = new StringBuilder();
        buffer.append("<html>\n");
        buffer.append("<body>\n");
        buffer.append("<form action=\"" + escapeHtml(location) + "\" name=\"f\" method=\"post\">\n");
        if (data != null) {
            for (final Map.Entry<String, String> entry : data.entrySet()) {
                buffer.append("<input type='hidden' name=\"" + escapeHtml(entry.getKey()) + "\" value=\"" + entry.getValue() + "\" />\n");
            }
        }
        buffer.append("<input value='POST' type='submit' />\n");
        buffer.append("</form>\n");
        buffer.append("<script type='text/javascript'>document.forms['f'].submit();</script>\n");
        buffer.append("</body>\n");
        buffer.append("</html>\n");
        action.content = buffer.toString();
        return action;
    }

    protected static String escapeHtml(final String s) {
        return s.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll("\"", "&quot;");
    }

    /**
     * Perform a {@link RedirectAction} on the web context.
     *
     * @param context the web context
     * @return the performed {@link HttpAction}
     */
    public HttpAction perform(final WebContext context) {
        if (type == RedirectAction.RedirectType.REDIRECT) {
            return HttpAction.redirect(context, location);
        } else {
            return HttpAction.ok(context, content);
        }
    }

    public RedirectType getType() {
        return this.type;
    }

    public String getLocation() {
        return this.location;
    }

    public String getContent() {
        return this.content;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "type", type, "location", location, "content", content);
    }
}
