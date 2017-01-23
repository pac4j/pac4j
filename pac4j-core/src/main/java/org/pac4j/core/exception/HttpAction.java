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
public class HttpAction extends Exception {
    
    private static final long serialVersionUID = -3959659239684160075L;
    
    protected int code;
    
    private HttpAction(final String message, final int code) {
        super(message);
        this.code = code;
    }

    /**
     * Build a response with message and status.
     *
     * @param message message
     * @param status the HTTP status
     * @param context context
     * @return an HTTP response
     */
    public static HttpAction status(final String message, final int status, final WebContext context) {
        context.setResponseStatus(status);
        return new HttpAction(message, status);
    }

    /**
     * Build a redirection.
     * 
     * @param message message
     * @param context context
     * @param url url
     * @return an HTTP redirection
     */
    public static HttpAction redirect(final String message, final WebContext context, final String url) {
        context.setResponseHeader(HttpConstants.LOCATION_HEADER, url);
        context.setResponseStatus(HttpConstants.TEMP_REDIRECT);
        return new HttpAction(message, HttpConstants.TEMP_REDIRECT);
    }
    
    /**
     * Build an HTTP Ok without any content.
     * 
     * @param message message
     * @param context context
     * @return an HTTP ok
     */
    public static HttpAction ok(final String message, final WebContext context) {
        return ok(message, context, "");
    }
    
    /**
     * Build an HTTP Ok.
     * 
     * @param message message
     * @param context context
     * @param content content
     * @return an HTTP ok
     */
    public static HttpAction ok(final String message, final WebContext context, String content) {
        context.setResponseStatus(HttpConstants.OK);
        context.writeResponseContent(content);
        return new HttpAction(message, HttpConstants.OK);
    }
    
    /**
     * Build a basic auth popup credentials.
     * 
     * @param message message
     * @param context context
     * @param realmName realm name
     * @return a basic auth popup credentials
     */
    public static HttpAction unauthorized(final String message, final WebContext context, final String realmName) {
        if (CommonHelper.isNotBlank(realmName)) {
            context.setResponseHeader(HttpConstants.AUTHENTICATE_HEADER, "Basic realm=\"" + realmName + "\"");
        }
        context.setResponseStatus(HttpConstants.UNAUTHORIZED);
        return new HttpAction(message, HttpConstants.UNAUTHORIZED);
    }

    /**
     * Build a digest auth popup credentials.
     *
     * @param message message
     * @param context context
     * @param realmName realm name
     * @param qop qop
     * @param nonce nonce
     * @return a digest auth popup credentials
     */
    public static HttpAction unauthorizedDigest(final String message, final WebContext context, final String realmName, final String qop, final String nonce) {
        if (CommonHelper.isNotBlank(realmName)) {
            context.setResponseHeader(HttpConstants.AUTHENTICATE_HEADER, "Digest realm=\"" + realmName + "\", qop=\"" + qop + "\", nonce=\"" + nonce + "\"");
        }
        context.setResponseStatus(HttpConstants.UNAUTHORIZED);
        return new HttpAction(message, HttpConstants.UNAUTHORIZED);
    }
    
    /**
     * Build a forbidden response.
     * 
     * @param message message
     * @param context context
     * @return a forbidden response
     */
    public static HttpAction forbidden(final String message, final WebContext context) {
        context.setResponseStatus(HttpConstants.FORBIDDEN);
        return new HttpAction(message, HttpConstants.FORBIDDEN);
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
        return CommonHelper.toString(HttpAction.class, "code", this.code);
    }
}
