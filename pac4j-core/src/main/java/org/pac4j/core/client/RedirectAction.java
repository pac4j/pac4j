package org.pac4j.core.client;

/**
 * Indicates the action when the {@link Client} requires a redirection to achieve user authentication. Valid redirection
 * type are :
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
        return "[type: " + type + ", location: " + location + ", content: " + content + "]";
    }
}
