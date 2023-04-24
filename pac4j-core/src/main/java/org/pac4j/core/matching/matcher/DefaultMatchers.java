package org.pac4j.core.matching.matcher;

/**
 * The default matchers.
 *
 * @author Jerome LELEU
 * @since 4.0.0
 */
public interface DefaultMatchers {

    /** The "hsts" matcher. */
    String HSTS = "hsts";

    /** The "nosniff" matcher. */
    String NOSNIFF = "nosniff";

    /** The "noframe" matcher. */
    String NOFRAME = "noframe";

    /** The "securityheaders" matcher. */
    String SECURITYHEADERS = "securityheaders";

    /** The "xssprotection" matcher. */
    String XSSPROTECTION = "xssprotection";

    /** The "allowAjaxRequests" matcher. */
    String ALLOW_AJAX_REQUESTS = "allowAjaxRequests";

    /** The "nocache" matcher. */
    String NOCACHE = "nocache";

    /** The "csrfToken" matcher. */
    String CSRF_TOKEN = "csrfToken";

    /** Constant <code>GET="get"</code> */
    String GET = "get";
    /** Constant <code>POST="post"</code> */
    String POST = "post";
    /** Constant <code>PUT="put"</code> */
    String PUT = "put";
    /** Constant <code>DELETE="delete"</code> */
    String DELETE = "delete";

    /** Constant <code>NONE="none"</code> */
    String NONE = "none";
}
