package org.pac4j.core.exception;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;

/**
 * The extra HTTP action, already performed on the web context.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class HttpAction extends TechnicalException {

    private static final long serialVersionUID = -3959659239684160075L;

    protected int code;

    protected HttpAction(final int code) {
        super("Performing a " + code + " HTTP action");
        this.code = code;
    }

    /**
     * Build a response with status.
     *
     * @param status the HTTP status
     * @param context context
     * @return an HTTP response
     */
    public static HttpAction status(final int status, final WebContext context) {
        context.setResponseStatus(status);
        return new HttpAction(status);
    }

    /**
     * Build a redirection.
     *
     * @param context context
     * @param url url
     * @return an HTTP redirection
     */
    public static HttpAction redirect(final WebContext context, final String url) {
        context.setResponseHeader(HttpConstants.LOCATION_HEADER, url);
        context.setResponseStatus(HttpConstants.TEMP_REDIRECT);
        return new HttpAction(HttpConstants.TEMP_REDIRECT);
    }

    /**
     * Build an HTTP Ok.
     *
     * @param context context
     * @param content content
     * @return an HTTP ok
     */
    public static HttpAction ok(final WebContext context, final String content) {
        context.setResponseStatus(HttpConstants.OK);
        context.writeResponseContent(content);
        return new HttpAction(HttpConstants.OK);
    }

    /**
     * Build an HTTP No content.
     *
     * @param context context
     * @return an HTTP No content
     */
    public static HttpAction noContent(final WebContext context) {
        context.setResponseStatus(HttpConstants.NO_CONTENT);
        context.writeResponseContent("");
        return new HttpAction(HttpConstants.NO_CONTENT);
    }

    /**
     * Build a basic auth popup credentials.
     *
     * @param context context
     * @return a basic auth popup credentials
     */
    public static HttpAction unauthorized(final WebContext context) {
        context.setResponseStatus(HttpConstants.UNAUTHORIZED);
        return new HttpAction(HttpConstants.UNAUTHORIZED);
    }

    /**
     * Build a forbidden response.
     *
     * @param context context
     * @return a forbidden response
     */
    public static HttpAction forbidden(final WebContext context) {
        context.setResponseStatus(HttpConstants.FORBIDDEN);
        return new HttpAction(HttpConstants.FORBIDDEN);
    }

    /**
     * Return the HTTP code.
     *
     * @return the HTTP code
     */
    public int getCode() {
        return this.code;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(HttpAction.class, "code", this.code);
    }
}
