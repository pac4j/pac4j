package org.pac4j.core.redirect;

import org.pac4j.core.client.Client;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.util.CommonHelper;

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

    private RedirectAction() {

    }

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

    /**
     * Perform a {@link RedirectAction} on the web context.
     *
     * @param context the web context
     * @return the performed {@link HttpAction}
     */
    public HttpAction perform(final WebContext context) {
        if (type == RedirectAction.RedirectType.REDIRECT) {
            return HttpAction.redirect("redirection via 302", context, location);
        } else {
            return HttpAction.ok("redirection via 200", context, content);
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
        return CommonHelper.toString(this.getClass(), "type", type, "location", location, "content", content);
    }
}
